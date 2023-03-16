package com.hoddmimes.turf.server.services.regionstat.heatmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.generated.HM_ZoneRqst;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.*;
import com.hoddmimes.turf.server.configuration.ZoneHeatMapConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.TurfActivityZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hoddmimes.turf.server.common.Turf.TurfSDF;

public class ZoneHeatMapService extends Thread implements TurfServiceInterface {

    private volatile boolean mInitDone;
    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private MongoAux mDbAux;
    private EventFilterNewZoneTakeOver mZoneFilter = null;
    private List<Integer> mRegionsOfInterest;
    public ZoneHeatMapService( ) {
        mLogger = LogManager.getLogger( this.getClass().getName());
    }



    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mTurfIf = pTurfServerInterface;
        mZoneFilter = new EventFilterNewZoneTakeOver();
        mDbAux = mTurfIf.getDbAux();

        mInitDone = false;
        this.start();
    }

    public void run() {
        long tExecTimeMs = System.currentTimeMillis();
        mLogger.info("Starting initial heat-map zone update...");
        updateZonesInDb();
        tExecTimeMs = System.currentTimeMillis() -tExecTimeMs;
        mLogger.info("Finishing initial heat-map zone update exec-time: " + (tExecTimeMs) + " ms.");
        mInitDone = true;
    }



    private void updateZonesInDb() {
        List<TurfZone> tZones = new ArrayList<>();
        TurfActivityZone taz = null;
        int tUpdatesZ = 0, tNewZ = 0;
        ZoneHeatMapConfiguration tConfig = mTurfIf.getServerConfiguration().getZoneHeatMapConfiguration();
        mRegionsOfInterest = tConfig.getRegions();

        // Request current status for all zones for the regions we have interest in
        for(int tRegionId : mRegionsOfInterest)
        {
            JsonArray jRegionIdArr = new JsonArray();
            List<TurfZone> tRegionZoneList = mTurfIf.getZonesByRegionId( tRegionId );
            for( TurfZone tz : tRegionZoneList) {
                JsonObject jZoneId = new JsonObject();
                jZoneId.addProperty("id", tz.getId());
                jRegionIdArr.add(jZoneId);
            }
            JsonArray jZoneArray = Turf.getInstance().turfServerPOST("zones", jRegionIdArr.toString(),mLogger).getAsJsonArray();
            for( int i = 0; i < jZoneArray.size(); i++) {
                TurfZone z = new TurfZone( jZoneArray.get(i).getAsJsonObject());
                tZones.add(z);
            }
        }


        mLogger.info("Got " + tZones.size() + " heat map zones loaded");

        for( TurfZone tZone : tZones) {
            List<TurfActivityZone> tTAZList = mDbAux.findTurfActivityZone(tZone.getRegionid(), tZone.getId());
            if ((tTAZList != null) && (tTAZList.size() > 0)) {
                 taz = tTAZList.get(0);
                 if (tZone.getLastDateTaken() != null) {
                    taz.setDateLastTaken( tZone.getLastDateTaken() );
                    taz.setUser( tZone.getOwner());
                    mDbAux.updateTurfActivityZone(taz, true);
                    tUpdatesZ++;
                }
            } else {
                // Zone is not found in DB
                taz = new TurfActivityZone();
                taz.setDateLastTaken(tZone.getLastDateTaken());
                taz.setZoneId( tZone.getId());
                taz.setUser( tZone.getOwner());
                taz.setLat( tZone.getLatitude());
                taz.setLong( tZone.getLong());
                taz.setRegionId( tZone.getRegionid());
                taz.setTakeovers(0L);
                taz.setTotHoldTimeSec(0L);
                mDbAux.updateTurfActivityZone(taz, true);
                tNewZ++;
            }
        }
        mLogger.info(" new zones: " + tNewZ + " updated zones: " + tUpdatesZ );
    }

    private long holdTimeInSeconds( String pLatestTakeTime, String pCurrentTakeTime ) {
        try {
            long t1 = TurfSDF.parse(pLatestTakeTime).getTime();
            long t2 = TurfSDF.parse(pCurrentTakeTime).getTime();
            long tDiff = (t2 - t1) / 1000L;
            return (tDiff <= 0L) ? 0L : tDiff;
        }
        catch( ParseException e) {
            mLogger.error("holdTimeInMinutes", e);
            return 0L;
        }
    }

    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
        List<ZoneEvent> tTakeOverEvents = mZoneFilter.getNewTakeover(pZoneUpdates.getAsJsonArray());
        for( ZoneEvent  tZoneEvent : tTakeOverEvents) {
            if ( mRegionsOfInterest.contains(tZoneEvent.getRegionId())) {
                List<TurfActivityZone> tTAZList = mDbAux.findTurfActivityZone(tZoneEvent.getRegionId(), tZoneEvent.getZoneId());
                if ((tTAZList != null) && (tTAZList.size() > 0)) {
                    TurfActivityZone taz = tTAZList.get(0);
                    long tDiffSec = holdTimeInSeconds(taz.getDateLastTaken().get(), tZoneEvent.getLatestTakeTime());
                    if (tDiffSec > 0) {
                        taz.setDateLastTaken(tZoneEvent.getLatestTakeTime());
                        taz.addHoldSec(tDiffSec);
                        taz.setUser( tZoneEvent.getCurrentOwner());
                        mDbAux.updateTurfActivityZone( taz, true);
                    } else {
                        mLogger.warn("Take over time less or equal to zero seconds");
                    }
                }
            }
        }
    }

    private long getCurrentHoldTimeSec( String pLatestTakeTime ) {
        try {
            Date d = TurfSDF.parse(pLatestTakeTime);
            return (System.currentTimeMillis() - d.getTime()) / 1000L;
        }
        catch( ParseException e) {
            throw new RuntimeException( e );
        }
    }

    private String formatSecToTime( long pSec ) {
        long tHour = pSec / 3600L;
        long tMin = (pSec -(3600L * tHour)) / 60;
        long tSec =  (pSec -(3600L * tHour) - (60L * tMin));
        return String.format("%4d:%02d:%02d", tHour, tMin, tSec);
    }

    private List<MapDataLayer> getMapLayers() {
        List<ZoneHeatMapConfiguration.ColorMap> tColorMaps = mTurfIf.getServerConfiguration().getZoneHeatMapConfiguration().getColorMaps();
        List<MapDataLayer> tMapLayers = new ArrayList<>();
        for(ZoneHeatMapConfiguration.ColorMap cp : tColorMaps) {
            tMapLayers.add( new MapDataLayer(cp.getUpperTakeOverTimeSec(), cp.getColorValue()));
        }
        return tMapLayers;
    }

    private void addZoneToMapLayer( TurfActivityZone taz, List<MapDataLayer> tMapLayes, boolean pCurrentFlg) {
        if ((!pCurrentFlg) && (taz.getTakeovers().get() <= 0)) {
            return;
        }

        if (!taz.getDateLastTaken().isPresent()) {
            return;
        }

        for( MapDataLayer tMapLayer :tMapLayes ) {
            long tSec = (pCurrentFlg) ? getCurrentHoldTimeSec( taz.getDateLastTaken().get()) :
                    (taz.getTotHoldTimeSec().get() / taz.getTakeovers().get());


            if (tMapLayer.isWithinBoundary( tSec )) {
                tMapLayer.addZone(taz, tSec);
                return;
            }
        }
        // We should never end up here. There should be a catch-all layer with
        // upper limit eq MAX_LONG
        throw new RuntimeException("Wow unexpected could not find a map layer");
    }

    @Override
    public JsonObject execute(MessageInterface pRqstMsg) {
        HM_ZoneRqst tRqstMsg = (HM_ZoneRqst) pRqstMsg;
        boolean tCurrentFlg = (tRqstMsg.getType().get().toUpperCase().contentEquals("CURRENT")) ? true : false;

        if (!mInitDone) {
            mLogger.info("Heat Map initialization is no ready");
            JsonObject jResponse = new JsonObject();
            JsonObject jBody = new JsonObject();
            jBody.add("mapLayers", new JsonArray());
            jResponse.add("HM_ZoneRsp", jBody);
            return jResponse;
        }



        List<MapDataLayer> tMapLayers = getMapLayers();

        List<TurfActivityZone> tZones = mDbAux.findAllTurfActivityZone();
        for( TurfActivityZone taz : tZones) {
           addZoneToMapLayer( taz, tMapLayers, tCurrentFlg);
        }

        JsonArray jArr = new JsonArray();
        for( MapDataLayer mdl : tMapLayers ) {
            if (mdl.hasData()) {
                jArr.add(mdl.mapLayerToJson());
            }
        }

        JsonObject jResponse = new JsonObject();
        JsonObject jBody = new JsonObject();
        jBody.add("mapLayers", jArr);
        jResponse.add("HM_ZoneRsp", jBody);
        return jResponse;
    }

    class MapDataLayer
    {
        RGB mColor;
        long     mUpperBoundarySec;
        JsonArray mGeoPositions;


        MapDataLayer(  long pUpperBoundarySec, String pColorValue) {
            mColor = new RGB(pColorValue); // #542e7b
            mUpperBoundarySec = pUpperBoundarySec;
            mGeoPositions = new JsonArray();
        }

        boolean isWithinBoundary( long pHoldSec ) {
            return (pHoldSec <= mUpperBoundarySec) ? true : false;
        }

        boolean hasData() {
            return (mGeoPositions.size() > 0) ? true : false;
        }
        void addZone( TurfActivityZone taz, long pSec ) {
            JsonObject jPos = new JsonObject();
            jPos.addProperty("type", "Feature");

            JsonObject jProp = new JsonObject();
            jProp.addProperty("message", taz.getUser().orElse("") + " " + formatSecToTime( pSec ));
            JsonArray jSizeArray = new JsonArray();
            jSizeArray.add(32); // Icon size 32 pixels
            jSizeArray.add(32); // Icon size 32 pixels
            jProp.add("iconSize", jSizeArray);
            jPos.add("properties", jProp );

            JsonObject jGeo = new JsonObject();
            jGeo.addProperty("type","Point");
            JsonArray jLatLong = new JsonArray();
            jLatLong.add(taz.getLong().get());
            jLatLong.add(taz.getLat().get());
            jGeo.add("coordinates", jLatLong);
            jPos.add("geometry", jGeo );

            mGeoPositions.add( jPos );
        }

        JsonElement mapLayerToJson() {
            JsonObject jMapBoxData = new JsonObject();
            jMapBoxData.addProperty("type","FeatureCollection");
            jMapBoxData.add("features", mGeoPositions );


            JsonObject jMapLayer = new JsonObject();
            jMapLayer.add("color", mColor.toJson() );
            jMapLayer.addProperty("holdTime", formatSecToTime( mUpperBoundarySec));
            jMapLayer.add("mapBoxData", jMapBoxData);

            return jMapLayer;
        }

    }


}
