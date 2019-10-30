package com.hoddmimes.turf.server;

import com.google.gson.JsonElement;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.server.common.ZoneEvent;

import java.util.List;

public interface TurfServiceInterface
{
    public void initialize( TurfServerInterface pTurfServerInterface );
    public void processZoneUpdates( JsonElement pZoneUpdates );
    public String execute( MessageInterface tRqstMsg );

}
