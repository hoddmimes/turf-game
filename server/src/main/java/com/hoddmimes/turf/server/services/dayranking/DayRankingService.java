package com.hoddmimes.turf.server.services.dayranking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.TurfUser;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.DayRankingConfiguration;
import com.hoddmimes.turf.server.configuration.UserTraceConfiguration;
import com.hoddmimes.turf.server.generated.DayRankingRegion;
import com.hoddmimes.turf.server.generated.DayRankingUser;
import com.hoddmimes.turf.server.generated.DayRankingUserStart;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.conversions.Bson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayRankingService implements TurfServiceInterface {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat HH_MM_SS_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private TurfServerInterface mTurfIf;
    private Logger                          mLogger;
    private MongoAux                        mDbAux;
    private DayRankingConfiguration         mConfig;
    private String                          mCurrentDate;
    private RefreshRankingThread            mRefreshThread;
    private EventFilterNewZoneTakeOver mZoneFilter = null;

    private Map<Integer, DayRankingUser>           mRankingUsers;
    private Map<Integer, DayRankingUserStart>      mRankingUsersStart;
    private Map<Integer, DayRankingRegion>         mRankingRegions;




    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mCurrentDate = DATE_FORMAT.format( System.currentTimeMillis()) ;
        mZoneFilter = new EventFilterNewZoneTakeOver();

        mTurfIf = pTurfServerInterface;
        mDbAux = mTurfIf.getDbAux();
        mLogger = LogManager.getLogger(this.getClass().getSimpleName());
        mConfig = mTurfIf.getServerConfiguration().getDayRankingConfiguration();;
        mRankingUsers = new HashMap<>();
        mRankingUsersStart = new HashMap<>();
        mRankingRegions = new HashMap<>();
        mLogger.info("Initialize " + this.getClass().getSimpleName());

        loadStartRankingFromDB();

        mRefreshThread = new RefreshRankingThread( this );
        mRefreshThread.start();
    }

    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates)
    {
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());
        for( ZoneEvent toe: tTakeOverEvents) {
            DayRankingUser ru = mRankingUsers.get( toe.getCurrentOwnerId());
            if (ru != null) {
                ru.setLatestTakeTime(toe.getLatestTakeOverTime());
            }
        }
    }

    @Override
    public String execute(MessageInterface tRqstMsg) {
        return null;
    }

    private void loadStartRankingFromDB() {

        // Load region start data for current date
        List<DayRankingRegion> tDbRegionList = mDbAux.findDayRankingRegionByDate(mCurrentDate);
        if ((tDbRegionList != null) && (tDbRegionList.size() > 0)) {
            for (DayRankingRegion r : tDbRegionList) {
                mRankingRegions.put( r.getRegionId().get(), r);
            }
            mLogger.info("Loaded Region Ranking Data from DB (" + mCurrentDate + "), regions loaded: " + tDbRegionList.size() + " users: " + mRankingUsers.size());
        } else {
             mLogger.info("No Start Ranking Region Data for  (" + mCurrentDate + ") was found in database");
        }


        // Load user start datat for current date
        Bson tFilter= Filters.eq("date", mCurrentDate);
        List<DayRankingUserStart> tDbUserList = mDbAux.findDayRankingUserStart( tFilter );
        if ((tDbUserList != null) && (tDbUserList.size() > 0)) {
            tDbUserList.stream().forEach( u -> mRankingUsersStart.put( u.getUserId().get(), u));
            mLogger.info("Loaded User Ranking Data from DB (" + mCurrentDate + "), users loaded: " + tDbUserList.size());
        } else {
            mLogger.info("No Start Ranking User Data for  (" + mCurrentDate + ") was found in database");
        }
    }

    class RefreshRankingThread extends Thread {
        DayRankingService mService;

        RefreshRankingThread( DayRankingService pService) {
            mService = pService;

        }

        JsonArray getUserRanking( DayRankingConfiguration.Region pRegion ) {
            String tPostRqst = "{\"region\": \"" + pRegion.getName() + "\", \"from\":1, \"to\": 300 }";
            try {
                JsonElement jElement = Turf.turfServerPOST("users/top", tPostRqst);
                if (jElement == null) {
                    return null;
                }
                return jElement.getAsJsonArray();
            }
            catch( Throwable e) {
                mService.mLogger.error("Failed to retreive user day ranking from Turf service", e);
                return null;
            }
        }

        private DayRankingUserStart createRankingUserStart( TurfUser pUser ) {
            DayRankingUserStart rus = new DayRankingUserStart();
            rus.setDate( mCurrentDate );
            rus.setPoints( pUser.getPoints());
            rus.setTaken( pUser.getTaken());
            rus.setUser( pUser.getUserName());
            rus.setUserId( pUser.getUserId());
            return rus;
        }
        private DayRankingUser createRankingUser( TurfUser pUser ) {
            DayRankingUser ru = new DayRankingUser();
            ru.setActiveZones( pUser.getActiveZones());
            ru.setLatestTakeTime(0L);
            ru.setPoints( pUser.getPoints());
            ru.setPlace( pUser.getPlace());
            ru.setPPH( pUser.getPPH());
            ru.setTaken( pUser.getTaken());
            ru.setUser( pUser.getUserName());
            ru.setUserId( pUser.getUserId());
            ru.setFinalized(false);
            ru.setDate( mCurrentDate );
            return ru;
        }
        private void setStartDayRankingData( DayRankingConfiguration.Region pRegion, JsonArray jUserArray ) {
            DayRankingRegion drr = new DayRankingRegion();
            drr.setDate(mCurrentDate);
            drr.setRegionId(pRegion.getId());
            drr.setRegionName(pRegion.getName());
            drr.setFinalized(false);
            drr.setStartTime(System.currentTimeMillis());
            mDbAux.insertDayRankingRegion(drr);


            List<DayRankingUserStart> tUserStartList = new ArrayList<>();
            for (int i = 0; i < jUserArray.size(); i++) {
                TurfUser tUser = new TurfUser(jUserArray.get(i).getAsJsonObject());
                DayRankingUserStart rus = createRankingUserStart(tUser);
                mRankingUsersStart.put(rus.getUserId().get(), rus);
                mDbAux.insertDayRankingUserStart(rus);
            }
        }

        private boolean newDay() {
            String tDate = DATE_FORMAT.format( System.currentTimeMillis());
            if (mCurrentDate.compareTo(tDate) == 0) {
               return false;
            }
            return true;
        }

        private void finalizeDay() {
            for( DayRankingRegion drr : mRankingRegions.values()) {
                drr.setFinalized( true );
                Bson tFilter= Filters.and( Filters.eq("date", drr.getDate().get()),
                                            Filters.eq("regionId", drr.getRegionId().get()));
                mDbAux.updateDayRankingRegion( tFilter, drr, true);
            }
            for(DayRankingUser ru : mRankingUsers.values()) {
                Bson tFilter= Filters.and( Filters.eq("date", ru.getDate().get()),
                                           Filters.eq("userId", ru.getUserId().get()));
                ru.setFinalized( true );
                mDbAux.updateDayRankingUser( tFilter, ru, true );

            }
            mRankingUsers.clear();
            mRankingUsersStart.clear();
            mRankingRegions.clear();
            mLogger.info(" FINALIZED Date: " + mCurrentDate );
            mCurrentDate = DATE_FORMAT.format( System.currentTimeMillis());
        }

        private void updateRankingData(DayRankingConfiguration.Region pRegion, JsonArray jUserArray ) {

            for (int i = 0; i < jUserArray.size(); i++) {
                TurfUser tu = new TurfUser(jUserArray.get(i).getAsJsonObject());

                DayRankingUserStart rus = mRankingUsersStart.get(tu.getUserId());
                if (rus != null) {
                    DayRankingUser ru = mRankingUsers.get(tu.getUserId());
                    ru.setActiveZones(tu.getActiveZones());
                    ru.setPoints(tu.getPoints() - rus.getPoints().get());
                    ru.setPPH(tu.getPPH());
                    ru.setTaken(tu.getTaken() - rus.getTaken().get());
                    mDbAux.updateDayRankingUser(ru.getDate().get(), ru.getUserId().get(), ru, true);
                }
            }
        }

        void refresh() {
            // Check if we passed date boundary, if so finalize date and initialize for a new day
            if (newDay()) {
                finalizeDay();
            }

            // get regions that are tracked
            List<DayRankingConfiguration.Region> mRegions = mService.mConfig.getRegions();

            for( DayRankingConfiguration.Region r : mRegions ) {
                // Get current user ranking for the region
                JsonArray jUserRanking = getUserRanking( r );
                if (jUserRanking != null) {
                    // Check if Ranking Start Data exists
                    if (!mRankingRegions.containsKey( r.getId())) {
                        setStartDayRankingData( r, jUserRanking );
                    } else {
                        updateRankingData( r, jUserRanking);
                    }
                } else {
                    mLogger.info("No ranking data found for region: " + r.getName() );
                }
            }
        }

        private long calcDismissTime( long pWaitTime ) {
            long tNow = System.currentTimeMillis();
            long tRem = (tNow % 60000L);
            return (pWaitTime - tRem) + 500L;
        }



        @Override
        public void run() {
            mLogger.info("Start DayRankingService refresh thread");
            refresh();
            try {
                long tDismissTime = calcDismissTime( mService.mConfig.getmRefreshIntervalMS() );
                Thread.sleep( tDismissTime );
            }
            catch( InterruptedException e) {}
        }

    }
}
