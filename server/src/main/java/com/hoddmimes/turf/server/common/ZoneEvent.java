package com.hoddmimes.turf.server.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;

public class ZoneEvent
{
    private JsonObject mZoneEvent;

    public ZoneEvent( JsonObject pZoneEvent ) {
        mZoneEvent = pZoneEvent;
    }

    public String getZoneName() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("name").getAsString();
    }

    public int getTakeoverCount() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("totalTakeovers").getAsInt();
    }

    public String getCurrentOwner() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        JsonObject jCurrentOwner = jZone.get("currentOwner").getAsJsonObject();
        return jCurrentOwner.get("name").getAsString();
    }

    public int getCurrentOwnerId() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        JsonObject jCurrentOwner = jZone.get("currentOwner").getAsJsonObject();
        return jCurrentOwner.get("id").getAsInt();
    }

    public String getPreviousOwner() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        if (jZone.get("previousOwner") == null) {
            return "";
        }

        JsonObject jCurrentOwner = jZone.get("previousOwner").getAsJsonObject();
        return jCurrentOwner.get("name").getAsString();
    }

    public int getPreviousOwnerId() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        if (jZone.get("previousOwner") == null) {
            return -1;
        }

        JsonObject jCurrentOwner = jZone.get("previousOwner").getAsJsonObject();
        return jCurrentOwner.get("id").getAsInt();
    }

    public int getZoneId() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("id").getAsInt();
    }

    public String getLatestTakeTime() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("dateLastTaken").getAsString();
    }

    public int getRegionId() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        JsonObject jRegion = jZone.getAsJsonObject("region").getAsJsonObject();
        return jRegion.get("id").getAsInt();
    }

    public String getRegionName() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        JsonObject jRegion = jZone.getAsJsonObject("region").getAsJsonObject();
        return jRegion.get("name").getAsString();
    }

    public long getLatestTakeOverTime() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        String tTimStr = jZone.get("dateLastTaken").getAsString();
        try {
            long tTime = Turf.TurfSDF.parse( tTimStr ).getTime();
            //System.out.println("turf-time: " + tTimStr + " local-time: " + Turf.SDF.format( tTime ));
            return tTime;
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public int getTP() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("takeoverPoints").getAsInt();
    }

    public int getPPH() {
        JsonObject jZone = mZoneEvent.getAsJsonObject("zone");
        return jZone.get("pointsPerHour").getAsInt();
    }

    public double getLong() {
        return mZoneEvent.get("longitude").getAsDouble();
    }

    public double getLat() {
       return mZoneEvent.get("latitude").getAsDouble();
    }

    public static void main( String args[]) {
        String tZoneString = "{\"zone\": { \"previousOwner\": { \"name\": \"147\", \"id\": 133657 }, \"dateCreated\": \"2013-03-04T20:19:49+0000\", \"dateLastTaken\": \"2019-10-03T10:05:00+0000\", \"latitude\": 55.706493, \"currentOwner\": { \"name\": \"Regeldavve\", \"id\": 166728 }, \"name\": \"Bookworm\", \"id\": 15529, \"totalTakeovers\": 15266, \"region\": { \"country\": \"se\", \"name\": \"Skåne\", \"id\": 135 }, \"pointsPerHour\": 7, \"longitude\": 13.192075, \"takeoverPoints\": 95 }, \"latitude\": 55.706493, \"currentOwner\": { \"name\": \"Regeldavve\", \"id\": 166728 }, \"time\": \"2019-10-03T10:05:00+0000\", \"type\": \"takeover\",\"longitude\": 13.192075 }";
        JsonObject jObj = new JsonParser().parse( tZoneString ).getAsJsonObject();

        ZoneEvent ze = new ZoneEvent( jObj );
        ze.getLatestTakeOverTime();

    }

}
