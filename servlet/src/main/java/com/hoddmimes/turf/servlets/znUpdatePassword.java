package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/znUpdatePassword")
public class znUpdatePassword
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String registerUser(String pRqst) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            tResponse = tResources.sendToTurfServer(pRqst);
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to update user password", e).toJson().toString();
        }
        return tResponse;
    }
}
