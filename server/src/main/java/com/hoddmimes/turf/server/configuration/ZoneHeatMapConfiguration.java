package com.hoddmimes.turf.server.configuration;

import com.hoddmimes.turf.server.services.regionstat.DebugContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneHeatMapConfiguration
{
    private List<ColorMap>      mColorMap;
    private List<Region>        mRegions;

    public void parse( Element pRootElement) {
        mRegions = new ArrayList<>();
        mColorMap = new ArrayList<>();


        Element tZoneMapElement = XmlAux.getElement(pRootElement, "ZoneHeatMap");

        Element tTakeIntervals = XmlAux.getElement(tZoneMapElement, "TakeIntervals");
        if (tTakeIntervals != null) {
            NodeList tIntervalList = tTakeIntervals.getElementsByTagName("Interval");
            if (tIntervalList != null) {
                for (int i = 0; i < tIntervalList.getLength(); i++) {
                    if (tIntervalList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tIntervalElement = (Element) tIntervalList.item(i);
                        if (!XmlAux.isAttributePresent( tIntervalElement,"lessThan")) {
                            throw new RuntimeException("Failed to load ZoneHeatMap, interval is missing \"lessThan\" attribute");
                        }
                        if (!XmlAux.isAttributePresent( tIntervalElement,"color")) {
                            throw new RuntimeException("Failed to load ZoneHeatMap, interval is missing \"color\" attribute");
                        }
                        int tLessThan = XmlAux.getIntAttribute( tIntervalElement,"lessThan", 0);
                        String tHexValueString = XmlAux.getStringAttribute( tIntervalElement,"color", null);
                        mColorMap.add( new ColorMap(tLessThan, tHexValueString));
                    }
                }
            }
            Element tRegionElement = XmlAux.getElement(tZoneMapElement,"Regions");
            if (tRegionElement != null) {
                NodeList tRegionList = tRegionElement.getElementsByTagName("Region");
                for (int i = 0; i < tRegionList.getLength(); i++) {
                    if (tRegionList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tRegion = (Element) tRegionList.item(i);
                        int tId  = XmlAux.getIntAttribute( tRegion,"id", 0);
                        String tName  = XmlAux.getStringAttribute( tRegion,"name", null);
                        mRegions.add( new Region( tId, tName ));
                    }
                }
            }
        }
    }




    public static class Region {
        int mId;
        String mName;

        public Region( int pId, String pName) {
            mId = pId;
            mName = pName;
        }

        public boolean isRegion( int pId, String pName) {
            if (pId == mId) {
                return true;
            }
            if ((mName != null) && (pName != null) && (pName.compareTo(mName) == 0)) {
                return true;
            }
            return false;
        }
    }

    public List<Integer> getRegions() {
        return mRegions.stream().map( r -> r.mId).collect(Collectors.toList());
    }

    public List<ColorMap> getColorMaps() {
        List<ColorMap> tList = new ArrayList<>( mColorMap );
        tList.add( new ColorMap( Integer.MAX_VALUE, "#000000" ));
        tList.sort(Comparator.comparing(o -> o.mMinutes ));
        return tList;
    }

    public static class ColorMap {
        int mMinutes;
        String mColorValue; // e.g "#7e6454"

        public ColorMap( int pMinutes, String pColorValue) {
            mMinutes = pMinutes;
            mColorValue = pColorValue;
        }

        public long getUpperTakeOverTimeSec() {
            return (long) (60L * mMinutes);
        }

        public String getColorValue() {
            return mColorValue;
        }
    }
}
