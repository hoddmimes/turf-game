package com.hoddmimes.turf.server.common;

import com.google.gson.JsonObject;

public class Zone
{
    private int     mId;
    private String  mName;
    private int     mPPH;
    private int     mTP;
    private double  mLat;
    private double  mLong;

    public Zone( JsonObject pZone ) {
       mId = pZone.get("id").getAsInt();
       mName = pZone.get("name").getAsString();
       mPPH = pZone.get("pointsPerHour").getAsInt();
       mTP = pZone.get("pointsPerHour").getAsInt();
       mLat = pZone.get("latitude").getAsDouble();
       mLong = pZone.get("longitude").getAsDouble();
    }

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
