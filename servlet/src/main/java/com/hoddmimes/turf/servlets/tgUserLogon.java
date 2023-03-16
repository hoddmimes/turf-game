package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.authorize.Authenticator;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.TG_LogonRqst;
import com.hoddmimes.turf.common.generated.TG_Response;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@Path("/tgUserLogon")
public class tgUserLogon
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String logonUser(@Context HttpServletRequest servletRequest, String pJsonRqstString) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            tResponse = tResources.sendToTurfServer(pJsonRqstString);
            TG_Response tTgRsp = new TG_Response( tResponse );
            if (tTgRsp.getSuccess().get()) {
                Authenticator.getInstance().authorize( new TG_LogonRqst( pJsonRqstString ), servletRequest);
            }
        }
        catch( Exception e) {
            tResponse = TGStatus.createError("Failed to logon user", e).toJson().toString();
        }
        return tResponse;
    }
}
