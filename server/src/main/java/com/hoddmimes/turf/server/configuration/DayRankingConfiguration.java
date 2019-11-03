package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class DayRankingConfiguration
{

    private List<Region> mRegions;
    private long         mRefreshUserIntervalMS;
    private long         mSaveToDBIntervalMS;
    private int          mMaxUsers;
    private int          mDisplayUsers;
    private String       mDefaultRegion;
    private int          mDefaultRegionId;





    public void parse( Element pRoot ){
        Element tCfg = XmlAux.getElement( pRoot, "DayRankingService");
        mRefreshUserIntervalMS = XmlAux.getIntAttribute(tCfg,"refreshIntervalSec", 10 );
        mSaveToDBIntervalMS = XmlAux.getIntAttribute(tCfg,"saveIntervaSec", 10 );
        mMaxUsers = XmlAux.getIntAttribute(tCfg,"maxUsers", 300);
        mDisplayUsers = XmlAux.getIntAttribute(tCfg,"maxUsers", 50);

        mRefreshUserIntervalMS = mRefreshUserIntervalMS * 1000L;
        mSaveToDBIntervalMS = mSaveToDBIntervalMS * 1000L;

        Element tDefaultRegionElement = XmlAux.getElement( pRoot, "DefaultRegion");
        if (tDefaultRegionElement != null) {
            mDefaultRegion = XmlAux.getStringAttribute( tDefaultRegionElement,"name", null);
            mDefaultRegionId = XmlAux.getIntAttribute( tDefaultRegionElement,"id", 0);
        }


        mRegions = new ArrayList<>();
        NodeList tNodeList = tCfg.getElementsByTagName("Region");
        for( int i = 0; i < tNodeList.getLength(); i++) {
            if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element rRegionElement = (Element) tNodeList.item(i);
                int tId = Integer.parseInt( rRegionElement.getAttribute("id"));
                String tName = rRegionElement.getAttribute("name");
                mRegions.add( new Region( tName, tId ));
            }
        }
    }



    public long getRefreshUserIntervalMS() {
        return mRefreshUserIntervalMS;
    }
    public long getSaveIntervalMS() {
        return mSaveToDBIntervalMS;
    }
    public int getMaxUsers() { return mMaxUsers;}
    public int getMaxDisplayUsers() { return mDisplayUsers; }

    public String getDefaultRegionName() {
        return mDefaultRegion;
    }

    public int getDefaultRegionId() {
        return mDefaultRegionId;
    }

    public List<Region> getRegions() {
        return mRegions;
    }


    public static class Region
    {
        private String  mName;
        private int     mId;

        public Region( String pName, int pId )  {
            mId = pId;
            mName = pName;
        }

        public String getName() { return mName; }
        public int getId() { return mId; }

        boolean isSame( int pId, String pName ) {
            if ((pId == mId) && (pName.equalsIgnoreCase( pName ))) {
                return true;
            }
            return false;
        }
    }
}
