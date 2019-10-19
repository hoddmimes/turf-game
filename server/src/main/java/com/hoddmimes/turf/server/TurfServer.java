package com.hoddmimes.turf.server;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.MessageFactory;
import com.hoddmimes.turf.common.generated.TG_LogonRqst;
import com.hoddmimes.turf.common.transport.TcpServer;
import com.hoddmimes.turf.common.transport.TcpServerCallbackIf;
import com.hoddmimes.turf.common.transport.TcpThread;
import com.hoddmimes.turf.common.transport.TcpThreadCallbackIf;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.Zone;
import com.hoddmimes.turf.server.common.ZoneDictionary;
import com.hoddmimes.turf.server.configuration.PasswordRules;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;

import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;


import javax.naming.NameNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


public class TurfServer implements TurfServerInterface, TcpServerCallbackIf, TcpThreadCallbackIf, WrapperListener
{
    private static boolean cDaemonMode = true;
    static volatile boolean cServerShoulExit = false;
    static ExecServerTasksThread cServerTaskThread = null;

    private ServerConfiguration mServerCfg = null;
    private ZoneNotifierService mZoneNotifierService = null;
    private ZoneDictionary mZoneDictory = null;

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
        mLogger.debug("[getZoneCollectTimeOffset] local-time: " + tTimStr + " url-time: " +  tURlString );
        return tURlString;
    }
    private JsonElement getZoneEvents() {
        String tFromTimStr = getZoneCollectTimeOffset( mServerCfg.getApiHistoryCollectOffsetMin(), mServerCfg.getApiTimeZoneOffetHr());
        JsonElement tRsp = Turf.turfServerGET( "feeds?afterDate=" + tFromTimStr);
        return tRsp;
    }

    private int countZones( JsonElement pJsonElement ) {
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
        while(!(cServerShoulExit )) {
            JsonElement tZoneUpdateRsp = getZoneEvents();
            mLogger.debug("[zonesUpdated] zones " + countZones(tZoneUpdateRsp) + " retreived");

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

        mDbAux = new MongoAux( mServerCfg.getDbName(), mServerCfg.getDbHost(), mServerCfg.getDbPort());
        mDbAux.connectToDatabase();


        mZoneDictory = new ZoneDictionary();
        mLogger.info("ZoneDictionary loaded [" + mZoneDictory.getSize() + "] zones.");
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
    public MongoAux getDbAux() { return mDbAux; }
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
    public Map<String, List<Zone>> getZonesByRegions()
    {
        return this.mZoneDictory.getZonesByRegions();
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

    private String turfRequestDispatch( String pJsonRequest ) {
        MessageFactory tFactory = new MessageFactory();

        String tJsonRqstMsgName = null;

        // Check if we can extract message name from the request message
        try {
            tJsonRqstMsgName = tFactory.getJsonMessageId( pJsonRequest );
        }
        catch( NameNotFoundException e) {
            mLogger.error("Failed to parse servlet Json request messsage", e);
            return TGStatus.createError( "failed to retreive Turf Rqst Msg Id from servlet Json request", null).toJson().toString();
        }

        MessageInterface tRqstMsg = tFactory.getMessageInstance( pJsonRequest );
        if (tRqstMsg == null) {
            return TGStatus.createError( "Unknown Turf Rqst \"" + tJsonRqstMsgName + "\"", null).toJson().toString();
        }

        if (tRqstMsg.getMessageName().startsWith("ZN_")) {
            return this.mZoneNotifierService.execute( tRqstMsg );
        }

        if (tRqstMsg.getMessageName().startsWith("TG_")) {
            return this.execute( tRqstMsg );
        }

        // No service found for request message
        return TGStatus.createError("No service for for request message \"" + tJsonRqstMsgName + "\"", null).toJson().toString();
    }

    @Override
    public void tcpMessageRead(TcpThread pThread, byte[] pBuffer) {
        String tRequest = null;
        String tResponse = null;

        try {
            tRequest = new String(pBuffer, "ISO-8859-1");
            mLogger.trace("[server-request] " + tRequest);
            tResponse = turfRequestDispatch( tRequest );
        } catch (UnsupportedEncodingException e) {
            tResponse = TGStatus.createError("Invalid encoded request", e).toJson().toString();
        }

        // Send response
        mLogger.trace("[server-response] " + tResponse);
        sendResponse(pThread, tResponse);
    }

    @Override
    public void tcpErrorEvent(TcpThread pThread, IOException pException) {
        mLogger.warn("tcp/ip client disconnect " + pThread.toString(), pException);
        pThread.close();
    }

    private String executeUserLogon( TG_LogonRqst pRqstMsg ) {

        List<User> tUsers = mDbAux.findUser( pRqstMsg.getMailAddress().get());
        if ((tUsers == null) || (tUsers.size() == 0)) {
            mLogger.warn("Logon failure, user \"" + pRqstMsg.getMailAddress().get() +"\" does not exist");
            return TGStatus.create(false,"Unknown user or invalid password", null).toJson().toString();
        }
        User tUser = tUsers.get(0);

        if (!tUser.getConfirmed().get()) {
            return TGStatus.create(false,"Login failed, user mail address is not yet confirmed", null).toJson().toString();
        }

        if (tUser.getPassword().get().compareTo(PasswordRules.hashPassword( pRqstMsg.getPassword().get())) != 0) {
            mLogger.warn("Logon failure, invalid password for user \"" + pRqstMsg.getMailAddress().get() +"\"");
            return TGStatus.create(false,"Unknown user or invalid password", null).toJson().toString();
        }

        tUser.setLastLogin( Turf.SDF.format( System.currentTimeMillis()));
        tUser.setLoginCounts( tUser.getLoginCounts().get() + 1);
        mDbAux.updateUser( tUser.getMailAddr().get(), tUser, false);

        return TGStatus.create(true,"Successfull logon", "/turf/turfZoneNotification.html").toJson().toString();
    }

    private String execute( MessageInterface pRqstMsg ) {
        if (pRqstMsg instanceof TG_LogonRqst) {
            return executeUserLogon((TG_LogonRqst) pRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method found for request \"" +
                pRqstMsg.getMessageName() + "\"", null ).toJson().toString();
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
        cServerShoulExit = true;
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


