package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/znResetPassword")
public class znResetPassword
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String resetPassword(String pRqst) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            tResponse = tResources.sendToTurfServer(pRqst);
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to reset password", e).toJson().toString();
        }
        return tResponse;
    }
}
