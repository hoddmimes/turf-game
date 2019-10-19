package com.hoddmimes.turf.server.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneDictionary extends Thread
{
    Map<Integer,Zone> mZoneIdMap;
    Map<String,Zone> mZoneNameMap;
    Map<String, List<Zone>> mRegionMap;


    public ZoneDictionary()
    {
        mZoneIdMap = new HashMap<>(65000);
        mZoneNameMap = new HashMap<>(65000);
        mRegionMap = new HashMap<>(200);
        sync();
        this.start();
    }

    public int getSize() {
        return mZoneIdMap.size();
    }


    public synchronized Map<String,List<Zone>> getZonesByRegions() {
        return mRegionMap;
    }



    public synchronized Zone  getZonebyName( String pName ) {
        return mZoneNameMap.get( pName );
    }

    public synchronized Zone getZoneById( int pId ) {
        return mZoneIdMap.get( pId );
    }

    private synchronized void sync() {
        mZoneIdMap.clear();
        mZoneNameMap.clear();
        mRegionMap.clear();

        JsonArray tZoneArray = Turf.turfServerGET("zones/all").getAsJsonArray();
        for( int i = 0; i < tZoneArray.size(); i++) {
            Zone z = new Zone( tZoneArray.get(i).getAsJsonObject());
            mZoneNameMap.put( z.getName(), z);
            mZoneIdMap.put( z.getId(), z );

            List<Zone> tRegionZoneList = mRegionMap.get( z.getRegionName());
            if (tRegionZoneList == null) {
                tRegionZoneList = new ArrayList<>();
                mRegionMap.put(z.getRegionName(), tRegionZoneList);
            }
            tRegionZoneList.add( z );
        }
    }

    public static void main( String args[]) {
        ZoneDictionary zd = new ZoneDictionary();
        zd.sync();
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
