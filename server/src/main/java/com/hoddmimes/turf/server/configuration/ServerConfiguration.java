package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServerConfiguration
{
    private ZoneNotifyConfiguration mZoneNotifyCfg = null;



    Element mRoot = null;

    public void parseConfiguration( String mConfigurationFile ) {
        try {
            mRoot = XmlAux.loadXMLFromFile(mConfigurationFile).getDocumentElement();
            if (XmlAux.isElementPresent( mRoot, "ZoneNotify")) {
                mZoneNotifyCfg = new ZoneNotifyConfiguration();
                mZoneNotifyCfg.parse(mRoot);
            }
        }
        catch( Exception e) {
            System.out.println("Failed to load configuration file \"" + mConfigurationFile + "\"");
            e.printStackTrace();
            System.exit(0);
        }
    }


    public boolean startZoneNotify() {
        return (mZoneNotifyCfg == null) ? false : true;
    }

    public ZoneNotifyConfiguration getZoneNotifyConfiguration() {
        return mZoneNotifyCfg;
    }

}
