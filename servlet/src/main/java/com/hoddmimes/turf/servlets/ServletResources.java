package com.hoddmimes.turf.servlets;


import com.google.gson.JsonObject;
import com.hoddmimes.turf.common.transport.TcpClient;
import com.hoddmimes.turf.common.transport.TcpClientSync;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.List;


public class ServletResources extends HttpServlet implements ServletResourcesIf
{
    private  static Logger cLogger = LogManager.getLogger( ServletResources.class );
    private static volatile ServletResources cInstance = null;
    private TcpClientSync mTcpClient;




    public static ServletResourcesIf getInstance()
    {
        return cInstance;
    }

    public ServletResources() {
        super();
        cInstance = this;
    }

    public void init(ServletConfig pConfig) throws ServletException {
        super.init(pConfig);

        createWatermark();


        try {
            //Create Transport connection to TurfServer
            String tHost = pConfig.getInitParameter("turfServerHost");
            int tPort = Integer.parseInt( pConfig.getInitParameter("turfServerPort"));
            mTcpClient = new TcpClientSync( tHost, tPort);
        }
        catch (Exception e ) {
            cLogger.error("Failed to initialize Turf servlet setup", e );
            throw new ServletException( "Failed to initialize Turf Serlet setup", e );
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

    @Override
    public String sendToTurfServer( String pJsonRequest ) throws IOException {
        byte[] tResponse = mTcpClient.transcieve( pJsonRequest.getBytes("ISO-8859-1") );
        return new String( tResponse,"ISO-8859-1" );
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
}