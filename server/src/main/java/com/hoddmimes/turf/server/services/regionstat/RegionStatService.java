package com.hoddmimes.turf.server.services.regionstat;

/*
  Logic to be implemeted
  - Load configuration
  - Load statistics from database
  - Process zone updates

 */


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.RS_RegionStatisticsRqst;
import com.hoddmimes.turf.common.generated.RS_RegionStatisticsRsp;
import com.hoddmimes.turf.common.generated.RegionStatistics;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.RegionStatConfiguration;
import com.hoddmimes.turf.server.generated.HourRegionStat;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.RegionStat;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.NumberFormat;
import java.util.*;

public class RegionStatService implements TurfServiceInterface
{

    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private EventFilterNewZoneTakeOver tZoneFilter = null;
    private MongoAux mDbAux;
    private RegionStatConfiguration mConfig = null;
    private DebugContext mDbgCtx = null;

    private HashMap<Integer, RegionStat> mRegions;
    private HashMap<Integer, User>       mUsers;
    private HashMap<Integer, ZoneEvent>  mZones;

    private NumberFormat mNBF;

    public RegionStatService() {
        mLogger = LogManager.getLogger( this.getClass().getSimpleName());
        mRegions = new HashMap<>(237);
        mUsers = new HashMap<>( 1237 );
        mZones = new HashMap<>( 3137 );
        mNBF = NumberFormat.getInstance();
        mNBF.setMaximumFractionDigits(1);
        mNBF.setMinimumFractionDigits(1);
        mNBF.setGroupingUsed(false);
    }

