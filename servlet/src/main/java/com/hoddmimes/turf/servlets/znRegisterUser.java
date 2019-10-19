package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.ZN_RegisterUserRqst;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/znRegisterUser")
public class znRegisterUser
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
            tResponse = TGStatus.createError("Failed to register user", e).toJson().toString();
        }
        return tResponse;
    }
}
