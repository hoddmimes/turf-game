package com.hoddmimes.turf.servlets;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoddmimes.turf.common.transport.TcpClient;
import com.hoddmimes.turf.common.transport.TcpClientSync;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ServletResources extends HttpServlet implements ServletResourcesIf
{
    private  Logger mLogger;
    private static volatile ServletResources cInstance = null;
    private TcpClientSync mTcpClient = null;
    private volatile TcpClientSync mTcpClientTest = null; // Connection to test system
    private List<String> mMsgsToTestSystem;
    private List<String> mMsgsToLog;
    private TestConnector mTestConnector = null;



    public static ServletResourcesIf getInstance()
    {
        return cInstance;
    }

    public ServletResources() {
        super();
        cInstance = this;
        mMsgsToTestSystem = new ArrayList<>();
        mMsgsToLog = new ArrayList<>();
    }

    public void init(ServletConfig pConfig) throws ServletException {


        super.init(pConfig);

        createWatermark();

        String tLogilename = pConfig.getInitParameter("logfile");
        Boolean tAppendMode = Boolean.parseBoolean(pConfig.getInitParameter("appendMode"));
        Logger.setLogfile( tLogilename,tAppendMode );
        mLogger = Logger.getInstance();

        String tMsgsListToLogfile = pConfig.getInitParameter("messagesToLogfile");
        if ((tMsgsListToLogfile != null) && (tMsgsListToLogfile.length() > 0))
        {
            tMsgsListToLogfile.replace(" ", "");
            String[] tMsgArr = tMsgsListToLogfile.split(",");
            if (tMsgArr != null) {
                for (int i = 0; i < tMsgArr.length; i++) {
                    mMsgsToLog.add( tMsgArr[i] );
                }
            }
        }

        if (Boolean.parseBoolean( pConfig.getInitParameter("turfTestServerEnabled"))) {
                String tMsgsString = pConfig.getInitParameter("messagesToTestSystem");
                if (tMsgsString != null) {
                    tMsgsString.replace(" ","");
                    String[] tMsgArr = tMsgsString.split(",");
                    if (tMsgArr != null) {
                        for (int i = 0; i < tMsgArr.length; i++) {
                            mMsgsToTestSystem.add( tMsgArr[i] );
                        }
                    }
                }
                //Create Transport connection to TurfServer
                mTestConnector = new TestConnector( pConfig.getInitParameter("turfTestServerHost"),
                                                    Integer.parseInt( pConfig.getInitParameter("turfTestServerPort")));
                mTestConnector.start();

        }

        try {
            //Create Transport connection to TurfServer
            String tHost = pConfig.getInitParameter("turfServerHost");
            int tPort = Integer.parseInt( pConfig.getInitParameter("turfServerPort"));
            mTcpClient = new TcpClientSync( tHost, tPort);
            mLogger.log("Successfully connected to turf server \"" + tHost + "\" on port " + tPort );
        }
        catch (Exception e ) {
            mLogger.error("Failed to initialize Turf servlet setup", e );
            throw new ServletException( "Failed to initialize Turf Servlet setup", e );
        }
    }

    private void createWatermark() {
        try {
            FileOutputStream tOut = new FileOutputStream("watermark");
            tOut.write("watermark".getBytes());
            tOut.flush();
            tOut.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean msgToTestSystem( String pJsonRqstMsg ) {
        if (mTcpClientTest == null) {
            return false;
        }
        JsonElement jRqstElement = JsonParser.parseString(pJsonRqstMsg);
        if ((jRqstElement != null) && (jRqstElement.isJsonObject())) {
            JsonObject jRqstMsg = jRqstElement.getAsJsonObject();
            String tMsgName = jRqstMsg.keySet().stream().findFirst().orElse("");
            for (String tFilterMsg : mMsgsToTestSystem) {
                if (tMsgName.startsWith(tFilterMsg)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean msgToLog( String pJsonRqstMsg ) {
        JsonElement jRqstElement = JsonParser.parseString(pJsonRqstMsg);
        if ((jRqstElement != null) && (jRqstElement.isJsonObject())) {
            JsonObject jRqstMsg = jRqstElement.getAsJsonObject();
            String tMsgName = jRqstMsg.keySet().stream().findFirst().orElse("");
            for (String tFilterMsg : mMsgsToLog) {
                if (tMsgName.startsWith(tFilterMsg)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String sendToTurfServer( String pJsonRequest ) throws IOException {
        String tResponse = null;

        if (msgToTestSystem( pJsonRequest)) {
            if (msgToLog( pJsonRequest )) {
                mLogger.log("[to-test-server] " + pJsonRequest);
            }
            tResponse = new String( mTcpClientTest.transcieve( pJsonRequest.getBytes("ISO-8859-1")), "ISO-8859-1");
            if (msgToLog( tResponse )) {
                mLogger.log("[from-test-server] " + tResponse );
            }
        } else {
            if (msgToLog( pJsonRequest )) {
                mLogger.log("[to-server] " + pJsonRequest);
            }
            tResponse = new String( mTcpClient.transcieve( pJsonRequest.getBytes("ISO-8859-1")), "ISO-8859-1");
            if (msgToLog( tResponse )) {
                mLogger.log("[from-server] " + tResponse );
            }
        }
        return tResponse;
    }


    /**
     * Reads the contents data from a POST request
     * the input stream is an input pointer to the contents body
     *
     * @param pInputStream, pointer to the contents body input stream
     * @return String read
     */

    public static  String readContentsBody( InputStream pInputStream ) {
        int tSize = 0;
        char[] tBuffer = new char[256];
        InputStreamReader tIn = new InputStreamReader(pInputStream, StandardCharsets.ISO_8859_1);
        StringBuilder tBody = new StringBuilder();
        while (tSize >= 0) {
            try {
                tSize = tIn.read(tBuffer);
                if (tSize != -1) {
                    tBody.append(tBuffer, 0, tSize);
                }
            }
            catch( IOException e) {
                e.printStackTrace(); // ToDo: should the exception be pushed up ?
                return null;
            }
        }
        String tContent = tBody.toString();
        return tContent;
    }

    public static void sendResponse(JsonObject pJsonMsg, HttpServletResponse response ) throws Exception {
        response.setStatus( 200 );
        response.setContentType("application/json");

        byte[] tBuffer = pJsonMsg.toString().getBytes();
        response.setContentLength( tBuffer.length );
        ServletOutputStream out = response.getOutputStream();
        out.write( tBuffer );
        out.flush();
    }

    class TestConnector extends Thread
    {
        String mHost;
        int mPort;

        TestConnector( String pHost, int pPort ) {
            mHost = pHost;
            mPort = pPort;
        }
        public void run() {
            if (mTcpClientTest == null) {
                try {
                    mTcpClientTest = new TcpClientSync( mHost, mPort);
                    mLogger.log("successfully connected to test system \"" + mHost + "\"  on port " + mPort);
                }
                catch (Exception e ) {
                    mLogger.log("Failed to connect to test system \"" + mHost + "\" on port " + mPort + " reason: " + e.getMessage());
                    mTcpClientTest = null;
                }
                try { Thread.sleep(15000L );}
                catch( InterruptedException ie )
                {
                }
            }
        }
    }
}