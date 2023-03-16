package com.hoddmimes.turf.server;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.MessageFactory;
import com.hoddmimes.turf.common.generated.TG_LogonRqst;
import com.hoddmimes.turf.common.generated.TG_WhenInitializedRqst;
import com.hoddmimes.turf.common.generated.TG_WhenInitializedRsp;
import com.hoddmimes.turf.common.transport.TcpServer;
import com.hoddmimes.turf.common.transport.TcpServerCallbackIf;
import com.hoddmimes.turf.common.transport.TcpThread;
import com.hoddmimes.turf.common.transport.TcpThreadCallbackIf;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.TurfZone;
import com.hoddmimes.turf.server.common.ZoneDictionary;
import com.hoddmimes.turf.server.configuration.PasswordRules;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;

import com.hoddmimes.turf.server.generated.FirstEntry;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.User;
import com.hoddmimes.turf.server.services.dayranking.DayRankingService;
import com.hoddmimes.turf.server.services.density.ZoneDensityService;
import com.hoddmimes.turf.server.services.regionstat.heatmap.ZoneHeatMapService;
import com.hoddmimes.turf.server.services.notifier.ZoneNotifierService;
import com.hoddmimes.turf.server.services.regionstat.RegionStatService;
import com.hoddmimes.turf.server.services.usertrace.UserTraceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;
import org.w3c.dom.Element;


