package com.hoddmimes.turf.server.services.density.pathcost;

import com.google.gson.JsonArray;
import com.hoddmimes.turf.server.common.TurfZone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlgNearestNeighbor implements PathCostInterface
{
    List<Node> mNodeList;
    Distances  mDistances;
    boolean[] mVisits;

    List<Integer> mPath;

    public AlgNearestNeighbor() {

    }
    @Override
    public void initialize(List<TurfZone> pZones) {
        mNodeList = new ArrayList<>();
        for (int i = 0; i < pZones.size(); i++) {
            TurfZone z = pZones.get(i);
            mNodeList.add(new Node( i, z.getLong(), z.getLatitude()));
        }
        mDistances = new Distances( mNodeList );
    }

    @Override
    public void initialize(JsonArray pJsonPoints) {
        mNodeList = new ArrayList<>();
        for (int i = 0; i < pJsonPoints.size(); i++) {
            JsonArray jPoint = pJsonPoints.get(i).getAsJsonArray();
            mNodeList.add(new Node( i, jPoint.get(0).getAsDouble(), jPoint.get(1).getAsDouble()));
        }
        mDistances = new Distances( mNodeList );
    }

    public List<Node>getNodeList() {
        return new ArrayList<>( mNodeList);
    }


    public double getDistance( List<Integer> pNodeList) {
        mPath = new ArrayList<>();
        double tTotCost = 0;
        for (int i = 0; i < pNodeList.size() - 1; i++) {
            mPath.add( mNodeList.get(i).nodeId );
            tTotCost += mDistances.getDistance( mNodeList.get( pNodeList.get(i)).nodeId, mNodeList.get( pNodeList.get(i+1)).nodeId );
        }
        mPath.add( mNodeList.get( mNodeList.size() - 1).nodeId );
        return tTotCost;
    }

    @Override
    public double getDistance() {
        mPath = new ArrayList<>();
        mVisits = new boolean[mNodeList.size()];
        return findDistance(mNodeList.get(0).nodeId);
    }

    private double findDistance(int pCurrentNodeId) {
        mVisits[pCurrentNodeId]  = true; // Current node done
        mPath.add( pCurrentNodeId );
        if (allDone()) {
            return 0.0d;
        }
        int tNextNodeId = findNextNodeId( pCurrentNodeId );
        return (findDistance( tNextNodeId ) + mDistances.getDistance( pCurrentNodeId, tNextNodeId ));
    }

    private int findNextNodeId( int pCurrentNodeId ) {
        int tNextNodeId = -1;
        double tMinDistance = Double.MAX_VALUE;
        for (int i = 0; i < mDistances.getNodes(); i++) {
            if (!mVisits[i]) {
                double tDistance = mDistances.getDistance(pCurrentNodeId, i);
                if ((tDistance > 0) && (tDistance < tMinDistance)) {
                    tMinDistance = tDistance;
                    tNextNodeId = mNodeList.get(i).nodeId;
                }
            }
        }
        return tNextNodeId;
    }

    @Override
    public List<Integer> getPath() {
        return mPath;
    }

    private boolean allDone() {
        for (int i = 0; i < mVisits.length; i++) {
            if (!mVisits[i]) {
                return false;
            }
        }
        return true;
    }
}
