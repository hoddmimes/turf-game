package com.hoddmimes.turf.server;


import com.hoddmimes.turf.server.common.Zone;
import com.hoddmimes.turf.server.common.ZoneDictionary;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public interface TurfServerInterface
{
    public Zone getZoneById(int pId );
    public Zone getZoneByName( String pName );
    public Map<String, List<Zone>> getZonesByRegions();

    public ServerConfiguration getServerConfiguration();
    public MongoAux getDbAux();


    public void log( String pMessage );
    public void logEx( String pMsg, Throwable e);
    public void logW( String pMsg );
    public void logE( String pMsg );
    public void logF( String pMsg, Throwable e);
    public Logger getLogger();


}
