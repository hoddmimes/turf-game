package com.hoddmimes.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.net.MalformedURLException;

public class Server
{
    Tomcat mTomcatServer;
    String mBaseDir =  "./temp";
    String mAppDir = "WebContent/";
    int    mPort = 8282;
    String   mHostname = "localhost";

    private void execute() {
        mTomcatServer = new Tomcat();
        mTomcatServer.setBaseDir(mBaseDir);
        mTomcatServer.setPort( mPort );
        mTomcatServer.setHostname( mHostname);


        // Define a web application context.

        /**
         * Accessing the servlets (in the embedded environment)
         * the addWebApp, contectParam will set the path that the prefix/context that servlet can be accessed i.e
         * http://localhost:8282/rest/<plus-URL-mapping-found-in-fil-web.xml>
         * Note! when using jersey the the servel mapping is determined by the @Path in the class see com.hoddmimes.rest.Hello.java
         */

        try {
            System.out.println("appdir: " + new File(mAppDir).getAbsolutePath());
            Context context = mTomcatServer.addWebapp("/rest", new File(mAppDir).getAbsolutePath());
            File configFile = new File(mAppDir + "WEB-INF/web.xml");
            context.setConfigFile(configFile.toURI().toURL());
        }
        catch( MalformedURLException e) {
            e.printStackTrace();
        }

        // Define and bind web.xml file location.

        try {

            mTomcatServer.start();

            mTomcatServer.getServer().await();
        }
        catch( LifecycleException e) {
            e.printStackTrace();
        }

    }

    public static void main( String[] args ) {
        Server srv = new Server();
        srv.execute();
    }
}
