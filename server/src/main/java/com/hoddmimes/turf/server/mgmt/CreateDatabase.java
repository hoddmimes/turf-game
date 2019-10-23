package com.hoddmimes.turf.server.mgmt;

import com.hoddmimes.turf.server.configuration.PasswordRules;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.User;

import java.text.SimpleDateFormat;

public class CreateDatabase extends Database
{
    private boolean mCreateTestUser = true;


    public static void main( String args[] ) {
        CreateDatabase cdb = new CreateDatabase();
        cdb.parseArguments( args );
        try {
            cdb.setupDB();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDB() throws Exception {
        MongoAux tDbAux = new MongoAux(mDatabaseName, mHost, mPort);

        if (mReset) {
            tDbAux.dropDatabase();
            System.out.println("Database " + mDatabaseName + " on " + mHost + " is dropped");
        }

        tDbAux.createDatabase();

        if (mCreateTestUser) {
            tDbAux.connectToDatabase();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            User tUser = new User();
            tUser.setPassword(new PasswordRules().hashPassword("test"));
            tUser.setMailAddr("test@foobar.com");
            tUser.setLoginCounts(0);
            tUser.setLastLogin(sdf.format(System.currentTimeMillis()));

            tDbAux.insertUser(tUser);
            System.out.println("Successfully added user test in database \"" + mDatabaseName + "\" on host \"" + mHost + "\"");
        }

        System.out.println("Successfully created database \"" + mDatabaseName + "\" on host \"" + mHost + "\"");
    }

    protected  void parseArguments( String args[]) {
        int i = 0;
        super.parseArguments( args );
        while( i < args.length) {
            if (args[i].compareToIgnoreCase("-testUser") == 0) {
                mCreateTestUser = Boolean.parseBoolean( args[i + 1] );
                i++;
            }
            i++;
        }
    }

}
