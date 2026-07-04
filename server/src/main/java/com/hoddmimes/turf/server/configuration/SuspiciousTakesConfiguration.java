package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;


/**
 * This service will keep track of the latest zone take for any user. When a new zone take
 * appears the service validates the speed implied by the distance and time between the
 * user's previous take and the new take. Moves exceeding the configured max speed are
 * logged to a HTML file, provided that the elapsed time does not exceed maxTimeSec and
 * the distance is not shorter than minDistanceMeter.
 */
public class SuspiciousTakesConfiguration extends CoreConfiguration
{
    private double mMaxSpeedKmh;
    private long mMaxTimeSec;
    private double mMinDistanceMeter;
    private String mHtmlLogFile;
    private boolean mTrace;

    public SuspiciousTakesConfiguration() {
        super( true );
    }

    public void parse( Element pRootElement)
    {
        Element tCfg = XmlAux.getElement( pRootElement, "SuspiciousTakesService");
        super.enable( XmlAux.getBooleanAttribute( tCfg, "enabled", true));
        mMaxSpeedKmh = XmlAux.getDoubleAttribute( tCfg, "maxSpeedKmh", 35.0d);
        mMaxTimeSec = XmlAux.getLongAttribute( tCfg, "maxTimeSec", 3600L);
        mMinDistanceMeter = XmlAux.getDoubleAttribute( tCfg, "minDistanceMeter", 1000.0d);
        mHtmlLogFile = XmlAux.getStringAttribute( tCfg, "htmlLogFile", "suspicious-takes.html");
        mTrace = XmlAux.getBooleanAttribute( tCfg, "trace", false);
    }

    public double getMaxSpeedKmh() {
        return mMaxSpeedKmh;
    }

    public long getMaxTimeMs() {
        return mMaxTimeSec * 1000L;
    }

    public double getMinDistanceMeter() {
        return mMinDistanceMeter;
    }

    public String getHtmlLogFile() {
        return mHtmlLogFile;
    }

    public boolean isTraceEnabled() {
        return mTrace;
    }
}
