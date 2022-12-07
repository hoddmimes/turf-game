package com.hoddmimes.turf.server.services.density.pathcost.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

public class T
{
    public static void main(String[] args) {
        T t = new T();
        t.test();
    }

    private void test() {
        try {

            PrintWriter fp = new PrintWriter(new FileOutputStream("zones.json"));
            FileReader tFileReader = new FileReader("polygon-zones.json");
            JsonArray jArr = JsonParser.parseReader(tFileReader).getAsJsonArray();
            JsonArray jOutArr = new JsonArray();
            Random rnd = new Random();
            for (int i = 0; i < 50; i++) {
                int idx = rnd.nextInt(jArr.size() - 1);
                jOutArr.add(jArr.get(idx));
                jArr.remove(idx);
            }
            fp.println(jOutArr.toString());
            fp.flush();
            fp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
