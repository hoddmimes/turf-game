package com.hoddmimes.turf.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hoddmimes.turf.server.common.EventFilterNewZoneTakeOver;
import com.hoddmimes.turf.server.common.Mailer;
import com.hoddmimes.turf.server.common.Turf;
import com.hoddmimes.turf.server.common.ZoneEvent;
import com.hoddmimes.turf.server.configuration.ServerConfiguration;
import com.hoddmimes.turf.server.configuration.ZoneNotifyConfiguration;
import com.hoddmimes.turf.server.generate.MongoAux;
import com.hoddmimes.turf.server.generate.Subscription;

import java.util.List;


public class ZoneNotifierService
{
    private TurfServerInterface mTurfIf;
    private EventFilterNewZoneTakeOver tZoneFilter = null;
    private MongoAux mDbAux;
    private Mailer mMailer;


    public void initialize( TurfServerInterface pTurfServerInterface ) {
        mTurfIf = pTurfServerInterface;
        tZoneFilter = new EventFilterNewZoneTakeOver();

        // Connect to Database
        ZoneNotifyConfiguration tCfg = pTurfServerInterface.getServerConfiguration().getZoneNotifyConfiguration();
        mDbAux = new MongoAux( tCfg.getDbName(), tCfg.getDbHost(), tCfg.getDbPort());
        mDbAux.connectToDatabase();

        // Initialize mailer
        mMailer =   new  Mailer(tCfg.getMailerHost(),
                587,
                tCfg.getMailerUser(),
                tCfg.getMailerPassword(),
                "text/html", true);
    }

    public void zonesUpdated( JsonElement pZoneUpdates) {
        List<ZoneEvent> tZones = tZoneFilter.getNewTakeover( pZoneUpdates.getAsJsonArray() );
        for( ZoneEvent ze: tZones ) {
            List<Subscription> tSubscriptions = mDbAux.findSubscriptionByZoneId( ze.getZoneId() );
            for( Subscription sub : tSubscriptions ) {
                notifyUser( sub, ze );
            }
        }
    }


    private String createBody( ZoneEvent pZonEvt ) {
        String tTimStr = Turf.SDF.format(pZonEvt.getLatestTakeOverTime());
        StringBuilder sb = new StringBuilder();
        sb.append("<html><br><br><br>");
        sb.append("<p align=\"center\" style=\"font-family:verdana;font-size:120%;\">");
        sb.append("Take over of \"" + pZonEvt.getZoneName() + "\"<br>" );
        sb.append("Take over at " + tTimStr + "<br>");
        sb.append("Current owner\"" + pZonEvt.getCurrentOwner() +"\" current owner \"" + pZonEvt.getPreviousOwner() + "\" <br>");
        sb.append("</p></html>");
        return sb.toString();
    }

    public void notifyUser( Subscription pSubscr, ZoneEvent pZonEvt ) {
        String tBody = createBody( pZonEvt );
    }
}
