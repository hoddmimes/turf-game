package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/restricted/znLoadZoneNames")
public class znLoadZoneNames
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
            tResponse = TGStatus.createError("Failed to load zone names data", e).toJson().toString();
        }
        System.out.println( tResponse );
        return tResponse;
    }
}
