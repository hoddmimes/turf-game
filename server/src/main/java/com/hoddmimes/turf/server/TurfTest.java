package com.hoddmimes.turf.server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.TurfUser;
import com.hoddmimes.turf.server.configuration.DayRankingConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TurfTest
{
    public static void main(String[] args) {
        TurfTest tt = new TurfTest();
        tt.test();
    }

    private void test() {
       getUserRanking();
    }

    void getUserRanking() {
        ArrayList<JsonObject> jUsers = new ArrayList<>();
        Logger tLogger = LogManager.getLogger(TurfTest.class);

        String tRegionName = "Stockholm";


        int tX = 1, tY = 300;
        boolean tNoMore = false;
        try {
            while ((!tNoMore) && (tX < tY)) {


                String tPostRqst = "{\"region\": \"" + tRegionName + "\", \"from\":" + tX + " , \"to\": " + tY + " }";


                //mService.mLogger.info("user/top request rqst: \"" + tPostRqst + "\"");

                JsonElement jElement = Turf.getInstance().turfServerPOST("users/top", tPostRqst, tLogger);
                if (jElement == null) {

                }

                JsonArray jArr = jElement.getAsJsonArray();
                for (int i = 0; i < jArr.size(); i++) {
                    TurfUser tu = new TurfUser(jArr.get(i).getAsJsonObject());
                    jUsers.add(jArr.get(i).getAsJsonObject());
                }
                tX = jUsers.size() + 1;
                if (jArr.size() == 0) {
                    tNoMore = true;
                }
            }
            Gson gson = new Gson();
            for (int i = 0; i < jUsers.size(); i++) {
                JsonObject jUser = jUsers.get(i).getAsJsonObject();
                System.out.println(" " + i + "  user: " + jUser.get("name").getAsString() + " points: " + jUser.get("points").getAsInt() + "place: " + jUser.get("place").getAsInt());
            }
        } catch (Throwable e) {
            tLogger.error("Failed to retrieve user day ranking from Turf service", e);
            e.printStackTrace();
        }
    }
}
