package com.hoddmimes.turf.server.common;

import com.google.gson.JsonObject;

public class TurfUser
{
    private JsonObject jUser;

    public TurfUser( JsonObject pUser ) {
        jUser = pUser;
    }

    public int getTaken() {
        return jUser.get("taken").getAsInt();
    }

    public int getPlace() {
        return jUser.get("place").getAsInt();
    }


    public int getUserId() {
        return jUser.get("id").getAsInt();
    }

    public String getUserName() {
        return jUser.get("name").getAsString();
    }

    public  int getPPH() {
        return jUser.get("pointsPerHour").getAsInt();
    }
    public  int getPoints() {
        return jUser.get("points").getAsInt();
    }


    public int getActiveZones() {
        if (jUser.has("zones")) {
            return jUser.get("zones").getAsJsonArray().size();
        }
        return 0;
    }

    public int getRegionId() {
        if (jUser.has("region")) {
            jUser.get("region").getAsInt();
        }
        return 0;
    }

    public int getRegionName() {
        if (jUser.has("region")) {
            jUser.get("name").getAsInt();
        }
        return 0;
    }
}
