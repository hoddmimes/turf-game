package com.hoddmimes.turf.server.common;

import com.google.gson.JsonObject;

public class RGB
{
    private int r,g,b;

    public RGB( int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGB( String pColorHexString ) {
        //"#7e6454"
        r = Integer.parseInt(pColorHexString.substring( 1, 3), 16);
        g = Integer.parseInt(pColorHexString.substring( 3, 5), 16);
        b = Integer.parseInt(pColorHexString.substring( 5, 7), 16);
    }

    public JsonObject toJson() {
        JsonObject jObj = new JsonObject();
        jObj.addProperty("red", r);
        jObj.addProperty("green", g);
        jObj.addProperty("blue", b);
        return jObj;
    }
}