    public String execute( MessageInterface tRqstMsg )
    {
        if (tRqstMsg instanceof RS_RegionStatisticsRqst) {
            return executeGetStatistics((RS_RegionStatisticsRqst) tRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method found for request \"" +
                tRqstMsg.getMessageName() + "\"", null ).toJson().toString();

    }



    private String executeGetStatistics(RS_RegionStatisticsRqst pRqstMsg)
    {
        if (pRqstMsg.getIsTotalRequest().orElse( true)) {
            return executeGetTotalStatistics(pRqstMsg);
        } else {
            return executeGetHourStatistics(pRqstMsg);
        }
    }

    String executeGetHourStatistics(RS_RegionStatisticsRqst pRqstMsg) {
        RS_RegionStatisticsRsp tResponse = new RS_RegionStatisticsRsp();
        ArrayList<RegionStatistics> tReginRsponseList = new ArrayList<>();

        for( RegionStat rs : this.mRegions.values()) {
            RegionStatistics rsRsp = buildHourStatisticResponse( rs );
            tReginRsponseList.add( rsRsp );
        }
        calculateRatingFactors( tReginRsponseList );

        long tTotalSamples = this.mRegions.values().stream().mapToLong( r -> r.getTotTakes().orElse(0L)).sum();
        tResponse.addRegionStats( tReginRsponseList );
        tResponse.setPeriodHH(mConfig.getPeriodMS() / (3600L * 1000L));
        tResponse.setTotalSamples( tTotalSamples );
        return tResponse.toJson().toString();
    }

    String executeGetTotalStatistics(RS_RegionStatisticsRqst pRqstMsg) {
        RS_RegionStatisticsRsp tResponse = new RS_RegionStatisticsRsp();
        ArrayList<RegionStatistics> tReginRsponseList = new ArrayList<>();

        for( RegionStat rs : this.mRegions.values()) {
            RegionStatistics rsRsp = buildGlobalStatisticResponse( rs );
            tReginRsponseList.add( rsRsp );
        }

        /*
         * Calculate PPH, TP and region factor factor
         */
        calculateRatingFactors( tReginRsponseList );

        long tTotalSamples = this.mRegions.values().stream().mapToLong( r -> r.getTotTakes().orElse(0L)).sum();
        tResponse.addRegionStats( tReginRsponseList );
        tResponse.setPeriodHH(0L);
        tResponse.setTotalSamples( tTotalSamples );
        return tResponse.toJson().toString();
    }

    private RegionStatistics buildHourStatisticResponse( RegionStat r  ) {
        LinkedList<HourRegionStat> tHHList = (LinkedList<HourRegionStat>) r.getHoursStat().orElse( new LinkedList<HourRegionStat>());
        RegionStatistics trs = new  RegionStatistics();

        trs.setRegionId(r.getId().get());
        trs.setRegion(r.getName().get());

        trs.setAvgPPHHoldTime( evalAvgPPHHoldTime( tHHList ));
        trs.setAvgPPH( evalAvgPPH( tHHList ));
        trs.setTotOutsideTakes( tHHList.stream().mapToLong( rhs -> rhs.getTotOutsideZones().orElse(0L)).sum());
        trs.setAvgAggregatedPPH( evalAvgAggregatedPPH( tHHList ));
        trs.setAvgDistance(  evalAvgDistance( tHHList ));
        trs.setAvgTime( evalAvgTime( tHHList ));
        trs.setAvgTP( evalAvgTP( tHHList ));
        trs.setTotTakes( tHHList.stream().mapToLong( rhs -> rhs.getTotTakes().orElse(0L)).sum());
        trs.setPphFactor( evalPPHFactor( r ));
        trs.setTpFactor( evalTPFactor( r ));
        return trs;
    }

    private void calculateRatingFactors( List<RegionStatistics> pTurfRegionStat ) {
        /**
         * Fix TP factors. Locate the norm value and adjust comparency
         */
        double tNormTP = Double.MAX_VALUE;
        // find the norm value
        for( RegionStatistics trs : pTurfRegionStat) {
            if ((trs.getTpFactor().get() < tNormTP) && (trs.getTpFactor().get() > 0.0d)) {
                tNormTP = trs.getTpFactor().get();
            }
        }
        for( RegionStatistics trs : pTurfRegionStat) {
            trs.setTpFactor(trs.getTpFactor().get() / tNormTP );
        }

        /**
         * Fix PPH factors. Locate the norm value and adjust comparency
         */
        double tNormPPH = 0;
        // find the norm value
        for( RegionStatistics trs : pTurfRegionStat) {
            if (trs.getPphFactor().get() > tNormPPH) {
                tNormPPH = trs.getPphFactor().get();
            }
        }

        // Adjust the factor to be relative comparency to the norm
        for( RegionStatistics trs : pTurfRegionStat) {
            if (trs.getPphFactor().get() == 0) {
                trs.setPphFactor( 999d );
            } else {
                trs.setPphFactor(tNormPPH / trs.getPphFactor().get());
            }
        }

        // Adjust Calculate region factor based upon TP and PPH factor
        double TP_PPH_RELATION = mConfig.getTpPphRelation();

        double tNormReginon = Double.MAX_VALUE;;
        for( RegionStatistics trs : pTurfRegionStat) {
            //trs.setRegionfactor(((trs.getPPHfactor() / TP_PPH_RELATION) + (trs.getTPfactor() / (1.0d - TP_PPH_RELATION)) / 2.0d));
            double f = (trs.getPphFactor().get() * (1.0d - TP_PPH_RELATION)) + (trs.getTpFactor().get() * TP_PPH_RELATION);
            trs.setRegionFactor( f );
            if (f < tNormReginon) {
                tNormReginon =  f;
            }
        }

        Collections.sort(pTurfRegionStat, new TurfRegionStatisticsFactorSorter());


        // Transform all region factorn to offset one'
        tNormReginon = pTurfRegionStat.get(0).getRegionFactor().get();
        for( RegionStatistics trs : pTurfRegionStat) {
            trs.setRegionFactor(trs.getRegionFactor().get() / tNormReginon );
        }


    }



    private RegionStatistics buildGlobalStatisticResponse( RegionStat r ) {
        RegionStatistics trs = new  RegionStatistics();
        trs.setRegionId(r.getId().get());
        trs.setRegion(r.getName().get());

        trs.setAvgPPHHoldTime( evalAvgPPHHoldTime( r ));
        trs.setAvgPPH( evalAvgPPH( r ));
        trs.setTotOutsideTakes( r.getTotOutsideZones().orElse(0L));
        trs.setAvgAggregatedPPH( evalAvgAggregatedPPH( r ));
        trs.setAvgDistance(  evalAvgDistance( r ));
        trs.setAvgTime( evalAvgTime( r ));
        trs.setAvgTP( evalAvgTP( r ));
        trs.setTotTakes( r.getTotTakes().orElse(0L));
        trs.setPphFactor( evalPPHFactor( r ));
        trs.setTpFactor( evalTPFactor( r ));

        return trs;
    }


    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
        long tStartTime = System.currentTimeMillis();

        List<ZoneEvent> tTakeOverEvents = tZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());


