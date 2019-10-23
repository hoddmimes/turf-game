package com.hoddmimes.turf.server.mgmt;

public abstract class Database {

    protected boolean mReset = true;
    protected String  mHost = "localhost";
    protected int mPort = 27017;
    protected  String mDatabaseName = "TurfGame";

    public Database()
    {
    }

    protected void parseArguments( String args[] ) {
        int i = 0;

        while( i < args.length ) {
            if (args[i].compareToIgnoreCase("-reset") == 0) {
                mReset = Boolean.parseBoolean( args[i + 1] );
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
        System.out.println("Create Database Parameters");
        System.out.println("    reset: " + String.valueOf( mReset ));
        System.out.println("    host: " + String.valueOf( mHost ));
        System.out.println("    database: " + String.valueOf( mDatabaseName ));
        System.out.println("    port: " + String.valueOf( mPort ));

    }
}
