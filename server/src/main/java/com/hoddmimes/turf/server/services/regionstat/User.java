package com.hoddmimes.turf.server.services.regionstat;

import com.hoddmimes.turf.server.common.ZoneEvent;

public class User {
    int	 	    mId;
    String	    mName;
    ZoneEvent   mLastZone;

    User( int pId, String pName ) {
        mId = pId;
        mName = pName;
    }
}