        if (mDbgCtx.ifDebug(DebugContext.TAKE_OVER_FEED_VERBOSE)) {
            for (ZoneEvent toe : tTakeOverEvents) {
                mLogger.debug("take-over-time: " + Turf.SDFSimpleTime.format(toe.getLatestTakeOverTime()) +
                        " zoneId: " + toe.getZoneId() + " zoneName: " + toe.getZoneName() +
                        " takeOvers: " + toe.getTakeoverCount() + " user: " + toe.getCurrentOwner());
            }
        }
        if (mDbgCtx.ifDebug(DebugContext.TAKE_OVER_FEED)) {
            if (tTakeOverEvents.size() >=2) {
                long tSec = (tTakeOverEvents.get( tTakeOverEvents.size() - 1).getLatestTakeOverTime() - tTakeOverEvents.get(0).getLatestTakeOverTime() ) / 1000L;
                mLogger.debug(
                        String.format("---- takeOverEvent: %3d newTakeOverEvents: %3d timeSpan: %4d sec ----",
                                pZoneUpdates.getAsJsonArray().size(),
                                tTakeOverEvents.size(),
                                tSec ));
            }
        }



        for (RegionStat rs : mRegions.values()) {
            rs.setChanged(false);
        }

        for (ZoneEvent toe : tTakeOverEvents) {
            RegionStat tRegion = mRegions.get(toe.getRegionId());
            if ((tRegion != null) && (tRegion.getTracked().get())) {
                // Update calc user stat
                User tUser = mUsers.get(toe.getCurrentOwnerId());
                if (tUser == null) {
                    tUser = new User(toe.getCurrentOwnerId(), toe.getCurrentOwner());
                    tUser.mLastZone = toe;
                    mUsers.put(tUser.mId, tUser);
                } else {
                    updateRegionStatisticsUserTakeOver(tRegion, tUser, toe);
                    tUser.mLastZone = toe;
                    tRegion.setChanged( true );
                }
                // Update PPH statistics from zone takeover
                ZoneEvent tPrevToe = mZones.get(toe.getZoneId());
                if (tPrevToe == null) {
                    mZones.put(toe.getZoneId(), toe);
                } else {
                    if (updateRegionStatisticsZoneTakeOver(tRegion, tPrevToe, toe)) {
                        mZones.put(toe.getZoneId(), toe);
                    }
                }
            }
        }

        int tUpdatedRegions = changedRegionsToDatabase();

