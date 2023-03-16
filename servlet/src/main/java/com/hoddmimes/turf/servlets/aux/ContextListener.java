package com.hoddmimes.turf.servlets.aux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.InetAddress;


public class ContextListener implements ServletContextListener
{
    static private final String PROD_HOST_NAME = "hoddmimes.com";
    @Override
    public void contextInitialized(ServletContextEvent pEvent ) {
        ServletContext tServletContext = pEvent.getServletContext();

        String tPrefix  = (getHostName().contentEquals(PROD_HOST_NAME)) ? "" : "WebContent/";

        String tLog4jConfigFile = tPrefix + tServletContext.getInitParameter("log4jConfiguration");
        LoggerContext context  = (LoggerContext)LogManager.getContext(false);
        File tConfigFile = new File(  tLog4jConfigFile );
        if (tConfigFile.exists()) {
            System.out.println( "config-file: " + tConfigFile.getAbsolutePath());
        } else {
            System.out.println( "config-file not found at " + tConfigFile.getAbsolutePath());
        }
        context.setConfigLocation(tConfigFile.toURI());
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName(); // etc/hostname i.e. "hoddmimes.com"
        }
        catch(Exception e) {
            e.printStackTrace();
            return PROD_HOST_NAME;
        }
    }

}
