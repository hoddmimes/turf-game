package com.hoddmimes.turf.server.services.density.pathcost;


import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;

import java.util.List;

public class Distances
{
    double mDistances[][];
    int mSize;

    public Distances( List<Node> pNodeList) {
        mSize = pNodeList.size();
        ;
        mDistances = new double[mSize][mSize];
        for (Node nx : pNodeList) {
            for (Node ny : pNodeList)
                mDistances[nx.nodeId][ny.nodeId] = calcDistance(nx, ny);
        }
    }

    public int getNodes() {
        return mSize;
    }

    private double calcDistance(Node n1, Node n2 ) {
        return DistanceCalculator.distance(  n1.lat, n1.lng, n2.lat, n2.lng);
    }

    double getDistance( int pFromNodeOrderIndex, int pToNodeOrderIndex ) {
        return mDistances[pFromNodeOrderIndex][pToNodeOrderIndex];
    }
}
