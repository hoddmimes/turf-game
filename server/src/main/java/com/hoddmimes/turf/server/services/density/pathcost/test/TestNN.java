package com.hoddmimes.turf.server.services.density.pathcost.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hoddmimes.turf.server.services.density.pathcost.AlgNearestNeighbor;

import java.io.FileReader;

public class TestNN
{
    JsonArray jZones;

    public static void main(String[] args) {
        TestNN t = new TestNN();

        t.loadZones();
        t.test();
    }

    private JsonArray shiftZones( int pStartIndex )  {
        JsonArray jArr = new JsonArray();
        int idx = 0;
        for (int i = 0; i < jZones.size(); i++) {
            idx = ((i + pStartIndex) >= jZones.size()) ?
            ((i + pStartIndex) - jZones.size()) : (i + pStartIndex);
            //System.out.print(" " + idx );
            jArr.add( jZones.get( idx ));
        }
        //System.out.println(" - ");
        return jArr;
    }

    private void test() {
        AlgNearestNeighbor pc = new AlgNearestNeighbor();
        pc.initialize( jZones );
        for (int i = 0; i < jZones.size(); i++) {
            System.out.println("Start Node: " + i + " cost: " + (int) Math.round(pc.getDistance(i)) + "  path: " + pc.getPath());
        }
    }

    private void loadZones() {
        try {
            FileReader tFileReader = new FileReader("zones.json");
            jZones = JsonParser.parseReader(tFileReader).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
