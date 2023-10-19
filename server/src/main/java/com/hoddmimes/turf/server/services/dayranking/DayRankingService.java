package com.hoddmimes.turf.server.services.dayranking;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.transform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.*;
import com.hoddmimes.turf.server.configuration.CfgRegion;
import com.hoddmimes.turf.server.configuration.DayRankingConfiguration;
import com.hoddmimes.turf.server.generated.*;
import com.hoddmimes.turf.server.generated.DayRankingUser;
import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

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
    private volatile RefreshRankingThread   mRefreshThread;
    private EventFilterNewZoneTakeOver      mZoneFilter = null;

    private volatile Map<Integer, DayRankingUser>           mRankingUsers;
    private volatile Map<Integer, DayRankingInitUser>       mRankingInitUsers;
    private volatile Map<Integer, DayRankingRegion>         mRankingRegions;
    private volatile Map<Integer, ZoneEvent>                mUserZones;
    private volatile Map<Integer, Long>                     mRegionDbSaveTimestamps;


    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mCurrentDate = DATE_FORMAT.format( System.currentTimeMillis()) ;
        mZoneFilter = new EventFilterNewZoneTakeOver();

        mTurfIf = pTurfServerInterface;
        mDbAux = mTurfIf.getDbAux();
        mLogger = LogManager.getLogger(this.getClass().getName());
        mConfig = mTurfIf.getServerConfiguration().getDayRankingConfiguration();;
        mRankingUsers = new HashMap<>();
        mRankingInitUsers = new HashMap<>();
        mRankingRegions = new HashMap<>();
        mRegionDbSaveTimestamps = new HashMap<>();
        mUserZones = new HashMap<>();
        mLogger.info("Initialize " + this.getClass().getSimpleName());

        loadStartRankingFromDB();

        mRefreshThread = new RefreshRankingThread( this );
        mRefreshThread.start();
    }

    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates)
    {
        long tTotTime = 0;
        int  tTotDistance = 0;
        int  tTotUsers = 0;
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());
        mLogger.debug("<processZoneUpdates> Dayranking zoneUpdates: "  + pZoneUpdates.getAsJsonArray().size() + " takeOverEvents: " + tTakeOverEvents.size());

        for( ZoneEvent toe: tTakeOverEvents) {
            DayRankingUser ru = mRankingUsers.get( toe.getCurrentOwnerId());
            if (ru != null) {
                tTotUsers++;
                ru.setLatestTakeTime(toe.getLatestTakeOverTime());
                ZoneEvent ltoe = mUserZones.get( toe.getCurrentOwnerId() );
                if (ltoe != null) {
                    long tmSec = (toe.getLatestTakeOverTime() - ltoe.getLatestTakeOverTime()) / 1000L;
                    if (tmSec < (30L * 60L)) { // if longer than 30 min new session and ignore time and distance
                        double tDistance = DistanceCalculator.distance(toe.getLat(), toe.getLong(), ltoe.getLat(), ltoe.getLong());
                        ru.setDistance((int) (ru.getDistance().orElse(0) + Math.round(tDistance)));
                        ru.setTime(ru.getTime().orElse(0L) + tmSec);

                        tTotTime += tmSec;
                        tTotDistance += Math.round( tDistance );

                        if (mLogger.isTraceEnabled()) {
                            mLogger.trace("DayRanking (user session) user: " + ru.getUser().get() + " new Zone: " + ltoe.getZoneName() +
                                    " time: " + tmSec + " sec distance: " + ((int) Math.round(tDistance)) + " m");
                        }
                    }
                }
            }
            mUserZones.put( toe.getCurrentOwnerId(), toe);
        }


        mLogger.debug("DayRanking (update zones) Users found: " + tTotUsers +
                        " tot-time: " + Turf.formatTimeDiff((tTotTime * 1000L)) +
                        " tot-distance: " + tTotDistance + " m.");


    }

    @Override
    public JsonObject execute(MessageInterface tRqstMsg) {
        if (tRqstMsg instanceof DR_RankingRqst) {
            return executeGetDayRanking((DR_RankingRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof DR_RegionRqst) {
            return executeGetRegions((DR_RegionRqst) tRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method not found for request \"" +
                tRqstMsg.getMessageName() + "\"", null ).toJson();

    }

    private List<DayRankingUser> getCurrentRankingUsers( int pRegionId ) {
        ArrayList<DayRankingUser> tList = new ArrayList<>();
        for( DayRankingUser ru : this.mRankingUsers.values()) {
            if (ru.getRegionId().get() == pRegionId) {
                tList.add(ru);
            }
        }
        return tList;
    }

    private JsonObject executeGetDayRanking( DR_RankingRqst pRqst  ) {
        DR_RankingRsp tResponse = new DR_RankingRsp();
        String tDate = pRqst.getDate().orElse( mCurrentDate );
        int tRegionId = pRqst.getRegionId().orElse(141);
        List<DayRankingRegion> tRegionLst = mDbAux.findDayRankingRegion(  tDate, tRegionId );
        if ((tRegionLst == null) || (tRegionLst.size() == 0)) {
            return TGStatus.create(false,"No day ranking exists for region \"" + pRqst.getRegion().get() + "\" (" + pRqst.getRegionId().get() + ") and date \"" + tDate + "\"").toJson();
        }

        // If the date requested is for the current date we collected the response from the cache
        // If not the response needs to be built from the database
        List<DayRankingUser> tUserList = null;
        if (mCurrentDate.compareTo( tDate ) == 0)
            tUserList = getCurrentRankingUsers( pRqst.getRegionId().get() );
        else {
            Bson tFilter= Filters.and(Filters.eq("date", tDate), Filters.eq("regionId", tRegionId));
            tUserList = mDbAux.findDayRankingUser( tFilter );
        }

        if ((tUserList == null) || (tUserList.size() == 0)) {
            return TGStatus.create(false,"No day ranking users exists for region \"" + pRqst.getRegion().get() + "\" (" + pRqst.getRegionId().get() + ") and date \"" + tDate + "\"").toJson();
        }
        ArrayList<DR_User> drUsers = new ArrayList<>();
        for ( DayRankingUser dru : tUserList) {
            DR_User u = new DR_User();
            u.setActiveZones( dru.getActiveZones().orElse(0));
            u.setPoints( dru.getPoints().orElse(0));
            u.setPph( dru.getPPH().orElse(0));
            u.setTakes( dru.getTakes().orElse(0));
            u.setUser( dru.getUser().get());
            u.setTime( dru.getTime().orElse(0L));

            double tKm = dru.getDistance().orElse(0) / 1000.0d;
            u.setDistance( tKm ); // Distance in Km

            if (dru.getTime().isPresent() && (dru.getTime().get() > 0)) {
                double tSpeed = ((double) dru.getDistance().orElse(0) / ((double) dru.getTime().get()) * 3.6d);
                u.setSpeed(tSpeed);
            } else {
                u.setSpeed( 0d );
            }
            mLogger.debug( u.toJson().toString());
            drUsers.add(u);

        }
        Collections.sort(drUsers, new DayRankingUsertSorter());
        for( int i = 0; i < drUsers.size(); i++) {
            drUsers.get(i).setPlace((i+1));
        }

        DayRankingRegion dr = tRegionLst.get(0);


        tResponse.setRegion( dr.getRegionName().get());
        tResponse.setRegionId( dr.getRegionId().get());
        tResponse.setStartTime( HH_MM_SS_FORMAT.format( dr.getStartTime().get()));
        tResponse.setUsers( drUsers.subList(0, Math.min(drUsers.size(), mConfig.getMaxDisplayUsers())));
        return tResponse.toJson();
    }

    private JsonObject executeGetRegions( DR_RegionRqst pRqst ) {
        DR_RegionRsp tResponse = new DR_RegionRsp();
        ArrayList<DR_Region> tRegionList = new ArrayList<>();
        for( CfgRegion r : mConfig.getRegions()) {
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
        return tResponse.toJson();

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
                mRegionDbSaveTimestamps.put( r.getRegionId().get(), 0L);
            }
            mLogger.info("Loaded Region Ranking Data from DB (" + mCurrentDate + "), regions loaded: " + tDbRegionList.size() + " users: " + mRankingUsers.size());
        } else {
             mLogger.info("No Start Ranking Region Data for  (" + mCurrentDate + ") was found in database");
        }


        // Load user start data for current date
        Bson tFilter= Filters.eq("date", mCurrentDate);
        List<DayRankingInitUser> tDbInitUserList = mDbAux.findDayRankingInitUser( tFilter );
        if ((tDbInitUserList != null) && (tDbInitUserList.size() > 0)) {
            tDbInitUserList.stream().forEach( u -> mRankingInitUsers.put( u.getUserId().get(), u));
            mLogger.info("Loaded User Init Ranking Data from DB (" + mCurrentDate + "), users loaded: " + tDbInitUserList.size());
        } else {
            mLogger.info("No Start Init Ranking User Data for  (" + mCurrentDate + ") was found in database");
        }

        // Load last known user data for current date
        tFilter= Filters.eq("date", mCurrentDate);
        List<DayRankingUser> tDbUserList = mDbAux.findDayRankingUser( tFilter );
        if ((tDbUserList != null) && (tDbUserList.size() > 0)) {
            tDbUserList.stream().forEach( u -> mRankingUsers.put( u.getUserId().get(), u));
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


    /**
     * =====================================================================================
     *
     *                  RefreshRankingThread
     *
     * =====================================================================================
     */

    class RefreshRankingThread extends Thread {
        DayRankingService mService;
        Object tWaitObject = new Object();

        RefreshRankingThread( DayRankingService pService) {
            mService = pService;

        }

        List<JsonObject> getUserRanking(CfgRegion pRegion ) {
            ArrayList<JsonObject> jUsers = new ArrayList<>();


            int tX = 1, tY = mService.mConfig.getMaxUsers();
            boolean tNoMore = false;
            try {
                while( (!tNoMore) && (tX < tY)) {


                    String tPostRqst = "{\"region\": \"" + pRegion.getName() + "\", \"from\":" + tX + " , \"to\": " + tY + " }";


                    //mService.mLogger.info("user/top request rqst: \"" + tPostRqst + "\"");

                    JsonElement jElement = Turf.getInstance().turfServerPOST("users/top", tPostRqst, mLogger);
                    if (jElement == null) {
                        return jUsers;
                    }

                    JsonArray jArr = jElement.getAsJsonArray();
                    for (int i = 0; i < jArr.size(); i++) {
                        TurfUser tu = new TurfUser( jArr.get(i).getAsJsonObject());
                        jUsers.add(jArr.get(i).getAsJsonObject());
                    }
                    tX = jUsers.size() + 1;
                    if (jArr.size() == 0) {
                        tNoMore = true;
                    }
                }
                return jUsers;
            }
            catch( Throwable e) {
                mService.mLogger.error("Failed to retrieve user day ranking from Turf service", e);
                return jUsers;
            }
        }

        private DayRankingInitUser createRankingInitUser(TurfUser pUser) {
            DayRankingInitUser rusi = new DayRankingInitUser();
            rusi.setDate( mService.mCurrentDate );
            rusi.setInitPoints( pUser.getPoints());
            rusi.setInitTakes( pUser.getTaken());
            rusi.setUser( pUser.getUserName());
            rusi.setUserId( pUser.getUserId());
            return rusi;
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
            ru.setDate( mService.mCurrentDate );
            return ru;
        }
        private void setStartDayRankingData( CfgRegion pRegion, List<JsonObject> tUserArray ) {
                mLogger.debug("setStartDayRankingData, set start of day ranking day region: " + pRegion.getName() + " users: " + tUserArray.size());

                DayRankingRegion drr = new DayRankingRegion();
                drr.setDate(mService.mCurrentDate);
                drr.setRegionId(pRegion.getId());
                drr.setRegionName(pRegion.getName());
                drr.setFinalized(false);
                drr.setStartTime(System.currentTimeMillis());
                mService.mRankingRegions.put(pRegion.getId(), drr);
                mService.mRegionDbSaveTimestamps.put(pRegion.getId(), 0L);
                mService.mDbAux.insertDayRankingRegion(drr);


                for (int i = 0; i < tUserArray.size(); i++) {
                    TurfUser tUser = new TurfUser(tUserArray.get(i));
                    DayRankingInitUser rusi = createRankingInitUser(tUser);
                    if (!mService.mRankingInitUsers.containsKey(rusi.getUserId().get())) {
                        mService.mRankingInitUsers.put(rusi.getUserId().get(), rusi);
                        mService.mDbAux.insertDayRankingInitUser(rusi);
                    }
                }
        }

        private boolean newDay() {
            String tDate = mService.DATE_FORMAT.format( System.currentTimeMillis());
            if (mService.mCurrentDate.compareTo(tDate) == 0) {
               return false;
            }
            return true;
        }

        private void finalizeDay() {
            for( DayRankingRegion drr : mService.mRankingRegions.values()) {
                mService.mDbAux.updateDayRankingRegion( drr, true);
            }
            for(DayRankingUser ru : mService.mRankingUsers.values()) {
                ru.setFinalized( true );
                mService.mDbAux.updateDayRankingUser( ru, true );

            }
            mService.mRankingUsers.clear();
            mService.mRankingInitUsers.clear();
            mService.mRankingRegions.clear();
            mService.mLogger.info(" FINALIZED Date: " + mService.mCurrentDate );
            mService.mCurrentDate = mService.DATE_FORMAT.format( System.currentTimeMillis());
        }

        private DayRankingUser createDayRankingUser( TurfUser pTurfUser) {
            DayRankingUser ru = new DayRankingUser();
            ru.setFinalized( false );
            ru.setLatestTakeTime(0L);
            ru.setDistance(0);
            ru.setTime(0L);
            ru.setDate( mCurrentDate );
            ru.setUser(pTurfUser.getUserName());
            ru.setUserId( pTurfUser.getUserId());
            ru.setRegionId( pTurfUser.getRegionId());
            ru.setPlace(0);
            return ru;
        }

        private int updateRankingData(CfgRegion pRegion, List<JsonObject> tUserArray ) {
            int tUpd = 0, tIns = 0, tFnd = 0;
            long tDistance = 0, tTime = 0;
            boolean tUserChanged;
            long tNow = System.currentTimeMillis();
            boolean tDBSave = false;
            if (mRegionDbSaveTimestamps.containsKey( pRegion.getId())) {
                if ((mRegionDbSaveTimestamps.get(pRegion.getId()) + mService.mConfig.getSaveIntervalMS()) < tNow) {
                    tDBSave = true;
                    mRegionDbSaveTimestamps.put(pRegion.getId(), tNow);
                }
            }


            for (int i = 0; i < tUserArray.size(); i++) {
                TurfUser tu = new TurfUser(tUserArray.get(i));

                DayRankingInitUser rusi = mService.mRankingInitUsers.get(tu.getUserId());
                if (rusi != null) {
                    tFnd++;
                    DayRankingUser ru = mService.mRankingUsers.get(tu.getUserId());
                    if (ru == null) {
                        ru = createDayRankingUser(tu);
                        mLogger.trace("New add user: " + tu.getUserName() + " region: " + tu.getRegionName() + " to UserDayRanking");
                        mService.mRankingUsers.put(tu.getUserId(), ru);
                        tIns++;
                    }

                    tUserChanged = isUserChanged(ru, tu, rusi);
                    tDistance += ru.getDistance().orElse(0);
                    tTime += ru.getTime().orElse(0L);
                    ru.setActiveZones(tu.getActiveZones());
                    ru.setRegionId(tu.getRegionId());
                    ru.setPoints(tu.getPoints() - rusi.getInitPoints().get());
                    ru.setPPH(tu.getPPH());
                    ru.setTakes(tu.getTaken() - rusi.getInitTakes().get());
                    if (tUserChanged) {
                        tUpd++;
                        if ((tDBSave) && (tUserChanged)) {
                            mDbAux.updateDayRankingUser(ru, true);
                        }
                    }
                }
            }
            long tExecTime = System.currentTimeMillis() - tNow;
            if (mConfig.getDebug()) {

                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                mService.mLogger.debug("<updateRankingData> region: " + pRegion.getName() + " int-found: " + tFnd + " updates: " + tUpd + " insert: " + tIns +
                        " dbSave: " + tDBSave + " exec-time: " + tExecTime + " ms. (turf-usr-arr: " + tUserArray.size() + ") \n                                    " +
                        " (ranking-users: " + mRankingUsers.size() +  ") (ranking-init-users: " + mRankingInitUsers.size() +
                        ") users dist: " + tDistance + " users time: " + tTime );


            }
            return tUpd;
        }


        private boolean isUserChanged( DayRankingUser ru, TurfUser tu, DayRankingInitUser rusi) {
            int tPoints = tu.getPoints() - rusi.getInitPoints().get();
            if (tPoints != ru.getPoints().orElse(0)) {
                return true;
            }

            int tTakes = tu.getTaken() - rusi.getInitTakes().get();
            if (tTakes != ru.getTakes().orElse(0)) {
                return true;
            }

            if (tu.getPPH() != ru.getPPH().orElse(0)){
                return true;
            }

            if (tu.getActiveZones() != ru.getActiveZones().orElse(0)){
                return true;
            }
            return false;
        }

        void refresh( long pDismissTime ) { //Frotz
            int tUserUpdated = 0, tTurfUsers = 0;
            // Check if we passed date boundary, if so finalize date and initialize for a new day
            boolean tForceNewDay = false;
            if (newDay() || tForceNewDay) {
                finalizeDay();
            }

            // get regions that are tracked
            List<CfgRegion> mRegions = mService.mConfig.getRegions();

            for( CfgRegion r : mRegions ) {
                // Get current user ranking for the region
                List<JsonObject> tUserRanking = getUserRanking( r );
                if (tUserRanking != null) {
                    tTurfUsers += tUserRanking.size();
                    // Check if Ranking Start Data exists
                    if (!mService.mRankingRegions.containsKey( r.getId())) {
                        setStartDayRankingData( r, tUserRanking );
                    } else {
                        tUserUpdated += updateRankingData(r, tUserRanking);
                    }
                } else {
                    mService.mLogger.info("No ranking data found for region: " + r.getName() );
                }
            }


            mService.mLogger.debug("<updateUserRanking> turf-users: " + tTurfUsers + " updated users: " + tUserUpdated + " dismissTime: " + pDismissTime );

        }

        private long calcDismissTime( long pWaitTimeMs ) {
            long tNow = System.currentTimeMillis();
            long tRem = (tNow % 60000L);
            long tSleepTimeMs = (pWaitTimeMs - tRem) + 500L;
            return  tSleepTimeMs;
        }



        @Override
        public void run() {
            long tDismissTime = 0;
            mService.mLogger.info("Start DayRankingService refresh thread");
            while( true ) {
                refresh(tDismissTime);
                try {
                    tDismissTime = calcDismissTime(mService.mConfig.getRefreshUserIntervalMS());
                    Thread.sleep(tDismissTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

    }
}
