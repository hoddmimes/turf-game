package com.hoddmimes.turf.server.common;

import com.google.gson.JsonObject;

public class TurfZone
{
    private int     mId;
    private String  mName;
    private int     mPPH;
    private int     mTP;
    private double  mLat;
    private double  mLong;
    private String  mRegionName;
    private int     mRegionsId;
    private String  mRegionCountry;
    private boolean mHasRegion;

    public TurfZone(JsonObject pZone ) {
       mId = pZone.get("id").getAsInt();
       mName = pZone.get("name").getAsString();
       mPPH = pZone.get("pointsPerHour").getAsInt();
       mTP = pZone.get("pointsPerHour").getAsInt();
       mLat = pZone.get("latitude").getAsDouble();
       mLong = pZone.get("longitude").getAsDouble();

       if (pZone.get("region") != null ) {
           JsonObject jRegion = pZone.get("region").getAsJsonObject();
           if ((jRegion.get("id") == null) || (jRegion.get("name") == null)) {
               mHasRegion = false;
           } else {
               mHasRegion = true;
               mRegionName = jRegion.get("name").getAsString();
               mRegionsId = jRegion.get("id").getAsInt();
               mRegionCountry = (jRegion.get("country") != null) ? jRegion.get("country").getAsString() : null;
           }
       } else {
           mHasRegion = false;
       }
    }

    public boolean hasRegion() {
        return mHasRegion;
    }

    public String getRegionName() { return mRegionName; }

    public int getRegionid() { return mRegionsId; }

    public String getRegionCountry() { return mRegionCountry; }

    public double geLatitude() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }

    public int getTP() {
        return mTP;
    }

    public int getPPH() {
        return mPPH;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
