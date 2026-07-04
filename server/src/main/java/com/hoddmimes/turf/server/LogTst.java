package com.hoddmimes.turf.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class LogTst
{
    public static void main(String[] args) {
        Logger tLogger = LogManager.getLogger( LogTst.class);

        if (tLogger.isTraceEnabled()) {
            System.out.println("Trace is enabled");
        }
        if (tLogger.isDebugEnabled()) {
            System.out.println("Debug is enabled");
        }
        if (tLogger.isFatalEnabled()) {
            System.out.println("Fatal is enabled");
        }
        if (tLogger.isErrorEnabled()) {
            System.out.println("Error is enabled");
        }
        if (tLogger.isWarnEnabled()) {
            System.out.println("Warning is enabled");
        }



        tLogger.info("info message iii");
        tLogger.error("error message eee");
        tLogger.warn("warning message www");
        tLogger.debug("debug message ddd");
        for (int i = 0; i < 20; i++) {
            tLogger.trace("trace message i: " + i);
        }

        tLogger.debug("LOG4J2 End testing message");
        try { Thread.sleep(2000L); }
        catch( InterruptedException e) {}
    }
}
