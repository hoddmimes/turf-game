package com.hoddmimes.turf.server.services.density;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.*;
import com.hoddmimes.turf.server.services.density.pathcost.*;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        if (pRqstMsg.getMessageName().contentEquals(ZD_TspSaveRqst.NAME)) {
            return executeTspSaveRequest((ZD_TspSaveRqst) pRqstMsg);
        }
        if (pRqstMsg.getMessageName().contentEquals(ZD_TspLoadZonesRqst.NAME)) {
            return executeTspLoadZonesRequest((ZD_TspLoadZonesRqst) pRqstMsg);
        }
        if (pRqstMsg.getMessageName().contentEquals(ZD_TspRqst.NAME)) {
            return executeTspRequest((ZD_TspRqst) pRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method not found for request \"" +
                pRqstMsg.getMessageName() + "\"", null ).toJson();
    }

    private void dumpZonesInPolygon( List<TurfZone> pZones ) {
        File tFile = new File("polygon-zones.json");
        if (!tFile.exists()) {
            return;
        }
        try {
            PrintWriter fp = new PrintWriter( tFile );
            JsonArray jArr = new JsonArray();
            for( TurfZone tz : pZones ) {
                JsonArray jPoint = new JsonArray();
                jPoint.add( tz.getLong() );
                jPoint.add( tz.getLatitude());
                jArr.add( jPoint );
            }
            fp.println( jArr.toString());
            fp.flush();
            fp.close();
        }
        catch( Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private JsonObject executeTspSaveRequest( ZD_TspSaveRqst pRqstMsg ) {
        Random tRandom = new Random();
       List<TurfZone> tTurfZones = mTurfIf.getZonesByRegionId(pRqstMsg.getRegionId().get());
        Polygon tPolygonArea = createPolygon(pRqstMsg.getCoordinates().get());

        List<TurfZone> tZonesInPolygon = tTurfZones.stream().filter(z -> pointWithinPolygon(z, tPolygonArea)).collect(Collectors.toList());

       File tFile = new File("../tspdata/" + pRqstMsg.getFilename().get());
       JsonArray jArr = new JsonArray();
       if (pRqstMsg.getMaxNodes().get() <= tZonesInPolygon.size()) {
           for( int i = 0; i < pRqstMsg.getMaxNodes().get(); i++ ) {
               JsonArray jPoint = new JsonArray();
               int tIdx = tRandom.nextInt( tZonesInPolygon.size());
               jPoint.add(tZonesInPolygon.get(tIdx).getLong());
               jPoint.add(tZonesInPolygon.get(tIdx).getLatitude());
               jArr.add( jPoint );
               tZonesInPolygon.remove( tIdx );
           }
       } else {
           for( int i = 0; i < tZonesInPolygon.size(); i++ ) {
               JsonArray jPoint = new JsonArray();
               jPoint.add(tZonesInPolygon.get(i).getLong());
               jPoint.add(tZonesInPolygon.get(i).getLatitude());
               jArr.add( jPoint );
           }
       }
       try {
           PrintWriter fp = new PrintWriter( new FileWriter( tFile ));
           fp.println( jArr.toString());
           fp.flush();
           fp.close();
       }
       catch( IOException e) {
           mLogger.error("Failed to save TSP geo points", e);
           ZD_TspSaveRsp tRsp = new ZD_TspSaveRsp();
           tRsp.setStatus("Failed to save TSP geo points, reason: " + e.getMessage());
           return tRsp.toJson();
       }
        ZD_TspSaveRsp tRsp = new ZD_TspSaveRsp();
        tRsp.setStatus("Saved " + jArr.size() + " points" );
        return tRsp.toJson();
    }

    private JsonObject executeDensityRequest( ZD_DensityRqst pRqstMsg) {
        List<TurfZone> tTurfZones = mTurfIf.getZonesByRegionId(pRqstMsg.getRegionId().get());
        Polygon tPolygonArea = createPolygon(pRqstMsg.getCoordinates().get());

        ZD_DensityRsp tRsp = new ZD_DensityRsp();

        List<TurfZone> tZonesInPolygon = tTurfZones.stream().filter(z -> pointWithinPolygon(z, tPolygonArea)).collect(Collectors.toList());

        dumpZonesInPolygon( tZonesInPolygon );


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
            tRsp.setSpAlg("");
            tRsp.setShortestPathJStr("[]"); // Empty array

            return tRsp.toJson();
        }


        AlgNearestNeighbor tNN = new AlgNearestNeighbor();
        tNN.initialize( tZonesInPolygon );



        boolean tSpALgNN = true;
        double tTotPathLength = (tZonesInPolygon.size() == 0) ? 0 : Double.MAX_VALUE;
        for (int i = 0; i < tZonesInPolygon.size(); i++) {
            double tDistance = tNN.getDistance(i);
            if (tDistance < tTotPathLength) {
                tTotPathLength = tDistance;
            }
        }

        double tBestNNDistance = tTotPathLength;
        AlgAnnealing tBestAA = null;
        if (tZonesInPolygon.size() > 1) {
            for (int i = 0; i < 5; i++) {
                AlgAnnealing tAA = new AlgAnnealing(tZonesInPolygon.size() * 2 , 1000000, 0.999995 );
                tAA.initialize( tZonesInPolygon );
                double tAADistance = tAA.getDistance();
                if (tAADistance < tTotPathLength) {
                    tBestAA = tAA;
                    tTotPathLength = tAADistance;
                }
                System.out.println("AA Distance: " + tAADistance + " best NN distance: " + tBestNNDistance );
            }
        }

        List<Integer> tBestPath = null;
        if (tBestAA != null) {
            tRsp.setSpAlg("AA");
            tBestPath = tBestAA.getPath();
        } else{
            tRsp.setSpAlg("NN");
            tBestPath = tNN.getPath();
        }






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
            tRsp.setShortestPathJStr( buildShortestPathJstr( tZonesInPolygon,tBestPath ));
        } else {
            tRsp.setZone_km(0.0d);
            tRsp.setTp_km(0.0d);
            tRsp.setPph_km(0.0d);
            tRsp.setShortestPathJStr("[]");
            tRsp.setSpAlg("");
        }

        //String s = tRsp.toJson().toString();
        return tRsp.toJson();
    }

    private JsonObject executeTspRequest( ZD_TspRqst pRqstMsg) {
        ZD_TspRsp tRsp = new ZD_TspRsp();
        JsonArray jZones = new JsonArray();

        if (pRqstMsg.getNodes().get().size() <= 1) {
            tRsp.setShortestPathJStr("[]");
            tRsp.setShortestPath(0);
            tRsp.setStatusCode( 1 );
            tRsp.setStatusText("To few nodes, path is not possible");
            return tRsp.toJson();
        }

        for( ZD_LongLat zll : pRqstMsg.getNodes().get()) {
            JsonArray jPoint = new JsonArray();
            jPoint.add( zll.getLng().get());
            jPoint.add( zll.getLat().get());
            jZones.add( jPoint );
        }

        String tAlgorithm = pRqstMsg.getAlgorithm().get();

        if (tAlgorithm.contentEquals("AA")) {
            return calcShortestPathAA( jZones, pRqstMsg.getAnnealingSimulations().orElse(4) );
        } else if (tAlgorithm.contentEquals("NN")) {
            return calcShortestPathNN( jZones );
        }

        tRsp.setStatusCode(-1);
        tRsp.setStatusText("Algorithm not yet implemented");
        return tRsp.toJson();
    }

    private JsonObject calcShortestPathAA( JsonArray jZones, int pSimulations ) {
        List<Integer> tBestPath = null;



        double tShortestDistance =  Double.MAX_VALUE;
        for (int i = 0; i < pSimulations; i++) {
            AlgAnnealing tAA = new AlgAnnealing( jZones.size(), 1000000, 0.999995);
            tAA.initialize( jZones );
            double tDistance = tAA.getDistance();
            if (tDistance < tShortestDistance) {
                tShortestDistance = tDistance;
                tBestPath = tAA.getPath();
            }
        }

        ZD_TspRsp tRsp = new ZD_TspRsp();
        tRsp.setShortestPath( (int) Math.round(tShortestDistance));
        tRsp.setShortestPathJStr( buildShortestPathJstr( jZones,tBestPath ));
        tRsp.setStatusText("success");
        tRsp.setStatusCode(0);

        //String s = tRsp.toJson().toString();
        return tRsp.toJson();
    }

    private JsonObject calcShortestPathNN( JsonArray jZones ) {
        List<Integer> tBestPath = null;
        AlgNearestNeighbor tNN = new AlgNearestNeighbor();
        tNN.initialize( jZones );


        double tShortestDistance =  Double.MAX_VALUE;
        for (int i = 0; i < jZones.size(); i++) {
            double tDistance = tNN.getDistance(i);
            if (tDistance < tShortestDistance) {
                tShortestDistance = tDistance;
                tBestPath = tNN.getPath();
            }
        }

        ZD_TspRsp tRsp = new ZD_TspRsp();
        tRsp.setShortestPath( (int) Math.round(tShortestDistance));
        tRsp.setShortestPathJStr( buildShortestPathJstr( jZones,tBestPath ));
        tRsp.setStatusText("success");
        tRsp.setStatusCode(0);

        //String s = tRsp.toJson().toString();
        return tRsp.toJson();
    }



    private String buildShortestPathJstr( JsonArray jZones, List<Integer> pShortestPath ) {
        JsonArray jBestPath = new JsonArray();
        for( Integer i : pShortestPath ) {
            jBestPath.add( jZones.get(i));
        }
        return jBestPath.toString();
    }

    private String buildShortestPathJstr( List<TurfZone> pZonesInPolygon, List<Integer> pShortestPath ) {
        JsonArray jCoordinates = new JsonArray();
        if (pZonesInPolygon.size() != pShortestPath.size()) {
            // Should never happen
            throw new RuntimeException("Zones in polygon (" +pZonesInPolygon.size() + " != zones shortpath (" +pShortestPath + ")" );
        }

        for( Integer i : pShortestPath ) {
            JsonArray jPoint = new JsonArray();
            jPoint.add( pZonesInPolygon.get(i).getLong());
            jPoint.add( pZonesInPolygon.get(i).getLatitude());
            jCoordinates.add( jPoint );
        }

        return jCoordinates.toString();
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

    private JsonObject executeTspLoadZonesRequest(ZD_TspLoadZonesRqst pRqstMsg) {

        JsonArray jZones = null;

        File tFile = new File("../tspdata/" + pRqstMsg.getFilename().get());
        if (!tFile.exists()) {
            ZD_TspLoadZonesRsp tRsp = new ZD_TspLoadZonesRsp();
            tRsp.setStatusText("Could not find coordinate file \"" +pRqstMsg.getFilename().get() + "\"");
            tRsp.setStatusCode( -1 );
            return tRsp.toJson();
        }

        try {
            jZones = JsonParser.parseReader( new FileReader( tFile )).getAsJsonArray();
        }
        catch( IOException e) {
            ZD_TspLoadZonesRsp tRsp = new ZD_TspLoadZonesRsp();
            tRsp.setStatusText("Failed to read coordinate file \"" + pRqstMsg.getFilename().get() + "\" reason: " + e.getMessage());
            tRsp.setStatusCode( -1 );
            return tRsp.toJson();
        }

        MapDataLayer tMapLayer = new MapDataLayer( jZones );

        JsonObject jResponse = new JsonObject();
        JsonObject jBody = new JsonObject();
        jBody.add("features", tMapLayer.mapLayerToJson());
        jBody.addProperty("statusText", "success");
        jBody.addProperty("statusCode", 0);
        jResponse.add("ZD_TspLoadZonesRsp", jBody);
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

        MapDataLayer( JsonArray pJsonZones ) {
            jGeoPositions = new JsonArray();
            for (int i = 0; i < pJsonZones.size(); i++) {
                addPoint( pJsonZones.get(i).getAsJsonArray(), i );
            }
        }

        private void addPoint( JsonArray pJsonPoint, int pIndex ) {
            JsonObject jPos = new JsonObject();
            jPos.addProperty("type", "Feature");

            JsonObject jProp = new JsonObject();
            jProp.addProperty("message", " [ " + pJsonPoint.get(0) + ":" + pJsonPoint.get(1) + " ]  (" + pIndex + ")");
            JsonArray jSizeArray = new JsonArray();
            jSizeArray.add(32); // Icon size 32 pixels
            jSizeArray.add(32); // Icon size 32 pixels
            jProp.add("iconSize", jSizeArray);
            jPos.add("properties", jProp );

            JsonObject jGeo = new JsonObject();
            jGeo.addProperty("type","Point");
            JsonArray jLatLong = new JsonArray();
            jLatLong.add(pJsonPoint.get(0));
            jLatLong.add(pJsonPoint.get(1));
            jGeo.add("coordinates", jLatLong);
            jPos.add("geometry", jGeo );

            jGeoPositions.add( jPos );
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
