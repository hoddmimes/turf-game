package com.hoddmimes.turf.server.services.density.pathcost;

import com.google.gson.JsonArray;
import com.hoddmimes.turf.server.common.TurfZone;

import java.util.List;

public interface PathCostInterface
{
    public void initialize( JsonArray jZones );
    public void initialize( List<TurfZone> pZones );

    public double getDistance();
    public List<Integer> getPath();
}
