package com.hoddmimes.turf.server;


import com.hoddmimes.turf.server.common.Zone;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import org.apache.logging.log4j.Logger;

public interface TurfServerInterface
{
    public Zone getZoneById(int pId );
    public Zone getZoneByName( String pName );

    ServerConfiguration getServerConfiguration();

    public void log( String pMessage );
    public void logEx( String pMsg, Throwable e);
    public void logW( String pMsg );
    public void logE( String pMsg );
    public void logF( String pMsg, Throwable e);
    public Logger getLogger();


}
