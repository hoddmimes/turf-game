package com.hoddmimes.turf.server.configuration;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UserTraceConfiguration
{
    private String mLogfilename;
    private List<String> mUsers;
    private boolean      mTraceAll;
    private long         mSessionMaxInactivityMin;

    public void parse( Element pRoot ){
        Element tCfg = XmlAux.getElement( pRoot, "UserTraceService");
        mLogfilename = tCfg.getAttribute("logfile");
        mSessionMaxInactivityMin = XmlAux.getIntAttribute( tCfg,"sessionMaxInactivityMin", 30 );
        mTraceAll = XmlAux.getBooleanAttribute( tCfg,"traceAll", false );

        mUsers = new ArrayList<>();
        NodeList tNodeList = tCfg.getElementsByTagName("User");
        for( int i = 0; i < tNodeList.getLength(); i++) {
            if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element tUsrElm = (Element) tNodeList.item(i);
                mUsers.add( tUsrElm.getAttribute("name"));
            }
        }
    }





    public boolean traceAll() {
        return mTraceAll;
    }

    public List<String> getUsers() {
        return mUsers;
    }

    public String getLogfilename() {
        return mLogfilename;
    }

    public long getSessionInactivityMin() {
        return mSessionMaxInactivityMin;
    }

}
