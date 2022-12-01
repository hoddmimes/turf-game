package com.hoddmimes.turf.servlets;

import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

public class Logger
{
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static String cOutputFilename = "turf-servlets.log";
    private static boolean cAppendMode = true;
    private static Logger cInstance = null;


    public static void setLogfile( String pOutputLogfileName, boolean pAppendMode ) {
        cOutputFilename = pOutputLogfileName.replace("\"", "");
        cAppendMode = pAppendMode;
    }


    public static Logger getInstance() {
        synchronized ( Logger.class) {
            if (cInstance == null) {
                cInstance = new Logger( cOutputFilename, cAppendMode);
            }
            return cInstance;
        }
    }

    private PrintWriter mFp;

    private Logger( String pOutputFilename, boolean pAppendMode ) {
        openLogfile( pOutputFilename, pAppendMode );
    }

    private void openLogfile( String pFilename, boolean pAppendMode ) {
        try {
            mFp = new PrintWriter( new FileOutputStream( pFilename, pAppendMode ));
            mFp.println("== Starting a new " + SDF.format(System.currentTimeMillis()) + " ==");
            mFp.flush();
        }
        catch( IOException e) {
            e.printStackTrace();
            mFp = null;
        }
    }

    public void error( String pLogMsg, Throwable pException ) {
        if (mFp != null) {
            mFp.println( SDF.format( System.currentTimeMillis()) + " " + pLogMsg);
            pException.printStackTrace( mFp );
            mFp.println("\n\n");
            mFp.flush();
        }
    }
    public void log( String pLogMsg ) {
        if (mFp != null) {
            mFp.println( SDF.format( System.currentTimeMillis()) + " " + pLogMsg);
            mFp.flush();
        }
    }
}
