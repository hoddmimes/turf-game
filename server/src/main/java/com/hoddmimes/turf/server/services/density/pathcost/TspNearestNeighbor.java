package com.hoddmimes.turf.server.services.density.pathcost;

import com.google.gson.JsonArray;
import com.hoddmimes.turf.server.common.TurfZone;

import java.util.List;

public class TspNearestNeighbor implements PathCostInterface
{
    @Override
    public void initialize(JsonArray jZones) {

    }

    @Override
    public void initialize(List<TurfZone> pZones) {

    }

    @Override
    public double getDistance() {
        return 0;
    }

    @Override
    public List<Integer> getPath() {
        return null;
    }
}
