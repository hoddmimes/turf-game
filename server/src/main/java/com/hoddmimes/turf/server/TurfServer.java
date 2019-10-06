package com.hoddmimes.turf.server;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.turf.common.transport.TcpServer;
import com.hoddmimes.turf.common.transport.TcpServerCallbackIf;
import com.hoddmimes.turf.common.transport.TcpThread;
import com.hoddmimes.turf.common.transport.TcpThreadCallbackIf;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.Zone;
import com.hoddmimes.turf.server.common.ZoneDictionary;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class TurfServer implements TurfServerInterface, TcpServerCallbackIf, TcpThreadCallbackIf
{
    private ServerConfiguration mServerCfg = null;
    private ZoneNotifierService mZoneNotifierService = null;
    private ZoneDictionary mZoneDictory = null;

    private TcpServer mTcpIpServer;
    private Logger mLogger = null;


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
        mLogger = LogManager.getLogger(this.getClass().getSimpleName());
        mLogger.info("Intitializing " + this.getClass().getSimpleName()  + com.hoddmimes.turf.server.Version.build);
        mZoneDictory = new ZoneDictionary();

        declareTcpIpServer();

        if (mServerCfg.startZoneNotify()) {
            mZoneNotifierService = new ZoneNotifierService();
            mZoneNotifierService.initialize( this );
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

    @Override
    public void log( String pMsg ) {
        mLogger.info( pMsg );
    }
    @Override
    public void logEx( String pMsg, Throwable e) {
        mLogger.error( pMsg, e);
    }
    @Override
    public void logW( String pMsg ) {
        mLogger.warn( pMsg );
    }
    @Override
    public void logE( String pMsg ) {
        mLogger.error( pMsg );
    }
    @Override
    public void logF( String pMsg, Throwable e ) {
        mLogger.fatal( pMsg, e );
    }
    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
    public Zone getZoneById(int pId) {
        return mZoneDictory.getZoneById( pId );
    }

    @Override
    public Zone getZoneByName(String pName) {
        return mZoneDictory.getZonebyName( pName );
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return mServerCfg;
    }

    private void declareTcpIpServer()
    {
        mTcpIpServer = new TcpServer(  this );
        try {
            mTcpIpServer.declareServer( mServerCfg.getTcpIpServerPort(), mServerCfg.getTcpIpInterface());
        }
        catch( IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tcpInboundConnection(TcpThread pThread)
    {
        pThread.setCallback( this );
    }

    @Override
    public void tcpMessageRead(TcpThread pThread, byte[] pBuffer) {

    }

    @Override
    public void tcpErrorEvent(TcpThread pThread, IOException pException) {

    }
}


