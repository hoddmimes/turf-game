package com.hoddmimes.turf.server.mgmt;

import com.hoddmimes.turf.server.generated.*;

import java.util.List;

public class DumpDatabase extends Database
{

    public static void main( String args[] ) {
        DumpDatabase ddb = new DumpDatabase();
        ddb.parseArguments( args );
        try {
            ddb.dump();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }


    private void dump() {
        MongoAux tDbAux = new MongoAux( mDatabaseName, mHost, mPort );
        tDbAux.connectToDatabase();

        List<User> tUsers = tDbAux.findAllUser();
        List<Subscription> tSubscriptions = tDbAux.findAllSubscription();
        List<RegionStat> tRegions = tDbAux.findAllRegionStat();
        List<HourRegionStat> tHHRegions = tDbAux.findAllHourRegionStat();


        System.out.println(" ==== Users ====");
        System.out.println("================");
        for( User tUser : tUsers) {
            System.out.println("    " + tUser.toJson().toString());
        }

        System.out.println("\n\n");

        System.out.println(" ==== Subscriptions ====");
        System.out.println("========================");
        for( Subscription tSubscr : tSubscriptions) {
            System.out.println("    " + tSubscr.toJson().toString());
        }

        System.out.println("\n\n");

        System.out.println(" ======= Regions =======");
        System.out.println("========================");
        for( RegionStat r : tRegions) {
            System.out.println("    " + r.toJson().toString());
        }

        System.out.println("\n\n");

        System.out.println(" ==== Hour Region Stats ====");
        System.out.println("============================");
        for( HourRegionStat hr : tHHRegions) {
            System.out.println("    " + hr.toJson().toString());
        }
        System.out.println("\n\n");

        System.out.println(" ==== First Entry ====");
        System.out.println("============================");
        FirstEntry tFirst = tDbAux.findAllFirstEntry().get(0);
        System.out.println("    " + tFirst.toJson().toString());
    }
}
