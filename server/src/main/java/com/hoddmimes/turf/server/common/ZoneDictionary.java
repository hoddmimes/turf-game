package com.hoddmimes.turf.server.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class ZoneDictionary extends Thread
{
    Map<Integer,Zone> mZoneIdMap;
    Map<String,Zone> mZoneNameMap;

    public ZoneDictionary()
    {
        mZoneIdMap = new HashMap<>(65000);
        mZoneNameMap = new HashMap<>(65000);
        sync();
        this.start();
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
        JsonArray tZoneArray = Turf.turfServerGET("zones/all").getAsJsonArray();
        for( int i = 0; i < tZoneArray.size(); i++) {
            Zone z = new Zone( tZoneArray.get(i).getAsJsonObject());
            mZoneNameMap.put( z.getName(), z);
            mZoneIdMap.put( z.getId(), z );
        }
        System.out.println("loaded " + tZoneArray.size() + " zones");
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
