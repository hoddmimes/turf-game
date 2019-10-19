package com.hoddmimes.turf.server.mgmt;

import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.User;

import java.text.SimpleDateFormat;

public class CreateZNDB
{
    private boolean mReset = true;
    private boolean mCreateTestUser = true;
    private String  mHost = "localhost";
    private int mPort = 27017;
    private  String mDatabaseName = "TurfGame";

    public static void main( String args[] ) {
        CreateZNDB czn = new CreateZNDB();
        czn.parseArguments( args );
        try {
            czn.setupDB();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDB() throws Exception
        {
            MongoAux tDbAux = new MongoAux( mDatabaseName, mHost, mPort );

            if (mReset) {
                tDbAux.dropDatabase();
            }

            tDbAux.createDatabase();
            tDbAux.createDatabase();

            if (mCreateTestUser) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                User tUser = new User();
                tUser.setPassword("test");
                tUser.setMailAddr("test@foobar.com");
                tUser.setLoginCounts(0);
                tUser.setLastLogin( sdf.format( System.currentTimeMillis()));

                tDbAux.insertUser( tUser );
            }

            System.out.println("Successfully created database \"" + mDatabaseName + "\" on host \"" + mHost +"\"");
        }
    }

    private void parseArguments( String args[] ) {
        int i = 0;

        while( i < args.length ) {
            if (args[i].compareToIgnoreCase("-reset") == 0) {
                mReset = Boolean.parseBoolean( args[i + 1] );
                i++;
            }
            if (args[i].compareToIgnoreCase("-testUser") == 0) {
                mCreateTestUser = Boolean.parseBoolean( args[i + 1] );
                i++;
            }
            if (args[i].compareToIgnoreCase("-host") == 0) {
                mHost =  args[i + 1];
                i++;
            }
            if (args[i].compareToIgnoreCase("-database") == 0) {
                mDatabaseName =  args[i + 1];
                i++;
            }
            if (args[i].compareToIgnoreCase("-port") == 0) {
                mPort = Integer.parseInt( args[i + 1] );
                i++;
            }
            i++;
        }
    }
}
