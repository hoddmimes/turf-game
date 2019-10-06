package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;

public class ServerConfiguration
{
    private int mApiCollectIntervalSec;
    private int mApiTimeZoneOffsetHr;
    private int mApiHistoryOffsetMin;

    private String mTcpIpInterface;
    private int mTcpIpServerPort;


    private ZoneNotifyConfiguration mZoneNotifyCfg = null;



    Element mRoot = null;

    public void parseConfiguration( String mConfigurationFile ) {
        try {
            mRoot = XmlAux.loadXMLFromFile(mConfigurationFile).getDocumentElement();
            if (XmlAux.isElementPresent(mRoot, "ZoneNotify")) {
                mZoneNotifyCfg = new ZoneNotifyConfiguration();
                mZoneNotifyCfg.parse(mRoot);
            }


            Element tTurfAPI = XmlAux.getElement(mRoot, "TurfApi");
            mApiCollectIntervalSec = XmlAux.getIntAttribute( tTurfAPI, "zoneCollectIntervalSec", 30);
            mApiTimeZoneOffsetHr =  XmlAux.getIntAttribute( tTurfAPI, "timeZoneOffsetHr", 30);
            mApiHistoryOffsetMin =  XmlAux.getIntAttribute( tTurfAPI, "historyOffsetMin", 30);

            Element tTcpIp= XmlAux.getElement(mRoot, "TcpIp");
            mTcpIpInterface = XmlAux.getStringAttribute( tTcpIp, "interface", "0.0.0.0");
            mTcpIpServerPort = XmlAux.getIntAttribute( tTcpIp, "serverPort", 9393);


        }
        catch( Exception e) {
            System.out.println("Failed to load configuration file \"" + mConfigurationFile + "\"");
            e.printStackTrace();
            System.exit(0);
        }
    }


    public int getApiZoneCollectIntervaSec() {
        return this.mApiCollectIntervalSec;
    }

    public long getApiZoneCollectIntervalMs() {
        return (long) (this.mApiCollectIntervalSec  * 1000L);
    }

    public int getApiTimeZoneOffetHr() {
        return this.mApiTimeZoneOffsetHr;
    }

    public int getApiHistoryCollectOffsetMin() {
        return this.mApiHistoryOffsetMin;
    }

    public int getTcpIpServerPort() {
        return mTcpIpServerPort;
    }

    public String getTcpIpInterface() {
        return mTcpIpInterface;
    }





    public boolean startZoneNotify() {
        return (mZoneNotifyCfg == null) ? false : true;
    }

    public ZoneNotifyConfiguration getZoneNotifyConfiguration() {
        return mZoneNotifyCfg;
    }

}