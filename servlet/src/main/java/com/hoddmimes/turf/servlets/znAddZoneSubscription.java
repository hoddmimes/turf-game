package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.authorize.Authenticator;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.ZN_AddZoneSubscriptionRqst;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/restricted/znAddZoneSubscription")
public class znAddZoneSubscription
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String addZone(@Context HttpServletRequest servletRequest, String pRqst) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            HttpSession tSession = servletRequest.getSession( false );
            if ((tSession != null) && (tSession.getAttribute(Authenticator.TURF_USERNAME) != null)) {
                ZN_AddZoneSubscriptionRqst tAddRqst = new ZN_AddZoneSubscriptionRqst(pRqst);
                tAddRqst.setMailAddress(tSession.getAttribute(Authenticator.TURF_USERNAME).toString());

                tResponse = tResources.sendToTurfServer(tAddRqst.toJson().toString());
            } else {
                tResponse = TGStatus.createError("Not logged in", null).toJson().toString();
            }
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to add zone subscription", e).toJson().toString();
        }
        return tResponse;
    }
}
