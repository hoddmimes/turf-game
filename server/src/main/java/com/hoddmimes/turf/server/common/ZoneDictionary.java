package com.hoddmimes.turf.server.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hoddmimes.turf.server.TurfServerInterface;
import org.apache.coyote.http2.Http2Exception;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneDictionary extends Thread
{


    Map<Integer, TurfZone>           mZoneIdMap;
    Map<String, TurfZone>            mZoneNameMap;
    Map<Integer, List<TurfZone>>    mRegionIdMap;
    Map<String, List<TurfZone>>     mRegionNameMap;
    Logger                          mLogger;
    String                          mLocalAllZonesDbFile;


    public ZoneDictionary(Logger pLogger, String pLocalAllZonesDbFile )
    {
        mLocalAllZonesDbFile = pLocalAllZonesDbFile;
        mLogger = pLogger;
        mZoneIdMap = new HashMap<>(65000); // Zones by zone-id
        mZoneNameMap = new HashMap<>(65000); // Zones by zone-name
        mRegionIdMap = new HashMap<>(200); // Regions by region-id
        mRegionNameMap = new HashMap<>(200); // Regions by region-name
        sync();
        this.start();
    }

    public int getSize() {
        return mZoneIdMap.size();
    }


    public synchronized Map<Integer,List<TurfZone>> getZonesByRegionsId() {
        return mRegionIdMap;
    }
    public synchronized Map<String,List<TurfZone>> getZonesByRegionsNames() {
        return mRegionNameMap;
    }

    public List<TurfZone> getZonesByRegionId( int pRegionId ) {
        return mRegionIdMap.get( pRegionId );
    }

    public synchronized TurfZone getZonebyName(String pName ) {
        return mZoneNameMap.get( pName );
    }

    public synchronized TurfZone getZoneById(int pId ) {
        return mZoneIdMap.get( pId );
    }

    private synchronized int sync() {
        mZoneIdMap.clear();
        mZoneNameMap.clear();
        mRegionIdMap.clear();
        mRegionNameMap.clear();

        JsonArray tZoneArray = null;

        try {
            tZoneArray = Turf.turfServerGETSignal("zones/all", mLogger).getAsJsonArray();
            mLogger.info("Zone directory loaded from TurfGame server");
            saveZonesToLocalStorage( tZoneArray );
        }
        catch( HttpException e) {
            if (e.getHttpResponseCode() == 429) {
                tZoneArray = loadZonesFromLocalStorage().getAsJsonArray();
            } else {
                mLogger.error("Failed to load all-zones from TurfGame server", e);
                System.exit(0);
            }
        }

        for( int i = 0; i < tZoneArray.size(); i++) {
            TurfZone z = new TurfZone( tZoneArray.get(i).getAsJsonObject());
            mZoneNameMap.put( z.getName(), z);
            mZoneIdMap.put( z.getId(), z );

            if (z.hasRegion()) {
                {
                    List<TurfZone> tRegionIdZoneList = mRegionIdMap.get(z.getRegionid());
                    if (tRegionIdZoneList == null) {
                        tRegionIdZoneList = new ArrayList<>();
                        mRegionIdMap.put(z.getRegionid(), tRegionIdZoneList);
                    }
                    tRegionIdZoneList.add(z);
                }
                {
                    List<TurfZone> tRegionNameZoneList = mRegionNameMap.get(z.getRegionName());
                    if (tRegionNameZoneList == null) {
                        tRegionNameZoneList = new ArrayList<>();
                        mRegionNameMap.put(z.getRegionName(), tRegionNameZoneList);
                    }
                    tRegionNameZoneList.add(z);
                }
            } else {
                System.out.println(" zone: " + z.getName() + " id: " + z.getId() + " has no region");
            }
        }


        return mZoneIdMap.size();
    }

    private JsonElement loadZonesFromLocalStorage() {
        JsonReader tReader = null;

        if (mLocalAllZonesDbFile == null) {
            mLogger.error("local all-zones local db configuration is not provided ");
        }

        try {
            File tFile = new File( mLocalAllZonesDbFile );
            if  (!tFile.exists()) {
                mLogger.error("local all-zones-local-db-file \"" + mLocalAllZonesDbFile + "\" does not exists");
            }
            tReader = new JsonReader(new FileReader( mLocalAllZonesDbFile ));
            mLogger.info("Zone directory loaded from local storage");
            return JsonParser.parseReader(tReader);

        }
        catch( Exception e) {
            mLogger.fatal( e );
            System.exit( 0 );
        }
        return JsonParser.parseReader(tReader);
    }


    private void saveZonesToLocalStorage( JsonArray pZoneArray ) {
        try {
            FileOutputStream fp = new FileOutputStream( mLocalAllZonesDbFile );
            fp.write( pZoneArray.toString().getBytes(StandardCharsets.UTF_8));
            fp.flush();
            fp.close();
        }
        catch( Exception e) {
           mLogger.fatal( e );
           System.exit( 0 );
        }
    }


    public static void main( String args[]) {
        Logger tLogger = LogManager.getLogger( ZoneDictionary.class );
        ZoneDictionary zd = new ZoneDictionary( tLogger,"all_zones_dictionary.json" );
        System.out.println("Loaded:  " + zd.sync() + " zones");
    }


    public void run() {
        this.setName("ZoneDictionaryThread");
        while( true ) {
            try { Thread.sleep( 7200000L );}
            catch( InterruptedException e) {}
            sync();
        }
    }
}
