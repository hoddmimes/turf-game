package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneDensityConfiguration extends CoreConfiguration
{

    public ZoneDensityConfiguration() {
        super( true );
    }
    public void parse( Element pRootElement)
    {
        Element tCfg = XmlAux.getElement( pRootElement, "ZoneDensity");
        super.enable( XmlAux.getBooleanAttribute( tCfg, "enabled", true));
    }
}
