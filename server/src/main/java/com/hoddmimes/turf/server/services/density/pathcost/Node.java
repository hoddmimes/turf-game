package com.hoddmimes.turf.server.services.density.pathcost;

import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;

class Node {
    public double lng;
    public double lat;
    public int nodeId; // The order the node is created/defined i.e from  0..n



    Node( int pOrder, double pLng, double pLat ) {
        this.lng = pLng;
        this.lat = pLat;
        this.nodeId = pOrder;
    }
}
