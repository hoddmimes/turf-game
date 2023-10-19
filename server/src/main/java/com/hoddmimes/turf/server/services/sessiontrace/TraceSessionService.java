package com.hoddmimes.turf.server.services.sessiontrace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.transform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.TurfZone;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.TraceSessionsConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.SessionTrace;
import com.hoddmimes.turf.server.generated.TraceZone;
import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TraceSessionService implements TurfServiceInterface {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat HH_MM_SS_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private TraceSessionsConfiguration mConfig;
    private List<SessionTrace>  mTopSessions;
    private Map<Integer,SessionTrace>  mActiveSessions; // Key == User Id
    private Map<Integer,SessionTrace> mActiveZones;   // Key == user Id
    private EventFilterNewZoneTakeOver mZoneFilter = null;
    private MongoAux mDbAux = null;



    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mConfig = pTurfServerInterface.getServerConfiguration().getTraceSessionsConfiguration();
        mLogger = LogManager.getLogger(this.getClass().getName());
        mZoneFilter = new EventFilterNewZoneTakeOver();
        mTurfIf = pTurfServerInterface;
        mLogger = LogManager.getLogger(this.getClass().getName());
        mActiveSessions = new HashMap<>();
        mActiveZones = new HashMap<>();
        mDbAux = mTurfIf.getDbAux();


        // Load Top Sessions from the database
        mTopSessions =  mTurfIf.getDbAux().findAllSessionTrace();
        mLogger.info("Initialized "  + this.getClass().getSimpleName() + ", load " + mTopSessions.size() + " from DB");
    }



    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());
        mLogger.debug("<processZoneUpdates> TraceSessionService zoneUpdates: " + pZoneUpdates.getAsJsonArray().size() + " takeOverEvents: " + tTakeOverEvents.size());

        for (ZoneEvent toe : tTakeOverEvents) {

            /**
             * Check if the take-over-event is for a zone that we currently trace
             * If so terminate the hold and remove the zone fom the mActiveZones map.
             */
            SessionTrace st = mActiveZones.get( toe.getZoneId());
            if (st != null) {
              traceZoneTerminateOwnership( st, toe.getZoneId(), toe.getLatestTakeOverTime());
              mActiveZones.remove(toe.getZoneId());
                mLogger.debug("zone ownership ended for user: " + toe.getCurrentOwner() + " zone: " + toe.getZoneName());
            }


            /**
             * Update/create a SessionTrace if being of interest
             */
            st = mActiveSessions.get(toe.getCurrentOwnerId());

            // Check is  a SessionTrace exists for the user if not create a entry (if being of interest)
            if (st == null) {
                TurfZone tz = mTurfIf.getZoneById(toe.getZoneId());
                if ((tz == null) || (!tz.hasRegion()) || (!this.mConfig.isRegionOfInterest( tz.getRegionid()))) {
                    continue;
                }
                st = new SessionTrace();
                st.setUserDateTime( toe.getCurrentOwnerId() + "-" + SDF.format(System.currentTimeMillis()));
                st.setUsername( toe.getCurrentOwner());
                st.setRegion(toe.getRegionName());
                st.setStartTimeStr( SDF.format(toe.getLatestTakeOverTime()));
                st.setStartTime( toe.getLatestTakeOverTime());
                st.setDistance(0.0d);
                this.mActiveSessions.put( toe.getCurrentOwnerId(), st);
                mLogger.info("starting a new top-session, user: "  + toe.getCurrentOwner() + " region: " + toe.getRegionName());
            }
            TraceZone tz = new TraceZone();
            tz.setZoneName( toe.getZoneName());
            tz.setZoneId( toe.getZoneId());
            tz.setTakeTime( toe.getLatestTakeOverTime());
            tz.setPph( toe.getPPH());
            tz.setTp( toe.getTP());
            tz.setEndTime(0L);
            tz.setLat( toe.getLat());
            tz.setLong( toe.getLong());
            st.setLatestTakeTime(  toe.getLatestTakeOverTime());

            st.addZones( tz );
            this.mActiveZones.put( toe.getZoneId(), st);
            mLogger.debug("added zone take for user: " + toe.getCurrentOwner() + " zone: " + toe.getZoneName() + " zones taken: " + st.getZones().get().size());
        }

        /**
         * Check if any session has expired / terminated if so evaluate the session and see
         *  whatever it make it on the top list
         */
         Iterator<SessionTrace> tSessItr = mActiveSessions.values().iterator();
         mLogger.debug("Checking active sessions timeout (" + mConfig.getSessionTimeoutSec() + " sec) sessions: " + mActiveSessions.size());
         while( tSessItr.hasNext()) {
             SessionTrace st = tSessItr.next();

             NumberFormat nbf = NumberFormat.getInstance();
             nbf.setMaximumFractionDigits(1);
             // Check if session have become inactive? If so evaluate check ranking and remove the session
             if ((st.getLatestTakeTime().get() + (mConfig.getSessionTimeoutSec() * 1000L)) < System.currentTimeMillis()) {
                 evaluateSessionTrace( st );
                 if (st.getZones().get().size() > 2) {
                     mLogger.info("completed session owner: " + st.getUsername().get() +
                             " region: " + st.getRegion().get() +
                             " takes: " + st.getZones().get().size() +
                             " points: " + st.getPoints().get() +
                             " distance: " + nbf.format((double) st.getDistance().get()) +
                             " time: " + formatTimeMs((st.getLatestTakeTime().get() - st.getStartTime().get())) +
                             " points/hour: " + nbf.format(st.getSess_pp_hh().get()) +
                             " points/km: " + nbf.format(st.getSess_pp_km().get()));
                 } else {
                     mLogger.info("completed session owner: " + st.getUsername().get() +
                             " takes: " + st.getZones().get().size() +
                             " points: " + st.getPoints().get());
                 }
                 if (st.getZones().get().size() >= mConfig.getMinTakes()) {
                     checkRanking(st);
                 }
                 tSessItr.remove();
             }
         }

    }


    private void checkRanking( SessionTrace pSessionTrace ) {
        this.mTopSessions.sort( new SessionComparator("TIME"));

        int tRank = -1;
        for( int i = 0; i < mTopSessions.size(); i++) {
            if (pSessionTrace.getSess_pp_hh().get() > mTopSessions.get(i).getSess_pp_hh().get()) {
                tRank = i;
            }
        }

        if ((tRank >= 0) || (mTopSessions.size() < mConfig.getRankingSize())) {
            if (mTopSessions.size() >= mConfig.getRankingSize()) {
                mDbAux.deleteSessionTraceByMongoId(mTopSessions.get(mTopSessions.size() - 1).getMongoId());
                mTopSessions.remove(mTopSessions.size() - 1);
            }
            mTopSessions.add(pSessionTrace);
            UpdateResult tDbResult = mDbAux.updateSessionTrace( pSessionTrace, true);
            pSessionTrace.setMongoId( tDbResult.getUpsertedId().asObjectId().getValue().toHexString());
            mLogger.info("New top-session: " + pSessionTrace );
        } else {
            mLogger.info("NOT a NEW top-session (rank: " + tRank + ") : " + pSessionTrace );
        }
    }

    private double traceZonePph( TraceZone pTraceZone, long pSessionEndTime ) {
        long tEndTime = (pTraceZone.getEndTime().get() > 0) ? pTraceZone.getEndTime().get() : pSessionEndTime;
        double hh = (tEndTime - pTraceZone.getTakeTime().get()) / 3600_000.0d;
        return (pTraceZone.getPph().get() * hh);
    }
    private double traceSessionGetDistanceKm( SessionTrace pSessionTrace) {
        double tDistance = 0.0d;
        List<TraceZone> zl = pSessionTrace.getZones().get();
        for( int i = 1; i < zl.size(); i++) {
            tDistance += DistanceCalculator.distance( zl.get(i-1).getLat().get(), zl.get(i-1).getLong().get(),
                                                      zl.get(i).getLat().get(), zl.get(i).getLong().get());
        }
        return (tDistance / 1000.0d);
    }

    private void evaluateSessionTrace( SessionTrace pSessionTrace ) {
        final long tEndTime = pSessionTrace.getLatestTakeTime().get();
       List<TraceZone> zl = pSessionTrace.getZones().get();
       double total_points = (double) zl.stream().mapToInt(z -> z.getTp().get()).sum() + // Add all TP
                                      zl.stream().mapToDouble(z -> traceZonePph(z, tEndTime)).sum(); // Add all PPH

       double hh = (double) (pSessionTrace.getLatestTakeTime().get() - pSessionTrace.getStartTime().get()) / 3600_000L;
       double km = traceSessionGetDistanceKm(pSessionTrace);

       pSessionTrace.setDistance( km );
       pSessionTrace.setPoints( (int) Math.round(total_points));

       if (km > 0.0d) {
           pSessionTrace.setSess_pp_km( total_points / km );
       } else {
           pSessionTrace.setSess_pp_km( 0.0d );
       }
       if (hh > 0) {
           pSessionTrace.setSess_pp_hh( total_points / hh );
       } else {
           pSessionTrace.setSess_pp_hh( 0.0d );
       }
    }

    private void traceZoneTerminateOwnership( SessionTrace pSessionTrace, int pZoneId, long pEndTime ) {
        List<TraceZone> zl = pSessionTrace.getZones().orElse( new ArrayList<>());
        for( TraceZone tz : zl ) {
            if (tz.getZoneId().get() == pZoneId) {
                tz.setEndTime( pEndTime );
                tz.setEndTimeStr( SDF.format( pEndTime ));
            }
        }
    }


    private String formatTimeSec(long pSeconds) {
        int hh = (int) (pSeconds / 3600L);
        int mm = (int) ((pSeconds - (hh * 3600L)) / 60);
        int  sec = (int) (pSeconds - (hh * 3600L) - (mm * 60));
        return String.format("%d:%02d:%02d", hh, mm, sec);
    }

    private String formatTimeMs(long pMilliSecond) {
        int hh = (int) (pMilliSecond / 3600_000L);
        int mm = (int) ((pMilliSecond - (hh * 3600_000L)) / 60_000);
        int  sec = (int) ((pMilliSecond - (hh * 3600_000L) - (mm * 60_000)) / 1000L);
        return String.format("%d:%02d:%02d", hh, mm, sec);
    }


    @Override
    public JsonObject execute(MessageInterface tRqstMsg) {
        mLogger.debug("execute request, request message: "  + tRqstMsg.toJson().toString());
        if (tRqstMsg instanceof ST_RankingRqst) {
            return executeRanking(((ST_RankingRqst) tRqstMsg).getType().orElse("TIME"));
        }
        if (tRqstMsg instanceof ST_SessionRqst) {
            return executeSession((ST_SessionRqst) tRqstMsg);
        }


        JsonObject tJsonErrorRsp = TGStatus.createError("No " + this.getClass().getSimpleName() +
                " service method not found for request \"" + tRqstMsg.getMessageName() + "\"", null ).toJson();
        mLogger.warn( tJsonErrorRsp.toString() );
        return tJsonErrorRsp;
    }


    SessionTrace findSessionTrace( String pSessionId ) {
        for( SessionTrace st : mTopSessions) {
            if (st.getMongoId().equalsIgnoreCase( pSessionId )) {
                return st;
            }
        }
        return null;
    }

    private double getHours( long pDeltatimeMs ) {
        return ((double) pDeltatimeMs / 3600_000d);
    }

    JsonObject executeSession(ST_SessionRqst pRqstMsg)
    {
        SessionTrace st = findSessionTrace( pRqstMsg.getSessionId().get());
        if (st == null) {
            return TGStatus.createError("Session trace with id \"" + pRqstMsg.getSessionId().get() + "\" is not found", null ).toJson();
        }
        ST_SessionRsp tResponse = new ST_SessionRsp();
        tResponse.setDistance( st.getDistance().get());
        tResponse.setRegion( st.getRegion().get());
        tResponse.setUser( st.getUsername().get());
        tResponse.setDateTime( SDF.format(st.getStartTime().get()).substring(0,16));
        tResponse.setDuration( formatTimeMs( (st.getLatestTakeTime().get() - st.getStartTime().get())));
        tResponse.setSpeed( st.getDistance().get() / getHours( (st.getLatestTakeTime().get() - st.getStartTime().get())));
        tResponse.setPpHH( st.getSess_pp_hh().get());
        tResponse.setPpKM( st.getSess_pp_km().get());
        tResponse.setPoints( st.getPoints().get());


        List<TraceZone> zl = st.getZones().get();
        for( int i = 0; i < zl.size(); i++ ) {
            ST_Zone z = new ST_Zone();
            z.setName( zl.get(i).getZoneName().get());
            z.setPph( zl.get(i).getPph().get());
            z.setTp( zl.get(i).getTp().get());
            z.setLat( zl.get(i).getLat().get());
            z.setLong( zl.get(i).getLong().get());
            z.setTime(HH_MM_SS_FORMAT.format(zl.get(i).getTakeTime().get()));
            if (i == 0) {
                z.setDeltaTime( formatTimeSec(0));
                z.setDistance(0);
            } else {
                z.setDeltaTime( formatTimeMs( (zl.get(i).getTakeTime().get() - zl.get(i - 1).getTakeTime().get())));
                z.setDistance( (int) Math.round(DistanceCalculator.distance( zl.get(i).getLat().get(),
                                                            zl.get(i).getLong().get(),
                                                            zl.get(i - 1).getLat().get(),
                                                            zl.get(i - 1).getLong().get())));
            }
            tResponse.addZones(z);
        }
        return tResponse.toJson();
    }




    JsonObject executeRanking(String pRankingType) {

        int tRank = 1;
        ST_RankingRsp tResponse = new ST_RankingRsp();
        if (mTopSessions.size() == 0) {
            tResponse.setSessions( new ArrayList<>());
            return tResponse.toJson();
        }

        mTopSessions.sort( new SessionComparator(pRankingType));
        for( SessionTrace top_st : mTopSessions) {
            ST_Session st = new ST_Session();
            st.setRank( tRank++);
            st.setDistance( top_st.getDistance().get());
            st.setSpeed( st.getDistance().get() / getHours( (top_st.getLatestTakeTime().get() - top_st.getStartTime().get())));
            String date_time_str = SDF.format( top_st.getStartTime().get()).substring(0,16);
            st.setDateTime( date_time_str );
            st.setPoints( top_st.getPoints().get());
            st.setUser(top_st.getUsername().get());
            st.setZones( top_st.getZones().get().size());
            st.setSessionId( top_st.getMongoId());
            st.setRegion( top_st.getRegion().get());
            st.setPoints_hh( top_st.getSess_pp_hh().get());
            st.setPoints_km( top_st.getSess_pp_km().get());
            st.setDuration( formatTimeMs(top_st.getLatestTakeTime().get() - top_st.getStartTime().get()));

            tResponse.addSessions( st );
        }
        return tResponse.toJson();
    }


    class SessionComparator implements Comparator<SessionTrace>
    {
        private String mRankingType;
        SessionComparator( String pRankingType ) {
            mRankingType = pRankingType;
        }
        @Override
        public int compare(SessionTrace st1, SessionTrace st2) {
            if (mRankingType.equalsIgnoreCase("TIME")) {
                return Double.compare(st2.getSess_pp_hh().get(), st1.getSess_pp_hh().get());
            } else {
                return Double.compare(st2.getSess_pp_km().get(), st1.getSess_pp_km().get());
            }
        }
    }
}
