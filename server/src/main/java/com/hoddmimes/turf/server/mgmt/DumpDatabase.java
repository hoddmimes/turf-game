package com.hoddmimes.turf.server.mgmt;

import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.Subscription;
import com.hoddmimes.turf.server.generated.User;

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

    }
}
