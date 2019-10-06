package com.hoddmimes.turf.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import com.hoddmimes.turf.server.configuration.ZoneNotifyConfiguration;
import com.hoddmimes.turf.server.generate.MongoAux;

import java.util.List;


public class ZoneNotifierService
{
    private ServerConfiguration mServerCfg;
    private EventFilterNewZoneTakeOver tZoneFilter = null;
    private MongoAux mDbAux;

    public void initialize( ServerConfiguration pServerCfg ) {
        mServerCfg = pServerCfg;
        tZoneFilter = new EventFilterNewZoneTakeOver();

        // Connect to Database
        ZoneNotifyConfiguration tCfg = mServerCfg.getZoneNotifyConfiguration();
        mDbAux = new MongoAux( tCfg.getDbName(), tCfg.getDbHost(), tCfg.getDbPort());
    }

    public void zonesUpdated( JsonElement pZoneUpdates) {

        List<ZoneEvent> tZones = tZoneFilter.getNewTakeover( pZoneUpdates.getAsJsonArray() );


    }

}
