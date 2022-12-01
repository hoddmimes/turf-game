package com.hoddmimes.turf.server.services.density;

import com.hoddmimes.turf.server.common.TurfZone;
import com.hoddmimes.turf.server.services.regionstat.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;




class PathCost
{

    Node[][] mNodeMatrix;
    List<Node> mNodeList;
    boolean[] mVisits;


    public PathCost(List<TurfZone> pZones) {
        System.out.println("Zones in area: " + pZones.size() + " matrix: " + (pZones.size() * pZones.size()));
        mNodeList = new ArrayList<>();
        for( TurfZone z : pZones ) {
            mNodeList.add(new Node( z.getLong(), z.getLatitude(), 0.0d));
        }
    }

    public double getTotalLength() {
        loadMatrix();
        return findPath();
    }

    private boolean allDone() {
        for (int i = 0; i < mVisits.length; i++) {
            if (!mVisits[i]) {
                return false;
            }
        }
        return true;
    }

    private double findPath() {
        return findPath(0 );
    }

    private double findPath( int pCurrentNode) {
        mVisits[pCurrentNode]  = true; // Current node done
        if (allDone()) {
            System.out.println("all done!");
            return 0.0d;
        }
        int tNextNode = findNextNode( mNodeMatrix[pCurrentNode]);
        return (findPath( tNextNode ) + mNodeMatrix[pCurrentNode][tNextNode].cost );
    }

    private int findNextNode( Node[] pAdjacents ) {
        int tNextNode = -1;
        double tMinCost = Double.MAX_VALUE;
        for (int i = 0; i < pAdjacents.length; i++) {
            if ((!mVisits[i]) && (pAdjacents[i].cost > 0)) {
                if (pAdjacents[i].cost < tMinCost) {
                    tMinCost = pAdjacents[i].cost;
                    tNextNode = i;
                }
            }
        }
        return tNextNode;
    }


    private void loadMatrix() {
        int tSize = mNodeList.size();
        mVisits = new boolean[tSize];
        mNodeMatrix = new Node[tSize][tSize];
        for (int i = 0; i < tSize; i++){
            for (int j = 0; j < tSize ; j++) {
                mNodeMatrix[i][j] = new Node(i, j, Node.calcDistance(mNodeList.get(i), mNodeList.get(j)));
                //System.out.println("[" + i + "," + j + "] " + mNodeMatrix[i][j].cost );
            }
        }
    }



    static class Node {
        double lng;
        double lat;
        double cost;

        Node( double pLng, double pLat ) {
            this.lng = pLng;
            this.lat = pLat;
            this.cost = 0.0d;
        }

        Node( double pLng, double pLat, double pCost ) {
            this.lng = pLng;
            this.lat = pLat;
            this.cost = pCost;
        }

        static double calcDistance(Node n1, Node n2 ) {
            return DistanceCalculator.distance(  n1.lat, n1.lng, n2.lat, n2.lng);
        }
    }
}
