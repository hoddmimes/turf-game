package com.hoddmimes.turf.server.configuration;

import com.hoddmimes.turf.server.services.regionstat.DebugContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class RegionStatConfiguration extends CoreConfiguration
{

    private DebugContext        mDbgCtx;
    private long                mStatPeriod;
    private List<CfgRegion>   mExtraRegions;
    private double              mTpPphRelation;

    public RegionStatConfiguration() {
        super(true);
    }

    public void parse( Element pRootElement) {

        Element tRSElement = XmlAux.getElement(pRootElement, "RegionStatisticsService");
        long hh = Long.parseLong(tRSElement.getAttribute("period"));
        mStatPeriod = hh * 3600L * 1000L;

        mTpPphRelation = Double.parseDouble(tRSElement.getAttribute("tpPphRelation"));

        super.enable( XmlAux.getBooleanAttribute(tRSElement,"enabled", true));


        mDbgCtx = new DebugContext();
        Element tDbgElement = XmlAux.getElement(tRSElement, "Debug");
        if (XmlAux.getBooleanAttribute(tDbgElement,"memory", false)) {
            mDbgCtx.addDebugFlag( DebugContext.MEMORY );
        }
        if (XmlAux.getBooleanAttribute(tDbgElement,"hourStat", false)) {
            mDbgCtx.addDebugFlag( DebugContext.HOUR_STATISTICS );
        }
        if (XmlAux.getBooleanAttribute(tDbgElement,"takeOver", false)) {
            mDbgCtx.addDebugFlag( DebugContext.TAKE_OVER_FEED );
        }
        if (XmlAux.getBooleanAttribute(tDbgElement,"takeOverVerbose", false)) {
            mDbgCtx.addDebugFlag( DebugContext.TAKE_OVER_FEED_VERBOSE );
        }
        if (XmlAux.getBooleanAttribute(tDbgElement,"statistics", false)) {
            mDbgCtx.addDebugFlag( DebugContext.STATISTICS);
        }

        mExtraRegions = new ArrayList<>();
        Element tExtraRegionsElement = XmlAux.getElement(tRSElement, "ExtraRegions");
        if (tExtraRegionsElement != null) {
            NodeList tRegionList = tExtraRegionsElement.getElementsByTagName("ExtraRegion");
            if (tRegionList != null) {
                for (int i = 0; i < tRegionList.getLength(); i++) {
                    if (tRegionList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tExtraRegion = (Element) tRegionList.item(i);
                        int tId = XmlAux.getIntAttribute( tExtraRegion,"id", 0);
                        String tName = XmlAux.getStringAttribute( tExtraRegion,"name", null);
                        mExtraRegions.add( new CfgRegion( tName, tId ));
                    }
                }
            }
        }
    }

    public double getTpPphRelation() {
        return mTpPphRelation;
    }

    public DebugContext getDebugContext() {
        return mDbgCtx;
    }

    public List<CfgRegion> getExtraRegions() {
        return this.mExtraRegions;
    }

    public long getPeriodMS() { return this.mStatPeriod; }


    public static class ExtraRegion {
        int mId;
        String mName;

        public ExtraRegion( int pId, String pName) {
            mId = pId;
            mName = pName;
        }

        public boolean isExtraRegion( int pId, String pName) {
            if (pId == mId) {
                return true;
            }
            if ((mName != null) && (pName != null) && (pName.compareTo(mName) == 0)) {
                return true;
            }
            return false;
        }
    }

}
