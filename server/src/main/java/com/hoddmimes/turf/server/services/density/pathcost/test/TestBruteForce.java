package com.hoddmimes.turf.server.services.density.pathcost.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hoddmimes.turf.server.services.density.pathcost.AlgNearestNeighbor;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TestBruteForce implements Permutations.PermuteCallback {
    // COST: 7417 Order [1, 3, 11, 2, 4, 9, 10, 6, 5, 7, 12, 0, 8]
    JsonArray jZones;
    AlgNearestNeighbor mPathCost;
    int mMinCost = Integer.MAX_VALUE;

    public static void main(String[] args) {
        TestBruteForce bf = new TestBruteForce();

        bf.loadZones();
        bf.test();
    }


    private void test() {
        List<Integer> tNodeIndexList = new ArrayList();
        for (int i = 0; i < jZones.size(); i++) {
            tNodeIndexList.add(i);
        }
        mPathCost = new AlgNearestNeighbor();
        mPathCost.initialize( jZones );
        Permutations tPermutation = new Permutations( tNodeIndexList );
        tPermutation.permute( this );
    }

    private void loadZones() {
        try {
            FileReader tFileReader = new FileReader("zones.json");
            jZones = JsonParser.parseReader(tFileReader).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextPermutation(List<Integer> pList) {
        int tCost = (int) Math.round(mPathCost.getDistance( pList ));
        if (tCost < mMinCost) {
            mMinCost = tCost;
            System.out.println(" COST: " + mMinCost + " Order " + pList );
        }
    }
}
