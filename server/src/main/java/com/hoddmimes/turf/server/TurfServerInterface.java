package com.hoddmimes.turf.server;


import com.hoddmimes.turf.server.common.Zone;
import com.hoddmimes.turf.server.common.ZoneDictionary;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public interface TurfServerInterface
{
    public Zone getZoneById(int pId );
    public Zone getZoneByName( String pName );
    public Map<Integer, List<Zone>> getZonesByRegionIds();
    public Map<String, List<Zone>> getZonesByRegionNames();

    public ServerConfiguration getServerConfiguration();
    public Element getGetAndLoadCurrentConfiguration();
    public MongoAux getDbAux();


}
