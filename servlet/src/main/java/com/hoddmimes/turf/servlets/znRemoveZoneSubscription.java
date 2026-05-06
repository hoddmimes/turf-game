package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/restricted/znRemoveZoneSubscription")
public class znRemoveZoneSubscription
{

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String removeSubscription(String pRqst) {
        String tResponse = null;

        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            tResponse = tResources.sendToTurfServer(pRqst);
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to remove subscription", e).toJson().toString();
        }
        return tResponse;
    }
}
