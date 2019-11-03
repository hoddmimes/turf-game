package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/drLoadRegions")
public class drLoadRegions
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String loadZoneNames(String pRqst) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            tResponse = tResources.sendToTurfServer(pRqst);
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to load day ranking regions", e).toJson().toString();
        }
        System.out.println( tResponse );
        return tResponse;
    }
}
