package com.hoddmimes.turf.server.services.density;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.*;
import com.hoddmimes.turf.server.configuration.ZoneHeatMapConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.TurfActivityZone;
import com.hoddmimes.turf.server.services.heatmap.ZoneHeatMapService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.measure.Measure;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import si.uom.SI;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hoddmimes.turf.server.common.Turf.TurfSDF;

public class ZoneDensityService implements TurfServiceInterface {
    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private GeometryFactory mGeometryFactory;

    public ZoneDensityService( ) {
        mLogger = LogManager.getLogger( this.getClass().getSimpleName());
         mGeometryFactory = JTSFactoryFinder.getGeometryFactory();
    }

    @Override
    public void initialize(TurfServerInterface pTurfServerInterface) {
        mTurfIf = pTurfServerInterface;
    }


    @Override
    public void processZoneUpdates(JsonElement pZoneUpdates) {
    }

    @Override
    public JsonObject execute(MessageInterface pRqstMsg) {
        if (pRqstMsg.getMessageName().contentEquals(ZD_DensityRqst.NAME)) {
            return executeDensityRequest((ZD_DensityRqst) pRqstMsg);
        }
        if (pRqstMsg.getMessageName().contentEquals(ZD_ZonesRqst.NAME)) {
            return executeZoneRequest((ZD_ZonesRqst) pRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method not found for request \"" +
                pRqstMsg.getMessageName() + "\"", null ).toJson();
    }

    private JsonObject executeDensityRequest( ZD_DensityRqst pRqstMsg) {
        List<TurfZone> tTurfZones = mTurfIf.getZonesByRegionId(pRqstMsg.getRegionId().get());
        Polygon tPolygonArea = createPolygon(pRqstMsg.getCoordinates().get());

        ZD_DensityRsp tRsp = new ZD_DensityRsp();

        List<TurfZone> tZonesInPolygon = tTurfZones.stream().filter(z -> pointWithinPolygon(z, tPolygonArea)).collect(Collectors.toList());
        double tArea = calcArea(tPolygonArea);

        tRsp.setSquareMeters( (int) Math.round(tArea));
        tRsp.setSquareKM( (double) tArea / (1000.0 * 1000.0));

        if (tZonesInPolygon.size() == 0) {
            tRsp.setZones(0);

            tRsp.setTotalDistance(0);
            tRsp.setTotalDistanceKM(0.0d);

            tRsp.setAvgTP(0.0d);
            tRsp.setAvgPPH(0.0d);
            tRsp.setAvgDist(0);

            tRsp.setZone_sqkm(0.0d);
            tRsp.setTp_sqkm(0.0d);
            tRsp.setPph_sqkm(0.0d);

            tRsp.setZone_km(0.0d);
            tRsp.setTp_km(0.0d);
            tRsp.setPph_km(0.0d);

            return tRsp.toJson();
        }


        PathCost tPathCost = new PathCost(tZonesInPolygon);
        double tTotPathLength = tPathCost.getTotalLength();
        double tTotTP = tZonesInPolygon.stream().mapToDouble(z -> z.getTP()).sum();
        double tTotPPH = tZonesInPolygon.stream().mapToDouble(z -> z.getPPH()).sum();

        tRsp.setZones(tZonesInPolygon.size());
        tRsp.setTotalDistance( (int) Math.round(tTotPathLength));

        double km = (tTotPathLength / 1000.0d);
        tRsp.setTotalDistanceKM( km );

        if (tZonesInPolygon.size() == 1) {
            tRsp.setAvgDist((int) Math.round(tTotPathLength));
        } else {
            tRsp.setAvgDist((int) Math.round(tTotPathLength / (tZonesInPolygon.size() - 1)));
        }
        double skm = tArea / (1000.0d * 1000.0d);
        tRsp.setAvgPPH(tTotPPH / tZonesInPolygon.size());
        tRsp.setAvgTP(tTotTP / tZonesInPolygon.size());

        double sqkm = (double) tArea / (1000.0 * 1000.0);
        tRsp.setZone_sqkm( tZonesInPolygon.size() / sqkm);
        tRsp.setTp_sqkm( tTotTP / sqkm );
        tRsp.setPph_sqkm( tTotPPH / sqkm );

        if (tZonesInPolygon.size() > 1) {
            tRsp.setZone_km((tZonesInPolygon.size() / km));
            tRsp.setTp_km((tTotTP / km));
            tRsp.setPph_km((tTotPPH / km));
        } else {
            tRsp.setZone_km(0.0d);
            tRsp.setTp_km(0.0d);
            tRsp.setPph_km(0.0d);
        }

        //String s = tRsp.toJson().toString();
        return tRsp.toJson();
    }


    private JsonObject executeZoneRequest(ZD_ZonesRqst pRqstMsg) {

        List<TurfZone> tTurfZones = mTurfIf.getZonesByRegionId(pRqstMsg.getRegionId().get());
        MapDataLayer tMapLayer = new MapDataLayer(tTurfZones);

        JsonObject jResponse = new JsonObject();
        JsonObject jBody = new JsonObject();
        jBody.add("features", tMapLayer.mapLayerToJson());
        jResponse.add("ZD_ZonesRsp", jBody);
        return jResponse;
    }










    /** ========================================================================================================
     * Geotools aux methods
     ==========================================================================================================*/

    private Polygon createPolygon(List<ZD_LongLat> pLngLats ) {
        GeometryFactory tGeometryFactory = new GeometryFactory();

        Coordinate[] tCoordinates = new Coordinate[ pLngLats.size()];
        for (int i = 0; i < pLngLats.size(); i++) {
            ZD_LongLat tLngLat = pLngLats.get(i);
            tCoordinates[i] = new Coordinate(tLngLat.getLng().get(), tLngLat.getLat().get());
        }
        // Loop should have been closed by the client
        //tCoordinates[pLngLats.size()] = tCoordinates[0];

        Polygon tPolygon = tGeometryFactory.createPolygon( tCoordinates );
        return tPolygon;
    }

    /*
        Calculate the polygon area and return the area as square meters
     */
    private double calcArea ( Polygon pPolygon )  {
        Point tCentroid = pPolygon.getCentroid();
        String tCode = "AUTO:42001," + tCentroid.getX() + "," + tCentroid.getY();
        try {
            CoordinateReferenceSystem tAuto = CRS.decode(tCode);
            MathTransform tTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, tAuto);
            Polygon tProjed = (Polygon) JTS.transform(pPolygon, tTransform);
            Measure tMeasure = new Measure(tProjed.getArea(), SI.SQUARE_METRE);
            return tMeasure.doubleValue();
        }
        catch( Exception e) {
            mLogger.fatal("Failed to calculate density area", e);
        }
        return 1.0d;
    }

