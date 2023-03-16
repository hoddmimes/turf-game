package com.hoddmimes.turf.server.services.usertrace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;

import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.UserTraceConfiguration;
import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTraceService implements TurfServiceInterface {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private PrintWriter mOut = null;
    private UserTraceConfiguration mConfig;
    private Map<String,TraceUser>  mUsers;
    private EventFilterNewZoneTakeOver mZoneFilter = null;
    private long mLatestConfigRecheck;


    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mZoneFilter = new EventFilterNewZoneTakeOver();
        mTurfIf = pTurfServerInterface;
        mLogger = LogManager.getLogger(this.getClass().getName());
        mConfig = mTurfIf.getServerConfiguration().getUserTraceConfiguration();
        mUsers = new HashMap<>();
        mLatestConfigRecheck = System.currentTimeMillis();

        try {
            String tFilename = mTurfIf.getServerConfiguration().getUserTraceConfiguration().getLogfilename();
            mOut = new PrintWriter(new FileOutputStream(tFilename, true), true);
            log(" ==== Starting the User Trace Service ==== ");
        } catch (IOException e) {
            mLogger.error("Failed to open user trace logfile \"" + mTurfIf.getServerConfiguration().getUserTraceConfiguration().getLogfilename() + "\"", e);
            mOut = null;
        }
    }

    private void log( String pMsg ) {
        if (mOut != null) {
            mOut.println( SDF.format( System.currentTimeMillis()) + " " + pMsg );
        }
        if (mConfig.traceAll()) {
            System.out.println( SDF.format( System.currentTimeMillis()) + " " + pMsg );
        }
    }

    private boolean userOfInterest( String pUser ) {
        if (mConfig.traceAll()) {
            return true;
        }

        for( String u : mConfig.getUsers()) {
            if (u.compareToIgnoreCase(pUser) == 0) {
                return true;
            }
        }
        return false;
    }

    private void recheckConfiguration() {
        if ((mLatestConfigRecheck + 180000L) < System.currentTimeMillis()) {
            Element tRoot = this.mTurfIf.getGetAndLoadCurrentConfiguration();
            mConfig.parse( tRoot );
            mLatestConfigRecheck = System.currentTimeMillis();
        }
    }

    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
        recheckConfiguration();


        long tStartTime = System.currentTimeMillis();
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());
        for( ZoneEvent toe: tTakeOverEvents) {
            if (userOfInterest( toe.getCurrentOwner())) {
                TraceUser tu = mUsers.get( toe.getCurrentOwner());
                if (tu == null) {
                    tu = new TraceUser( toe );
                    mUsers.put( tu.mName, tu );
                    log( tu.getStartSessionMessage());
                } else {
                    tu.newTake( toe );
                    log( tu.getZoneTakeOverMessage());
                }
            }
        }
        /**
         * Check if we have any session that has expired
         */
        long tNow = System.currentTimeMillis();
        ArrayList<TraceUser> tRmLst = new ArrayList();
        for( TraceUser tu : mUsers.values()) {
            if ((tNow - tu.mLatestZone.getLatestTakeOverTime()) > (mConfig.getSessionInactivityMin() * 60000L)) {
                log( tu.getEndSessionMessage());
                tRmLst.add( tu );
            }
        }

        tRmLst.stream().forEach( tu ->mUsers.remove(tu.mName));
    }


    private String formatTime(long pSeconds) {
        int hh = (int) (pSeconds / 3600L);
        int mm = (int) ((pSeconds - (hh * 3600L)) / 60);
        int  sec = (int) (pSeconds - (hh * 3600L) - (mm * 60));
        return String.format("%d:%02d:%02d", hh, mm, sec);
    }


    @Override
    public JsonObject execute(MessageInterface tRqstMsg) {
        return null;
    }

    class TraceUser {
        private ZoneEvent mLatestZone;
        private final long      mSessionCreateTime;
        private final String    mName;
        private long      mDistance;
        private long      mZoneTaken;

        private long mLatestTimeDiffSec;
        private long mLatestDistanceDiff;
        private double mLatestSpeedKM;

        private NumberFormat nbf = NumberFormat.getInstance();

        TraceUser( ZoneEvent toe ) {
            mLatestZone = toe;
            mName = toe.getCurrentOwner();
            mDistance = 0;
            mZoneTaken = 1;
            mSessionCreateTime = toe.getLatestTakeOverTime();
            nbf.setMinimumFractionDigits(1);
            nbf.setMaximumFractionDigits(2);
            nbf.setGroupingUsed(false);
        }

        void newTake( ZoneEvent toe ) {
            mLatestTimeDiffSec = (toe.getLatestTakeOverTime() - mLatestZone.getLatestTakeOverTime()) / 1000L;
            double tDist = Math.round(DistanceCalculator.distance( mLatestZone.getLat(), mLatestZone.getLong(), toe.getLat(), toe.getLong()));
            mLatestSpeedKM = (tDist * 3.6) / (double) mLatestTimeDiffSec;
            mLatestDistanceDiff = Math.round( tDist );

            mZoneTaken++;
            mDistance += mLatestDistanceDiff;
            mLatestZone = toe;
        }

        String getStartSessionMessage() {
            return "**** user: " + mName + " starting new session at " + SDF.format( mLatestZone.getLatestTakeOverTime()) + " by taking zone: " +
                    mLatestZone.getZoneName() + " in region " + mLatestZone.getRegionName() + " ****";
        }

        String getZoneTakeOverMessage() {

            return "user: " + mName + " zone: " + mLatestZone.getZoneName() + " region: " + mLatestZone.getRegionName() + " dist: " + mLatestDistanceDiff +
                    " m.  timdiff: " + formatTime( mLatestTimeDiffSec ) + " sec.  speed: " + nbf.format( mLatestSpeedKM ) + " (km/h)" +
                    " takes: "  + mZoneTaken  + " total dist: " + mDistance + " tottim: " + formatTime(((mLatestZone.getLatestTakeOverTime() - mSessionCreateTime) / 1000L)) +
                    " avg speed: " + nbf.format( (((double) mDistance) * 3.6) / ((mLatestZone.getLatestTakeOverTime() - mSessionCreateTime) / 1000L)) + " km/h";
        }

        String getEndSessionMessage() {
            double tSpeed = (mZoneTaken == 1) ? 0 : ((((double) mDistance) * 3.6) / ((mLatestZone.getLatestTakeOverTime() - mSessionCreateTime) / 1000L));

            return "$$$$ user: " + mName + " ending session at " + SDF.format( mLatestZone.getLatestTakeOverTime()) + " distance: " + mDistance +
                    " zone taken: " + mZoneTaken +
                    " time used: " + formatTime(((mLatestZone.getLatestTakeOverTime() - mSessionCreateTime) / 1000L)) +
                    " min / zon: " + nbf.format( ((mLatestZone.getLatestTakeOverTime() - mSessionCreateTime) / 60000L) / (double) mZoneTaken ) +
                    " speed: " + nbf.format( tSpeed ) + " km/h $$$$";
        }



    }
}
