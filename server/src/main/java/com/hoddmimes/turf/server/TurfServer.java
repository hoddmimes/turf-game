package com.hoddmimes.turf.server;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class TurfServer
{
    ServerConfiguration mServerCfg = null;
    ZoneNotifierService mZoneNotifierService = null;

    public static void main( String args[] ) {
        TurfServer tf = new TurfServer();
        tf.parseConfiguration( args );
        tf.initialize();
        tf.processZoneUpdates();
    }

    private String getZoneCollectTimeOffset( int pHistoryOffsetMin, int pHistoryTimezoneOffset ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long tTim = System.currentTimeMillis() - (pHistoryOffsetMin * 60000L);
        String tTimOffset = String.format("+%04d;-", (100 * pHistoryTimezoneOffset));
        String tTimStr = sdf.format(tTim) + tTimOffset;
        return URLEncoder.encode( tTimStr );
    }
    private JsonElement getZoneEvents() {
        JsonElement tRsp = Turf.turfServerGET( "feeds?afterDate=" + getZoneCollectTimeOffset( mServerCfg.getApiHistoryCollectOffsetMin(), mServerCfg.getApiTimeZoneOffetHr()));
        return tRsp;
    }

    private void processZoneUpdates() {
        while( true ) {
            JsonElement tZoneUpdateRsp = getZoneEvents();
            if (mZoneNotifierService != null) {
                mZoneNotifierService.zonesUpdated( tZoneUpdateRsp );
            }
            try {
                Thread.sleep( mServerCfg.getApiZoneCollectIntervalMs());
            }
            catch( InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialize() {
        if (mServerCfg.startZoneNotify()) {
            mZoneNotifierService = new ZoneNotifierService();
            mZoneNotifierService.initialize( mServerCfg );
        }
    }

    private void parseConfiguration( String args[])
    {
        if (args.length == 0) {
            System.out.println("server configuration file is not provided, program will exit");
            System.exit(0);
        }

        File tFile = new File(args[0]);
        if ((!tFile.exists()) || (!tFile.canRead())) {
                System.out.println("Can not find or read configuration file \"" + args[0] + "\"");
                System.exit(0);
        }

        mServerCfg = new ServerConfiguration();
        mServerCfg.parseConfiguration( args[0] );
    }

}


