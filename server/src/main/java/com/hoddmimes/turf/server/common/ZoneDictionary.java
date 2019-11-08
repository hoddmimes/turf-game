package com.hoddmimes.turf.server.common;

import com.google.gson.JsonArray;
import org.apache.logging.log4j.Logger;

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

    public ZoneDictionary( Logger pLogger)
    {
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

        JsonArray tZoneArray = Turf.turfServerGET("zones/all", mLogger).getAsJsonArray();
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

    public static void main( String args[]) {
        ZoneDictionary zd = new ZoneDictionary( null );
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
