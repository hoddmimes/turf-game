package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


/**
 * This service will trace all sessions where the user have collected
 * high points per hour. The service will keep track of the best 'n' session discovered
 * The results are kept the DB and presented in the web interface
 */
public class TraceSessionsConfiguration extends CoreConfiguration
{
    private int mMinTakes;
    private int mSessesionTimeoutSec;

    private int mRankingSize;
    private List<CfgRegion> mRegions;


    public TraceSessionsConfiguration() {
        super( true );
        mRegions = new ArrayList<>();
    }
    public void parse( Element pRootElement)
    {
        Element tCfg = XmlAux.getElement( pRootElement, "TraceSessionsService");
        super.enable( XmlAux.getBooleanAttribute( tCfg, "enabled", true));
        mMinTakes = XmlAux.getIntAttribute( tCfg, "minTakes", 20);
        mRankingSize = XmlAux.getIntAttribute( tCfg, "rankingSize", 100);
        mSessesionTimeoutSec = XmlAux.getIntAttribute( tCfg, "sessionTimeoutSec", 720);


        Element tRegionElements = XmlAux.getElement(tCfg, "Regions");
        if (tRegionElements != null) {
            NodeList tRegionList = tRegionElements.getElementsByTagName("Region");
            for (int i = 0; i < tRegionList.getLength(); i++) {
                if (tRegionList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element tRegion = (Element) tRegionList.item(i);
                    int tId  = XmlAux.getIntAttribute( tRegion,"id", 0);
                    String tName  = XmlAux.getStringAttribute( tRegion,"name", null);
                    mRegions.add( new CfgRegion(  tName, tId ));
                }
            }
        }
    }

    public int getMinTakes() {
        return mMinTakes;
    }

    public int getSessionTimeoutSec() {
        return mSessesionTimeoutSec;
    }

    public int getRankingSize() {
        return mRankingSize;
    }

    public boolean isRegionOfInterest( int pRegionId ) {
        for( CfgRegion cr : this.mRegions ) {
            if (cr.getId() == pRegionId) {
                return true;
            }
        }
        return false;
    }

}
