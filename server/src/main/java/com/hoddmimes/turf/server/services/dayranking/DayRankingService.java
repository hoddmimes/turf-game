package com.hoddmimes.turf.server.services.dayranking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.TurfUser;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.DayRankingConfiguration;
import com.hoddmimes.turf.server.generated.*;
import com.hoddmimes.turf.server.generated.DayRankingUser;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.*;

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
    private EventFilterNewZoneTakeOver      mZoneFilter = null;
    private long                            mDBSaveTimestamp;

    private Map<Integer, DayRankingUser>           mRankingUsers;
    private Map<Integer, DayRankingUserInit>       mRankingUsersInit;
    private Map<Integer, DayRankingRegion>         mRankingRegions;




    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mCurrentDate = DATE_FORMAT.format( System.currentTimeMillis()) ;
        mZoneFilter = new EventFilterNewZoneTakeOver();
        mDBSaveTimestamp = 0;

        mTurfIf = pTurfServerInterface;
        mDbAux = mTurfIf.getDbAux();
        mLogger = LogManager.getLogger(this.getClass().getSimpleName());
        mConfig = mTurfIf.getServerConfiguration().getDayRankingConfiguration();;
        mRankingUsers = new HashMap<>();
        mRankingUsersInit = new HashMap<>();
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
        if (tRqstMsg instanceof DR_RankingRqst) {
            return executeGetDayRanking((DR_RankingRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof DR_RegionRqst) {
            return executeGetRegions((DR_RegionRqst) tRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method not found for request \"" +
                tRqstMsg.getMessageName() + "\"", null ).toJson().toString();

    }

    private String executeGetDayRanking( DR_RankingRqst pRqst  ) {
        DR_RankingRsp tResponse = new DR_RankingRsp();
        String tDate = pRqst.getDate().orElse( mCurrentDate );
        int tRegionId = pRqst.getRegionId().orElse(141);
        List<DayRankingRegion> tRegionLst = mDbAux.findDayRankingRegion(  pRqst.getDate().get(), pRqst.getRegionId().get() );
        if ((tRegionLst == null) || (tRegionLst.size() == 0)) {
            return TGStatus.create(false,"No day ranking exists for region \"" + pRqst.getMessageName() + "\" and date \"" + tDate + "\"").toJson().toString();
        }

        List<DayRankingUser> dbUserList = mDbAux.findDayRankingUser( tDate, tRegionId );
        if ((dbUserList == null) || (dbUserList.size() == 0)) {
            return TGStatus.create(false,"No day ranking users exists for region \"" + pRqst.getMessageName() + "\" and date \"" + tDate + "\"").toJson().toString();
        }
        ArrayList<DR_User> drUsers = new ArrayList<>();
        for ( DayRankingUser dru : dbUserList) {
            DR_User u = new DR_User();
            u.setActiveZones( dru.getActiveZones().orElse(0));
            u.setPoints( dru.getPoints().orElse(0));
            u.setPph( dru.getPPH().orElse(0));
            u.setTakes( dru.getTakes().orElse(0));
            u.setUser( dru.getUser().get());
            drUsers.add(u);

        }
        Collections.sort(drUsers, new DayRankingUsertSorter());
        for( int i = 0; i < dbUserList.size(); i++) {
            drUsers.get(i).setPlace((i+1));
        }

        DayRankingRegion dr = tRegionLst.get(0);


        tResponse.setRegion( dr.getRegionName().get());
        tResponse.setRegionId( dr.getRegionId().get());
        tResponse.setStartTime( HH_MM_SS_FORMAT.format( dr.getStartTime().get()));
        tResponse.setUsers( drUsers.subList(0, mConfig.getMaxDisplayUsers()));
        return tResponse.toJson().toString();
    }

    private String executeGetRegions( DR_RegionRqst pRqst ) {
        DR_RegionRsp tResponse = new DR_RegionRsp();
        ArrayList<DR_Region> tRegionList = new ArrayList<>();
        for( DayRankingConfiguration.Region r : mConfig.getRegions()) {
            DR_Region dr = new DR_Region();
            dr.setRegion( r.getName());
            dr.setRegionId( r.getId());
            tRegionList.add( dr );
        }
        if (mConfig.getDefaultRegionName() != null) {
            tResponse.setDefaultRegion( mConfig.getDefaultRegionName());
            tResponse.setDefaultRegionId( mConfig.getDefaultRegionId());
        }
        tResponse.setFirstDateInDB( getFirstDateInDB());
        tResponse.addRegions(tRegionList);
        return tResponse.toJson().toString();

    }

    private String getFirstDateInDB() {
        MongoCollection tDayRankUsersColl = mDbAux.getDayRankingUserCollection();
        Bson tFilter =  Filters.eq("finalized", true);

        FindIterable<Document> tItrDoc = tDayRankUsersColl.find( tFilter).sort(new BasicDBObject("date",1)).limit(1);
        if (tItrDoc == null) {
            return mCurrentDate;
        }
        MongoCursor<Document> tIter = tItrDoc.iterator();
        if (tIter.hasNext()) {
             Document tDoc = tIter.next();
             String tDateStr = tDoc.get("date").toString();
             return (tDateStr == null) ? mCurrentDate : tDateStr;
        }
        return mCurrentDate;
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
        List<DayRankingUserInit> tDbUserList = mDbAux.findDayRankingUserInit( tFilter );
        if ((tDbUserList != null) && (tDbUserList.size() > 0)) {
            tDbUserList.stream().forEach( u -> mRankingUsersInit.put( u.getUserId().get(), u));
            mLogger.info("Loaded User Ranking Data from DB (" + mCurrentDate + "), users loaded: " + tDbUserList.size());
        } else {
            mLogger.info("No Start Ranking User Data for  (" + mCurrentDate + ") was found in database");
        }
    }

    class DayRankingUsertSorter implements Comparator<DR_User> {

        @Override
        public int compare(DR_User d1, DR_User d2) {
            if (d1.getPoints().get() < d2.getPoints().get()) {
                return 1;
            } else  if (d1.getPoints().get() > d2.getPoints().get()) {
                return -1;
            }
            return 0;
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

        private DayRankingUserInit createRankingUserStart( TurfUser pUser ) {
            DayRankingUserInit rus = new DayRankingUserInit();
            rus.setDate( mCurrentDate );
            rus.setInitPoints( pUser.getPoints());
            rus.setInitTaken( pUser.getTaken());
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
            ru.setTakes( pUser.getTaken());
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


            List<DayRankingUserInit> tUserStartList = new ArrayList<>();
            for (int i = 0; i < jUserArray.size(); i++) {
                TurfUser tUser = new TurfUser(jUserArray.get(i).getAsJsonObject());
                DayRankingUserInit rus = createRankingUserStart(tUser);
                mRankingUsersInit.put(rus.getUserId().get(), rus);
                mDbAux.insertDayRankingUserInit(rus);
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
                mDbAux.updateDayRankingRegion( drr, true);
            }
            for(DayRankingUser ru : mRankingUsers.values()) {
                ru.setFinalized( true );
                mDbAux.updateDayRankingUser( ru, true );

            }
            mRankingUsers.clear();
            mRankingUsersInit.clear();
            mRankingRegions.clear();
            mLogger.info(" FINALIZED Date: " + mCurrentDate );
            mCurrentDate = DATE_FORMAT.format( System.currentTimeMillis());
        }

        private void updateRankingData(DayRankingConfiguration.Region pRegion, JsonArray jUserArray ) {
            long tNow = System.currentTimeMillis();
            boolean tDBSave = false;
            if ((mDBSaveTimestamp + mConfig.getSaveIntervalMS()) < tNow) {
                tDBSave = true;
                mDBSaveTimestamp = tNow;
            }

            for (int i = 0; i < jUserArray.size(); i++) {
                TurfUser tu = new TurfUser(jUserArray.get(i).getAsJsonObject());

                DayRankingUserInit rus = mRankingUsersInit.get(tu.getUserId());
                if (rus != null) {
                    DayRankingUser ru = mRankingUsers.get(tu.getUserId());
                    ru.setActiveZones(tu.getActiveZones());
                    ru.setPoints(tu.getPoints() - rus.getInitPoints().get());
                    ru.setPPH(tu.getPPH());
                    ru.setTakes(tu.getTaken() - rus.getInitTaken().get());
                    if (tDBSave) {
                        mDbAux.updateDayRankingUser(ru.getDate().get(), ru.getUserId().get(), ru, true);
                    }
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
                long tDismissTime = calcDismissTime( mService.mConfig.getRefreshUserIntervalMS() );
                Thread.sleep( tDismissTime );
            }
            catch( InterruptedException e) {}
        }

    }
}
