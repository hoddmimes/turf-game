package com.hoddmimes.turf.server.services.density.pathcost.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hoddmimes.turf.server.services.density.pathcost.AlgAnnealing;
import com.hoddmimes.turf.server.services.density.pathcost.AlgNearestNeighbor;

import java.io.FileReader;

public class TestAAS
{
    JsonArray jZones;

    public static void main(String[] args) {
        TestAAS t = new TestAAS();

        t.loadZones();
        t.test();
    }


    private void test() {
        AlgAnnealing pc = new AlgAnnealing( (double) jZones.size() , 1000000, 0.999995d);
        pc.initialize( jZones );
        System.out.println("Cost: " + (int) Math.round(pc.getDistance()) + "  path: " + pc.getPath());
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
