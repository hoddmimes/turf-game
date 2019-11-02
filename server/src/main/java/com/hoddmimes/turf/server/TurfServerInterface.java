package com.hoddmimes.turf.server;


import com.hoddmimes.turf.server.common.TurfZone;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public interface TurfServerInterface
{
    public TurfZone getZoneById(int pId );
    public TurfZone getZoneByName(String pName );
    public Map<Integer, List<TurfZone>> getZonesByRegionIds();
    public Map<String, List<TurfZone>> getZonesByRegionNames();

    public ServerConfiguration getServerConfiguration();
    public Element getGetAndLoadCurrentConfiguration();
    public MongoAux getDbAux();


}