import javax.naming.NameNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class TurfServer implements TurfServerInterface, TcpServerCallbackIf, TcpThreadCallbackIf, WrapperListener
{
    private static boolean cDaemonMode = false;
    static volatile boolean cServerShouldExit = false;
    static ExecServerTasksThread cServerTaskThread = null;

    private String mServerCfgFilename = null;
    private ServerConfiguration mServerCfg = null;
    private ZoneDictionary mZoneDictionary = null;

    private ZoneNotifierService mZoneNotifierService = null;
    private RegionStatService mRegionStatService = null;
    private UserTraceService mUserTraceService = null;
    private DayRankingService mDayRankingService = null;
    private ZoneHeatMapService mZoneHeatMapService = null;

    private ZoneDensityService mZoneDensityService = null;


    private TcpServer mTcpIpServer;
    private MongoAux mDbAux;
    private Logger mLogger = null;


    private static void checkDaemonMode( String args[]  ) {
        for( int i = 0; i < args.length; i++) {
            if (args[i].compareToIgnoreCase("-daemon") == 0) {
                cDaemonMode = Boolean.parseBoolean( args[i+1] );
            }
        }
    }

    public static void main( String args[] ) {
        checkDaemonMode(args);
        if (TurfServer.cDaemonMode) {
            System.out.println("Starting server in Daemon mode ");
            WrapperManager.start(new TurfServer(), args);
        } else {
            TurfServer tf = new TurfServer();
            tf.parseConfiguration(args);
            tf.initialize();
            tf.execServerTasks();

        }
    }

    private String getZoneCollectTimeOffset( int pHistoryOffsetMin, int pHistoryTimezoneOffset ) {
        String tURlString;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long tTim = System.currentTimeMillis() - (pHistoryOffsetMin * 60000L);
        String tTimOffset = String.format("+%04d", (100 * pHistoryTimezoneOffset));
        String tTimStr = sdf.format(tTim) + tTimOffset;
        try {
            tURlString = URLEncoder.encode(tTimStr, "UTF-8");
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            tURlString = tTimStr;
        }

        //mLogger.debug("[getZoneCollectTimeOffset] local-time: " + tTimStr + " url-time: " +  tURlString );
        return tURlString;
    }
    private JsonElement getZoneEvents() {
        String tFromTimStr = getZoneCollectTimeOffset( mServerCfg.getApiHistoryCollectOffsetMin(), mServerCfg.getApiTimeZoneOffetHr());
        JsonElement tRsp = Turf.getInstance().turfServerGET( "feeds?afterDate=" + tFromTimStr, true, mLogger);
        return tRsp;
    }

    private int countZones( JsonElement pJsonElement ) {
        if (pJsonElement == null) {
            return -1;
        }

        if (pJsonElement.isJsonArray()) {
            int tCount = 0;
            JsonArray tJsonArray = pJsonElement.getAsJsonArray();
            for(int i = 0; i < tJsonArray.size(); i++) {
               JsonObject jObject = tJsonArray.get(i).getAsJsonObject();
               if (jObject.get("type").getAsString().compareTo("takeover") == 0) {
                   tCount++;
               }
            }
            return tCount;
        }
        return 0;
    }

    private void execServerTasks() {
        while(!(cServerShouldExit)) {
            JsonElement tZoneUpdateRsp = getZoneEvents();

            //mLogger.debug("[zonesUpdated] zones " + countZones(tZoneUpdateRsp) + " retreived");
            if (tZoneUpdateRsp != null) {
                if (mZoneNotifierService != null) {
                    mZoneNotifierService.processZoneUpdates(tZoneUpdateRsp);
                }

                if (mRegionStatService != null) {
                    mRegionStatService.processZoneUpdates(tZoneUpdateRsp);
                }

                if (mUserTraceService != null) {
                    mUserTraceService.processZoneUpdates(tZoneUpdateRsp);
                }
                if (mDayRankingService != null) {
                    mDayRankingService.processZoneUpdates(tZoneUpdateRsp);
                }

                if (mZoneHeatMapService != null) {
                    mZoneHeatMapService.processZoneUpdates(tZoneUpdateRsp);
                }

                if (mZoneDensityService != null) {
                    mZoneDensityService.processZoneUpdates(tZoneUpdateRsp);
                }

            }
            try {
                Thread.sleep( mServerCfg.getApiZoneCollectIntervalMs());
            }
            catch( InterruptedException e) {
                if (cServerShouldExit) {
                    mLogger.info("Execution server task (thread) is interrupted and will exit");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialize() {

        mLogger = LogManager.getLogger(this.getClass().getName());
        mLogger.info("Initializing " + this.getClass().getSimpleName()  + com.hoddmimes.turf.server.generated.Version.build);

        mDbAux = new MongoAux( mServerCfg.getDbName(), mServerCfg.getDbHost(), mServerCfg.getDbPort());
        mDbAux.connectToDatabase();



        mZoneDictionary = new ZoneDictionary( mLogger, mServerCfg.getLocalAllZonesDBFile() );
        mLogger.info("ZoneDictionary loaded [" + mZoneDictionary.getSize() + "] zones.");
        declareTcpIpServer();



        if (mServerCfg.startZoneNotify()) {
            mZoneNotifierService = new ZoneNotifierService();
            mZoneNotifierService.initialize( this );
        }

        if (mServerCfg.startRegionStat()) {
            mRegionStatService = new RegionStatService();
            mRegionStatService.initialize( this );
        }

        if (mServerCfg.startUserTrace()) {
            mUserTraceService = new UserTraceService();
            mUserTraceService.initialize( this );
        }

        if (mServerCfg.startDayRanking()) {
            mDayRankingService = new DayRankingService();
            mDayRankingService.initialize( this );
        }

        if (mServerCfg.startZoneHeatMap()) {
            mZoneHeatMapService = new ZoneHeatMapService();
            mZoneHeatMapService.initialize( this );
        }
        if (mServerCfg.startZoneDensity()) {
            mZoneDensityService = new ZoneDensityService();
            mZoneDensityService.initialize( this );
        }



    }

    private void parseConfiguration( String args[])
    {
        int i = 0;

        while( i < args.length ) {
            if (args[i].toUpperCase().startsWith("-CONFIG")) {
                mServerCfgFilename = args[i+1];
                i++;
            }
            i++;
        }


        if (mServerCfgFilename == null) {
            System.out.println("server configuration file is not provided, i.e. \"-configuration <turf-server-configuration-file>\"");
            System.exit(0);
        }

        File tFile = new File(mServerCfgFilename);
        if (!tFile.exists()) {
            System.out.println("Can not find configuration file \"" + mServerCfgFilename + "\"");
            System.exit(0);
        }

        if (!tFile.canRead()) {
                System.out.println("Do not have read access to configuration file \"" + mServerCfgFilename + "\"");
                System.exit(0);
        }

        mServerCfg = new ServerConfiguration();
        mServerCfg.parseConfiguration( mServerCfgFilename );
    }

    @Override
    public MongoAux getDbAux() { return mDbAux; }

    @Override
    public Element getGetAndLoadCurrentConfiguration() {
        return this.mServerCfg.getGetAndLoadCurrentConfiguration();
    }

    @Override
    public TurfZone getZoneById(int pId) {
        return mZoneDictionary.getZoneById( pId );
    }

    @Override
    public TurfZone getZoneByName(String pName) {
        return mZoneDictionary.getZonebyName( pName );
    }

    @Override
    public Map<Integer, List<TurfZone>> getZonesByRegionIds()
    {
        return this.mZoneDictionary.getZonesByRegionsId();
    }
    @Override
    public List<TurfZone> getZonesByRegionId( int pRegionId )
    {
        return this.mZoneDictionary.getZonesByRegionId( pRegionId );
    }

    @Override
    public Map<String, List<TurfZone>> getZonesByRegionNames()
    {
        return this.mZoneDictionary.getZonesByRegionsNames();
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
            mLogger.info("Turf server declare tcp/ip listener on interface " +  mServerCfg.getTcpIpInterface() + " port " + mServerCfg.getTcpIpServerPort());
        }
        catch( IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tcpInboundConnection(TcpThread pThread) {

        mLogger.info("tcp/ip inbound connection, " + pThread.toString());

        pThread.setCallback(this);
        pThread.start();
    }


    private void sendResponse( TcpThread pTcpThread, String pJsonMessageString )
    {
        try {
            byte[] tBuffer = pJsonMessageString.getBytes("ISO-8859-1");
            pTcpThread.send( tBuffer );
        }
        catch( Exception e) {
            mLogger.error("tcp/ip send exception, " + pTcpThread.toString(), e);
            pTcpThread.close();
        }
    }

    private JsonObject turfRequestDispatch( String pJsonRequest ) {
        MessageFactory tFactory = new MessageFactory();

        String tJsonRqstMsgName = null;

        // Check if we can extract message name from the request message
        try {
            tJsonRqstMsgName = tFactory.getJsonMessageId( pJsonRequest );
        }
        catch( NameNotFoundException e) {
            mLogger.error("Failed to parse servlet Json request messsage", e);
            return TGStatus.createError( "failed to retreive Turf Rqst Msg Id from servlet Json request", null).toJson();
        }

        MessageInterface tRqstMsg = tFactory.getMessageInstance( pJsonRequest );
        if (tRqstMsg == null) {
            return TGStatus.createError( "Unknown Turf Rqst \"" + tJsonRqstMsgName + "\"", null).toJson();
        }

        if (tRqstMsg.getMessageName().startsWith("HM_")) {
            return this.mZoneHeatMapService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("ZD_")) {
            return this.mZoneDensityService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("DR_")) {
            return this.mDayRankingService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("ZN_")) {
            return this.mZoneNotifierService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("RS_")) {
            return this.mRegionStatService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("TG_")) {
            return this.execute( tRqstMsg );
        }


        // No service found for request message
        return TGStatus.createError("No service for for request message \"" + tJsonRqstMsgName + "\"", null).toJson();
    }

    @Override
    public void tcpMessageRead(TcpThread pThread, byte[] pBuffer) {
        String tRequest = null;
        JsonObject jResponse = null;

        try {
            tRequest = new String(pBuffer, "ISO-8859-1");
            mLogger.trace("[server-request] " + tRequest);
            jResponse = turfRequestDispatch( tRequest );
        } catch (UnsupportedEncodingException e) {
            jResponse = TGStatus.createError("Invalid encoded request", e).toJson();
        }

        // Send response
        mLogger.trace("[server-response] " + jResponse.toString());
        sendResponse(pThread, jResponse.toString());
    }

    @Override
    public void tcpErrorEvent(TcpThread pThread, IOException pException) {
        pThread.close();
    }

    private JsonObject executeUserLogon( TG_LogonRqst pRqstMsg ) {

        List<User> tUsers = mDbAux.findUser( pRqstMsg.getMailAddress().get());
        if ((tUsers == null) || (tUsers.size() == 0)) {
            mLogger.warn("Logon failure, user \"" + pRqstMsg.getMailAddress().get() +"\" does not exist");
            return TGStatus.create(false,"Unknown user or invalid password", null).toJson();
        }
        User tUser = tUsers.get(0);

        if (!tUser.getConfirmed().get()) {
            return TGStatus.create(false,"Login failed, user mail address is not yet confirmed", null).toJson();
        }

        if (tUser.getPassword().get().compareTo(PasswordRules.hashPassword( pRqstMsg.getPassword().get())) != 0) {
            mLogger.warn("Logon failure, invalid password for user \"" + pRqstMsg.getMailAddress().get() +"\"");
            return TGStatus.create(false,"Unknown user or invalid password", null).toJson();
        }

        tUser.setLastLogin( Turf.SDF.format( System.currentTimeMillis()));
        tUser.setLoginCounts( tUser.getLoginCounts().get() + 1);
        mDbAux.updateUser( tUser.getMailAddr().get(), tUser, false);

        return TGStatus.create(true,"Successfull logon", "/turf/zn.html").toJson();
    }

    private JsonObject executeWhenInitialized( TG_WhenInitializedRqst pRqstMsg )
    {
        List<FirstEntry> tFirstEntryList = mDbAux.findAllFirstEntry();
        if ((tFirstEntryList == null) || (tFirstEntryList.size() == 0)) {
            return TGStatus.create(false,"System initialization info not found", null).toJson();
        }

        FirstEntry fe = tFirstEntryList.get(0);

        TG_WhenInitializedRsp jRsp = new TG_WhenInitializedRsp();
        jRsp.setDateTime( fe.getTime().get());
        String arr[] = fe.getTime().get().split(" ");
        jRsp.setDate( arr[0]);
        return jRsp.toJson();
    }
    private JsonObject execute( MessageInterface pRqstMsg ) {
        if (pRqstMsg instanceof TG_LogonRqst) {
            return executeUserLogon((TG_LogonRqst) pRqstMsg);
        }

        if (pRqstMsg instanceof TG_WhenInitializedRqst) {
            return executeWhenInitialized((TG_WhenInitializedRqst) pRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method found for request \"" +
                pRqstMsg.getMessageName() + "\"", null ).toJson();
    }

    static class ExecServerTasksThread extends Thread
    {
        private TurfServer mTurfServer;

        ExecServerTasksThread( TurfServer pTurfServer ) {
            mTurfServer = pTurfServer;
        }

        public void run() {
            setName("WrapperApplicationTask");
            mTurfServer.execServerTasks();
        }

    }


    @Override
    public Integer start(String[] args) {
        TurfServer tf = new TurfServer();
        tf.parseConfiguration(args);
        tf.initialize();

        cServerTaskThread = new ExecServerTasksThread( tf );
        cServerTaskThread.start();
        tf.mLogger.info("[TurfServer] started complete (wrapper)");
        return null;
    }

    @Override
    public int stop(int pExitCode) {
        cServerShouldExit = true;
        if (cServerTaskThread != null) {
            try {cServerTaskThread.interrupt();}
            catch( Exception e) { e.printStackTrace();}
        }
        return pExitCode;
    }

    @Override
    public void controlEvent(int pEvent) {
        if ((pEvent == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT) && (WrapperManager.isIgnoreUserLogoffs())) {
            // Ignore
        } else {
            WrapperManager.stop(0);
        }
    }
}


