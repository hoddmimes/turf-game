package com.hoddmimes.turf.server.services.suspicioustakes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.transform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.SuspiciousTakesConfiguration;
import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class SuspiciousTakesService implements TurfServiceInterface
{
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final long PRUNE_INTERVAL_MS = 3600_000L;      // Prune user takes once an hour
    private static final long TAKE_MAX_AGE_MS = 24L * 3600_000L;  // Takes older than 24 hrs are of no interest

    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private SuspiciousTakesConfiguration mConfig;
    private EventFilterNewZoneTakeOver mZoneFilter = null;
    private Map<Integer, LastTake> mLatestUserTakes; // Key == User Id
    private long mNextPruneTime;
    private NumberFormat mNBF;

    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mTurfIf = pTurfServerInterface;
        mConfig = pTurfServerInterface.getServerConfiguration().getSuspiciousTakesConfiguration();
        mLogger = LogManager.getLogger(this.getClass().getName());
        mZoneFilter = new EventFilterNewZoneTakeOver();
        mLatestUserTakes = new HashMap<>();
        mNextPruneTime = System.currentTimeMillis() + PRUNE_INTERVAL_MS;

        mNBF = NumberFormat.getInstance();
        mNBF.setMaximumFractionDigits(1);
        mNBF.setMinimumFractionDigits(1);
        mNBF.setGroupingUsed(false);

        mLogger.info("Initialized " + this.getClass().getSimpleName() +
                " maxSpeedKmh: " + mConfig.getMaxSpeedKmh() +
                " maxTimeSec: " + (mConfig.getMaxTimeMs() / 1000L) +
                " minDistanceMeter: " + Math.round(mConfig.getMinDistanceMeter()) +
                " htmlLogFile: " + mConfig.getHtmlLogFile() +
                " trace: " + mConfig.isTraceEnabled());
    }

    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());

        for (ZoneEvent toe : tTakeOverEvents) {
            LastTake tPreviousTake = mLatestUserTakes.get(toe.getCurrentOwnerId());
            LastTake tNewTake = new LastTake(toe);

            if (tPreviousTake != null) {
                validateMove(toe, tPreviousTake, tNewTake);
            } else if (mConfig.isTraceEnabled()) {
                mLogger.info("[trace] take user: " + toe.getCurrentOwner() +
                        " zone: " + tNewTake.mZoneName +
                        " time: " + SDF.format(tNewTake.mTakeTime) +
                        " (first take seen for user)");
            }

            if ((tPreviousTake == null) || (tNewTake.mTakeTime >= tPreviousTake.mTakeTime)) {
                mLatestUserTakes.put(toe.getCurrentOwnerId(), tNewTake);
            }
        }
        pruneOldTakes();
    }

    private void validateMove(ZoneEvent pTakeOverEvent, LastTake pPreviousTake, LastTake pNewTake) {
        long tTimeMs = pNewTake.mTakeTime - pPreviousTake.mTakeTime;
        if ((tTimeMs <= 0) || (pNewTake.mZoneId == pPreviousTake.mZoneId)) {
            if (mConfig.isTraceEnabled()) {
                mLogger.info("[trace] take user: " + pTakeOverEvent.getCurrentOwner() +
                        " zone: " + pNewTake.mZoneName +
                        " time: " + SDF.format(pNewTake.mTakeTime) +
                        ((pNewTake.mZoneId == pPreviousTake.mZoneId) ? " (re-take of same zone)" : " (out of order event)"));
            }
            return; // Same zone re-take or out of order event, no move to validate
        }

        double tDistanceMeter = DistanceCalculator.distance(pPreviousTake.mLat, pPreviousTake.mLong,
                pNewTake.mLat, pNewTake.mLong);
        double tHours = (double) tTimeMs / 3600_000.0d;
        double tSpeedKmh = (tDistanceMeter / 1000.0d) / tHours;

        if (mConfig.isTraceEnabled()) {
            mLogger.info("[trace] take user: " + pTakeOverEvent.getCurrentOwner() +
                    " from-zone: " + pPreviousTake.mZoneName +
                    " to-zone: " + pNewTake.mZoneName +
                    " time: " + SDF.format(pNewTake.mTakeTime) +
                    " elapsed: " + formatTimeMs(tTimeMs) +
                    " distance-m: " + Math.round(tDistanceMeter) +
                    " speed-kmh: " + mNBF.format(tSpeedKmh));
        }

        if (tSpeedKmh > mConfig.getMaxSpeedKmh()) {
            // Additional qualifiers for the HTML log (trace above is not affected).
            // Moves over a too long time or a too short distance are not considered suspicious.
            if ((tTimeMs > mConfig.getMaxTimeMs()) || (tDistanceMeter < mConfig.getMinDistanceMeter())) {
                if (mConfig.isTraceEnabled()) {
                    mLogger.info("[trace] disqualified take user: " + pTakeOverEvent.getCurrentOwner() +
                            " from-zone: " + pPreviousTake.mZoneName +
                            " to-zone: " + pNewTake.mZoneName +
                            " speed-kmh: " + mNBF.format(tSpeedKmh) +
                            ((tTimeMs > mConfig.getMaxTimeMs())
                                    ? " (elapsed " + formatTimeMs(tTimeMs) + " > max time)"
                                    : " (distance " + Math.round(tDistanceMeter) + " m < min distance)"));
                }
                return;
            }
            mLogger.info("suspicious take user: " + pTakeOverEvent.getCurrentOwner() +
                    " from-zone: " + pPreviousTake.mZoneName +
                    " to-zone: " + pNewTake.mZoneName +
                    " distance-m: " + Math.round(tDistanceMeter) +
                    " speed-kmh: " + mNBF.format(tSpeedKmh));
            logSuspiciousTake(pTakeOverEvent.getCurrentOwner(), pPreviousTake, pNewTake, tTimeMs, tDistanceMeter, tSpeedKmh);
        }
    }

    private synchronized void logSuspiciousTake(String pUsername, LastTake pPreviousTake, LastTake pNewTake,
                                                long pTimeMs, double pDistanceMeter, double pSpeedKmh) {
        File tFile = new File(mConfig.getHtmlLogFile());
        boolean tWriteHeader = ((!tFile.exists()) || (tFile.length() == 0));

        try (PrintWriter tWriter = new PrintWriter(new FileWriter(tFile, true))) {
            if (tWriteHeader) {
                tWriter.println("<html><head><title>Suspicious Turf Takes</title>");
                tWriter.println("<style>table,th,td {border: 1px solid black; border-collapse: collapse; padding: 4px;}</style>");
                tWriter.println("</head><body>");
                tWriter.println("<h2>Suspicious Turf Takes</h2>");
                tWriter.println("<table>");
                tWriter.println("<tr><th>Take Time</th><th>User</th><th>From Zone</th><th>To Zone</th>" +
                        "<th>Elapsed Time</th><th>Distance (m)</th><th>Speed (km/h)</th></tr>");
            }
            tWriter.println("<tr><td>" + SDF.format(pNewTake.mTakeTime) + "</td>" +
                    "<td>" + pUsername + "</td>" +
                    "<td>" + pPreviousTake.mZoneName + "</td>" +
                    "<td>" + pNewTake.mZoneName + "</td>" +
                    "<td>" + formatTimeMs(pTimeMs) + "</td>" +
                    "<td>" + Math.round(pDistanceMeter) + "</td>" +
                    "<td>" + mNBF.format(pSpeedKmh) + "</td></tr>");
        } catch (IOException e) {
            mLogger.error("Failed to write suspicious take to HTML log file \"" + mConfig.getHtmlLogFile() + "\"", e);
        }
    }

    /**
     * Users seen in the feed are worldwide. Regularly get rid of take entries for users
     * not being active within the last 24 hours to prevent the map from growing indefinitely.
     */
    private void pruneOldTakes() {
        if (mNextPruneTime > System.currentTimeMillis()) {
            return;
        }
        mNextPruneTime = System.currentTimeMillis() + PRUNE_INTERVAL_MS;

        int tSizeBefore = mLatestUserTakes.size();
        long tOldestTakeTime = System.currentTimeMillis() - TAKE_MAX_AGE_MS;
        Iterator<LastTake> tItr = mLatestUserTakes.values().iterator();
        while (tItr.hasNext()) {
            if (tItr.next().mTakeTime < tOldestTakeTime) {
                tItr.remove();
            }
        }
        mLogger.debug("pruned user takes, before: " + tSizeBefore + " after: " + mLatestUserTakes.size());
    }

    private String formatTimeMs(long pMilliSecond) {
        int hh = (int) (pMilliSecond / 3600_000L);
        int mm = (int) ((pMilliSecond - (hh * 3600_000L)) / 60_000);
        int sec = (int) ((pMilliSecond - (hh * 3600_000L) - (mm * 60_000)) / 1000L);
        return String.format("%d:%02d:%02d", hh, mm, sec);
    }

    @Override
    public JsonObject execute(MessageInterface tRqstMsg) {
        JsonObject tJsonErrorRsp = TGStatus.createError("No " + this.getClass().getSimpleName() +
                " service method found for request \"" + tRqstMsg.getMessageName() + "\"", null).toJson();
        mLogger.warn(tJsonErrorRsp.toString());
        return tJsonErrorRsp;
    }

    static class LastTake
    {
        private final int mZoneId;
        private final String mZoneName;
        private final long mTakeTime;
        private final double mLat;
        private final double mLong;

        LastTake(ZoneEvent pZoneEvent) {
            mZoneId = pZoneEvent.getZoneId();
            mZoneName = pZoneEvent.getZoneName();
            mTakeTime = pZoneEvent.getLatestTakeOverTime();
            mLat = pZoneEvent.getLat();
            mLong = pZoneEvent.getLong();
        }
    }
}
