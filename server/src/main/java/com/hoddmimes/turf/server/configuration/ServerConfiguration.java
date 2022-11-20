package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;

public class ServerConfiguration
{
    private String mBaseURL;
    private String mConfigurationFilename;
    private int mApiCollectIntervalSec;
    private int mApiTimeZoneOffsetHr;
    private int mApiHistoryOffsetMin;

    private String mTcpIpInterface;
    private int mTcpIpServerPort;

    private String mLocalAllZoneDB;

    String mDbHost = null;
    int    mDbPort = 0;
    String mDbName = null;


    private ZoneNotifyConfiguration mZoneNotifyCfg = null;
    private RegionStatConfiguration mRegionStatCfg = null;
    private UserTraceConfiguration  mUserTraceCfg = null;
    private DayRankingConfiguration  mDayRankCfg = null;

    private ZoneHeatMapConfiguration mZoneHeatMapCfg = null;



    Element mRoot = null;

    public Element getGetAndLoadCurrentConfiguration() {
        try {
            Element tRoot = XmlAux.loadXMLFromFile(mConfigurationFilename).getDocumentElement();
            return tRoot;
        }
        catch( Exception e) {
            System.out.println("Failed to load configuration file \"" + mConfigurationFilename + "\"");
            e.printStackTrace();
        }
        return null;
    }


    public void parseConfiguration( String tConfigurationFile ) {
        mConfigurationFilename = tConfigurationFile;
        try {
            mRoot = XmlAux.loadXMLFromFile(tConfigurationFile).getDocumentElement();

            mBaseURL = mRoot.getAttribute("baseURL").toString();

            if (XmlAux.isElementPresent(mRoot, "ZoneNotifyService")) {
                mZoneNotifyCfg = new ZoneNotifyConfiguration();
                mZoneNotifyCfg.parse(mRoot);
            }

            if (XmlAux.isElementPresent(mRoot, "DayRankingService")) {
                mDayRankCfg = new DayRankingConfiguration();
                mDayRankCfg.parse(mRoot);
            }

            if (XmlAux.isElementPresent(mRoot, "ZoneHeatMap")) {
                mZoneHeatMapCfg = new ZoneHeatMapConfiguration();
                mZoneHeatMapCfg.parse(mRoot);
            }

            if (XmlAux.isElementPresent(mRoot, "RegionStatisticsService")) {
                mRegionStatCfg = new RegionStatConfiguration();
                mRegionStatCfg.parse(mRoot);
            }

            if (XmlAux.isElementPresent(mRoot, "UserTraceService")) {
                mUserTraceCfg = new UserTraceConfiguration();
                mUserTraceCfg.parse(mRoot);
            }


            Element tTurfAPI = XmlAux.getElement(mRoot, "TurfApi");
            mApiCollectIntervalSec = XmlAux.getIntAttribute( tTurfAPI, "zoneCollectIntervalSec", 30);
            mApiTimeZoneOffsetHr =  XmlAux.getIntAttribute( tTurfAPI, "timeZoneOffsetHr", 30);
            mApiHistoryOffsetMin =  XmlAux.getIntAttribute( tTurfAPI, "historyOffsetMin", 30);
            mLocalAllZoneDB = XmlAux.getStringAttribute( tTurfAPI, "localZoneDB", null );

            Element tTcpIp= XmlAux.getElement(mRoot, "TcpIp");
            mTcpIpInterface = XmlAux.getStringAttribute( tTcpIp, "interface", "0.0.0.0");
            mTcpIpServerPort = XmlAux.getIntAttribute( tTcpIp, "serverPort", 9393);

            Element tDBElement = XmlAux.getElement( mRoot, "Database");
            mDbHost = XmlAux.getStringAttribute(tDBElement,"host", null);
            mDbPort = XmlAux.getIntAttribute(tDBElement,"port", 0);
            mDbName =  XmlAux.getStringAttribute(tDBElement,"database", null);

        }
        catch( Exception e) {
            System.out.println("Failed to load configuration file \"" + tConfigurationFile + "\"");
            e.printStackTrace();
            System.exit(0);
        }
    }




    public String getBaseURL() { return this.mBaseURL; }

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


    public String getDbHost() {
        return this.mDbHost;
    }

    public String getDbName() {
        return this.mDbName;
    }

    public int getDbPort() {
        return this.mDbPort;
    }

    public String getLocalAllZonesDBFile() {
        return mLocalAllZoneDB;
    }

    public boolean startZoneNotify() {
        return (mZoneNotifyCfg == null) ? false : true;
    }
    public boolean startRegionStat() { return (mRegionStatCfg == null) ? false : true; }
    public boolean startUserTrace() {
        return (mUserTraceCfg == null) ? false : true;
    }
    public boolean startDayRanking() {
        return (mDayRankCfg == null) ? false : true;
    }
    public boolean startZoneHeatMap() {
        return (mZoneHeatMapCfg == null) ? false : true;
    }

    public ZoneNotifyConfiguration getZoneNotifyConfiguration() {
        return mZoneNotifyCfg;
    }
    public RegionStatConfiguration getRegionStatConfiguration() {
        return mRegionStatCfg;
    }
    public UserTraceConfiguration getUserTraceConfiguration() { return mUserTraceCfg; }
    public DayRankingConfiguration getDayRankingConfiguration() { return mDayRankCfg; }

    public ZoneHeatMapConfiguration getZoneHeatMapConfiguration() { return mZoneHeatMapCfg; }

}
