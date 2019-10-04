package com.hoddmimes.turf.server;


import com.hoddmimes.turf.server.configuration.ServerConfiguration;

import java.io.File;

public class TurfServer
{
    ServerConfiguration mServerCfg = null;

    public static void main( String args[] ) {
        TurfServer tf = new TurfServer();
        tf.parseConfiguration( args );
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