    private boolean pointWithinPolygon( TurfZone tZone, Polygon pPolygon ) {
        Point tPoint = mGeometryFactory.createPoint(new Coordinate(tZone.getLong(), tZone.getLatitude()));
        return pPolygon.contains( tPoint );
    }

    class MapDataLayer
    {
        JsonArray jGeoPositions;

        MapDataLayer( List<TurfZone> pTurfZones ) {
            jGeoPositions = new JsonArray();
            for( TurfZone tz : pTurfZones) {
                addZone( tz );
            }
        }

        private void addZone( TurfZone tz ) {
            JsonObject jPos = new JsonObject();
            jPos.addProperty("type", "Feature");

            JsonObject jProp = new JsonObject();
            jProp.addProperty("message", tz.getName() + " [ " + tz.getTP() + ":" + tz.getPPH() + " ]");
            JsonArray jSizeArray = new JsonArray();
            jSizeArray.add(32); // Icon size 32 pixels
            jSizeArray.add(32); // Icon size 32 pixels
            jProp.add("iconSize", jSizeArray);
            jPos.add("properties", jProp );

            JsonObject jGeo = new JsonObject();
            jGeo.addProperty("type","Point");
            JsonArray jLatLong = new JsonArray();
            jLatLong.add(tz.getLong());
            jLatLong.add(tz.getLatitude());
            jGeo.add("coordinates", jLatLong);
            jPos.add("geometry", jGeo );

            jGeoPositions.add( jPos );
        }

        JsonElement mapLayerToJson() {
           return jGeoPositions;
        }
    }

    class LongLat
    {
        double lng;
        double lat;

        LongLat( double pLng, double pLat ) {
            lng = pLng;
            lat = pLat;
        }
    }

}