        if (mDbgCtx.ifDebug(DebugContext.MEMORY)) {
            int tExecTime = (int) (System.currentTimeMillis() - tStartTime);
            long tFreeMemory = (Runtime.getRuntime().freeMemory() / (1024 * 1024));
            long tUsedMemory = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

            mLogger.debug(String.format("TakeOverEvents: %3d, NewTakeOver: %3d ExecTime: %5d ms, Regions Updated: %2d Free Mem: %3d Used Mem: %3d %n",
                    pZoneUpdates.getAsJsonArray().size(),
                    tTakeOverEvents.size(),
                    tExecTime,
                    tUpdatedRegions,
                    tFreeMemory,
                    tUsedMemory));
        }
    }

    @Override
    public void initialize( TurfServerInterface pTurfServerInterface ) {
        mTurfIf = pTurfServerInterface;
        tZoneFilter = new EventFilterNewZoneTakeOver();

        // Retreive region stat configuration
        mConfig = pTurfServerInterface.getServerConfiguration().getRegionStatConfiguration();

        mDbgCtx = mConfig.getDebugContext();
        mDbgCtx.setLogger( mLogger );
        // Get database handle
        mDbAux = pTurfServerInterface.getDbAux();

        // Load regions
        loadRegionsFromTurfServer();

        // Verify region definiions in DB
        verifyRegionsInDatabase();
    }

    private HourRegionStat getCurentHourStat( RegionStat pRegionStat ) {
        HourRegionStat tHHStat = null;

        if (!pRegionStat.getHoursStat().isPresent()) {
            pRegionStat.setHoursStat( new LinkedList<HourRegionStat>());
        }

        LinkedList<HourRegionStat> tList = (LinkedList<HourRegionStat>) pRegionStat.getHoursStat().get();


        if ((tList.size() == 0) || (tList.getLast().getCreateTime().get() + 3600000L < System.currentTimeMillis())) {
            tHHStat = new HourRegionStat();
            tHHStat.setId( pRegionStat.getId().get());
            tHHStat.setCreateTime( System.currentTimeMillis());
            pRegionStat.addHoursStat( tHHStat );
            mDbAux.insertHourRegionStat( tHHStat );
        } else {
            tHHStat = tList.getLast();
        }
        return tHHStat;
    }

    private void updateRegionStatisticsUserTakeOver( RegionStat pRegionStat, User pUser, ZoneEvent toe) {
        ZoneEvent z = pUser.mLastZone;

        long tTimeDiffSec = (toe.getLatestTakeOverTime() - z.getLatestTakeOverTime()) / 1000L;
        if (tTimeDiffSec < (30 * 60)) { // Outside 30 min ignore completely
            if (tTimeDiffSec <= (12 * 60)) { // If less or equal to 12 min
                long tDistance = Math.round(DistanceCalculator.distance(z.getLat(), z.getLong(), toe.getLat(), toe.getLong()));
                pRegionStat.setTotDistance(tDistance + pRegionStat.getTotDistance().orElse(0L));
                pRegionStat.setTotTakes( pRegionStat.getTotTakes().orElse(0L) + 1 );
                pRegionStat.setTotTime( pRegionStat.getTotTime().orElse(0L) +  tTimeDiffSec );
                pRegionStat.setTotTP( pRegionStat.getTotTP().orElse(0L) + toe.getTP() );
                pRegionStat.setTotPPH( pRegionStat.getTotPPH().orElse(0L) + toe.getPPH());
            } else {

                pRegionStat.setTotOutsideZones( pRegionStat.getTotOutsideZones().orElse(0L) + 1 );
                pRegionStat.setChanged( true );
            }
        }

        /**
         * Update hour statistics, remove hour statistics that expires outside period
         */
        if (pRegionStat.getHoursStat().isPresent()) {
            List<HourRegionStat> tLst = pRegionStat.getHoursStat().get();
            while ((tLst.size() > 0) && (tLst.get(0).getCreateTime().get() + mConfig.getPeriodMS() < System.currentTimeMillis())) {
                HourRegionStat hrs = tLst.remove(0);
                mDbAux.deleteHourRegionStatByMongoId(hrs.getMongoId());
            }
        }

        HourRegionStat ttHHStat = getCurentHourStat(pRegionStat);

        if (tTimeDiffSec < (30 * 60)) {
            if (tTimeDiffSec <= (12 * 60)) { // If less or equal to 12 min
                long tDistance = Math.round(DistanceCalculator.distance(z.getLat(), z.getLong(), toe.getLat(), toe.getLong()));
                ttHHStat.setTotDistance( ttHHStat.getTotDistance().orElse(0L) +  tDistance );
                ttHHStat.setTotTakes( ttHHStat.getTotTakes().orElse(0L) + 1);
                ttHHStat.setTotTime( ttHHStat.getTotTime().orElse(0L) + tTimeDiffSec );
                ttHHStat.setTotTP( ttHHStat.getTotTP().orElse(0L) + toe.getTP());
                ttHHStat.setTotPPH( ttHHStat.getTotPPH().orElse(0L) + toe.getPPH());
            } else {
                ttHHStat.setTotOutsideZones( ttHHStat.getTotOutsideZones().orElse(0L) + 1);
            }
        }
    }


    private boolean updateRegionStatisticsZoneTakeOver( RegionStat pRegionStat, ZoneEvent prev_toe, ZoneEvent toe) {

        if (toe.getLatestTakeOverTime() > prev_toe.getLatestTakeOverTime() && toe.getCurrentOwnerId() != prev_toe.getCurrentOwnerId())
        {
            long tTimeDiffSec = (toe.getLatestTakeOverTime() - prev_toe.getLatestTakeOverTime()) / 1000L; //Note!! seconds
            pRegionStat.setTotPPHChanges( pRegionStat.getTotPPHChanges().orElse(0L) + 1);
            pRegionStat.setTotPPHTime( pRegionStat.getTotPPHTime().orElse(0L) + tTimeDiffSec);
            double pnts = (((double) tTimeDiffSec / (double) 3600.0d) * (double) prev_toe.getPPH());
            pRegionStat.setTotPPHTimePoints( pRegionStat.getTotPPHTimePoints().orElse(0d) + pnts );

            HourRegionStat tHHStat = getCurentHourStat( pRegionStat );
            tHHStat.setTotPPHChanges( tHHStat.getTotPPHChanges().orElse(0L) + 1);
            tHHStat.setTotPPHTime( tHHStat.getTotPPHTime().orElse(0L) + tTimeDiffSec);
            tHHStat.setTotPPHTimePoints( tHHStat.getTotPPHTimePoints().orElse(0d) + pnts );

            return true;
        }
        return false;
    }

        private int changedRegionsToDatabase() {
        int tUpdates = 0;
        try {
            for (RegionStat r : mRegions.values()) {
                if (r.getChanged().get()) {
                    mDbAux.updateRegionStatByMongoId(r.getMongoId(), r);
                    tUpdates++;

                    if (r.getHoursStat().isPresent() && (r.getHoursStat().get().size() > 0)) {
                        HourRegionStat tHHStat = ((LinkedList<HourRegionStat>) r.getHoursStat().get()).getLast();
                        mDbAux.updateHourRegionStat( tHHStat.getId().get(), tHHStat.getCreateTime().orElse(0L), tHHStat, true );
                    }

                    if (mDbgCtx.ifDebug(DebugContext.STATISTICS)) {
                        mLogger.debug( "region-updated: " + r.toJson().toString() );
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tUpdates;
    }




    private void verifyRegionsInDatabase() {
        int tFoundInDb = 0;
        int tInsertedInDb = 0;

        try {
            List<RegionStat> tDbRegionStats = mDbAux.findAllRegionStat();
            for (RegionStat rsdb : tDbRegionStats) {
                RegionStat tRegion = mRegions.get(rsdb.getId().get());
                if (tRegion != null) {
                    rsdb.setIsInDB( true );
                    tFoundInDb++;
                    Bson tFilter= Filters.and(
                            Filters.eq("id", rsdb.getId().get()),
                            Filters.gte("createTime", mConfig.getPeriodMS()));
                    List<HourRegionStat> mHHRegionStat = mDbAux.findHourRegionStat( tFilter, new Document("creatTime", - 1));

                    if (mHHRegionStat != null) {
                        rsdb.setHoursStat(mHHRegionStat);
                    }
                    /* replace the region stat with what is found in the Database */
                    mRegions.put(rsdb.getId().get(), rsdb );
                }
            }
            for (RegionStat rs : mRegions.values()) {
                if ((rs.getTracked().get()) && (!rs.getIsInDB().orElse(false))) {
                    this.mDbAux.insertRegionStat(rs);
                    tInsertedInDb++;
                }
            }
            mLogger.info("Regions verified in database, inserted: " + tInsertedInDb + " found in database: " + tFoundInDb );
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRegionsFromTurfServer() {
        int	pTrackedRegions = 0;
        JsonElement tElement = Turf.turfServerGET("regions", true);
        if (tElement != null) {
            JsonArray tRegionArray = tElement.getAsJsonArray();
            if (tRegionArray != null) {
                for (int i = 0; i < tRegionArray.size(); i++) {
                    JsonObject tRegionObject  = tRegionArray.get(i).getAsJsonObject();
                    int tId = tRegionObject.get("id").getAsInt();
                    String tName = tRegionObject.get("name").getAsString();
                    boolean tInludeFlag = false;
                    if (((tRegionObject.get("country") != null) &&
                            (tRegionObject.get("country").getAsString().compareToIgnoreCase("se") == 0)) ||
                            (ifExtraRegion( tId, tName))) {
                        tInludeFlag = true;
                        pTrackedRegions++;


                        RegionStat tRegion = new RegionStat();
                        tRegion.setId(tId);
                        tRegion.setName(tName);
                        tRegion.setTracked(tInludeFlag);


                        mRegions.put(tId, tRegion);
                    }
                }
            }
            mLogger.info(String.valueOf(mRegions.size()) + " regions loaded. Statistics will be collected for " + String.valueOf(pTrackedRegions) + " regions.");
        }
    }

    private boolean ifExtraRegion( int pId, String pName ) {
        for (RegionStatConfiguration.ExtraRegion er : mConfig.getExtraRegions()) {
            if (er.isExtraRegion(pId, pName)) {
                return true;
            }
        }
        return false;
    }

    private long evalAvgPPHHoldTime( RegionStat r) {
        // calculate how long in average a zone is owned before taken by someone (getTotPPHTime is seconds)
        if (!r.getTotPPHChanges().isPresent()) {
            return 0;
        }
        long x = Math.round((double) r.getTotPPHTime().orElse(0L) / (double) r.getTotPPHChanges().get());
        return x;
    }

    private double evalAvgPPH( RegionStat r) {
        // calculate the average PPH for zones taken
        if (!r.getTotTakes().isPresent()) {
            return 0d;
        }
        double d = (double) r.getTotPPH().orElse(0L) / (double) r.getTotTakes().get();
        return d;
    }

    private long evalAvgAggregatedPPH( RegionStat r) {
        // calculate the averahe PPH taken for each ownership
        // i.e. avg-pph * avg-pph-hold-time / pph-changes

        if (!r.getTotPPHChanges().isPresent()) {
            return 0l;
        }

        double d = r.getTotPPHTimePoints().orElse(0d) / (double) r.getTotPPHChanges().get();
        return Math.round( d );
    }

    private long evalAvgDistance( RegionStat r) {
        // calculate distance per zone take
        if (!r.getTotTakes().isPresent()) {
            return 0l;
        }
        long x = Math.round( (double) r.getTotDistance().orElse(0L) / (double) r.getTotTakes().get());
        return x;
    }

    private long evalAvgTime( RegionStat r) {
        // calculate timea user spend to ride between zone in average
        if (!r.getTotTakes().isPresent()) {
            return 0l;
        }
        long x = Math.round( (double) r.getTotTime().orElse(0L) / (double) r.getTotTakes().get());
        return x;
    }

    private long evalAvgTP( RegionStat r) {
        // calculate timea user spend to ride between zone in average
        if (!r.getTotTakes().isPresent()) {
            return 0l;
        }
        long x = Math.round( (double) r.getTotTP().orElse(0L) / (double) r.getTotTakes().get());
        return x;
    }

    private double evalPPHFactor( RegionStat r) {
        // calculate PPH factor for the region
        return evalAvgPPH(r) * (double) evalAvgPPHHoldTime(r);
    }

    private double evalTPFactor( RegionStat r) {
        // calculate TP factor average
        if (!r.getTotTakes().isPresent()) {
            return 0d;
        }
        double x = evalAvgTime(r);
        double y = evalAvgTP(r);

        if (y == 0) {
            return 0d;
        }
        return x/y;
    }

    // ========================================================================

    private long evalAvgPPHHoldTime( LinkedList<HourRegionStat> rhl ) {
        // calculate how long in average a zone is owned before taken by someone (getTotPPHTime is seconds)
        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }

        double pphChanges = rhl.stream().mapToDouble( hs -> hs.getTotPPHChanges().orElse(0L)).sum();
        double pphHoldTime = rhl.stream().mapToDouble( hs -> hs.getTotPPHTime().orElse(0L)).sum();

        if (pphChanges == 0) {
            return 0L;
        }
        return Math.round( pphHoldTime / pphChanges);
    }

    private double evalAvgPPH( LinkedList<HourRegionStat> rhl ) {
        // calculate the average PPH for zones taken
        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }
        double pphChanges = rhl.stream().mapToDouble( hs -> hs.getTotPPHChanges().orElse(0L)).sum();
        double pph = rhl.stream().mapToDouble( hs -> hs.getTotPPH().orElse(0L)).sum();

        if (pphChanges == 0) {
            return 0L;
        }
        return pph / pphChanges;
    }

    private long evalAvgAggregatedPPH( LinkedList<HourRegionStat> rhl ) {
        // calculate the averahe PPH taken for each ownership
        // i.e. avg-pph * avg-pph-hold-time / pph-changes

        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }
        double pphChanges = rhl.stream().mapToDouble( hs -> hs.getTotPPHChanges().orElse(0L)).sum();
        double pphTimePoints = rhl.stream().mapToDouble( hs -> hs.getTotPPHTimePoints().orElse(0d)).sum();

        if (pphChanges == 0) {
            return 0L;
        }

        double d = pphTimePoints / (double) pphChanges;
        return Math.round( d );
    }



    private long evalAvgDistance( LinkedList<HourRegionStat> rhl ) {
        // calculate distance per zone take
        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }
        double totTakes = rhl.stream().mapToDouble( hs -> hs.getTotTakes().orElse(0L)).sum();
        double totDist = rhl.stream().mapToDouble( hs -> hs.getTotDistance().orElse(0L)).sum();

        if (totTakes == 0) {
            return 0;
        }

        long x = Math.round( totDist / totTakes);
        return x;
    }

    private long evalAvgTime( LinkedList<HourRegionStat> rhl ) {
        // calculate distance per zone take
        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }
        double totTakes = rhl.stream().mapToDouble( hs -> hs.getTotTakes().orElse(0L)).sum();
        double totTime = rhl.stream().mapToDouble( hs -> hs.getTotTime().orElse(0L)).sum();

        if (totTakes == 0) {
            return 0;
        }

        long x = Math.round( totTime / totTakes);
        return x;
    }

    private long evalAvgTP(LinkedList<HourRegionStat> rhl) {
        // calculate timea user spend to ride between zone in average

        // calculate distance per zone take
        if ((rhl == null) || (rhl.size() == 0)) {
            return 0L;
        }
        double totTakes = rhl.stream().mapToDouble(hs -> hs.getTotTakes().orElse(0L)).sum();
        double totTp = rhl.stream().mapToDouble(hs -> hs.getTotTP().orElse(0L)).sum();

        if (totTakes == 0) {
            return 0;
        }

        long x = Math.round(totTp / totTakes);
        return x;
    }

    private double evalPPHFactor( LinkedList<HourRegionStat> rhl) {
        // calculate PPH factor for the region
        return evalAvgPPH(rhl) * (double) evalAvgPPHHoldTime(rhl);
    }

    private double evalTPFactor( LinkedList<HourRegionStat> rhl) {
        // calculate TP factor average
        double x = evalAvgTime(rhl);
        double y = evalAvgTP(rhl);

        if (y == 0) {
            return 0d;
        }
        return x/y;
    }


    class TurfRegionStatisticsFactorSorter implements Comparator<RegionStatistics>
    {
        @Override
        public int compare(RegionStatistics r1, RegionStatistics r2) {
            if (r1.getRegionFactor().get() > r2.getRegionFactor().get()) {
                return 1;
            } else if (r1.getRegionFactor().get() < r2.getRegionFactor().get()) {
                return -1;
            }
            return 0;
        }
    }

}
