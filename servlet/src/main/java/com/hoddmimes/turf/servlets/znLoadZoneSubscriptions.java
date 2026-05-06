package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.authorize.Authenticator;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.ZN_LoadZoneSubscriptionsRqst;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/restricted/znLoadZoneSubscriptions")
public class znLoadZoneSubscriptions
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String loadZoneSubscription(@Context HttpServletRequest servletRequest, String pRqst) {
        String tResponse = null;
        ServletResourcesIf tResources = ServletResources.getInstance();

        try {
            HttpSession tSession = servletRequest.getSession( false );
            if ((tSession != null) && (tSession.getAttribute(Authenticator.TURF_USERNAME) != null)) {
                ZN_LoadZoneSubscriptionsRqst jLoadRqst = new ZN_LoadZoneSubscriptionsRqst(pRqst);
                jLoadRqst.setMailAddress(tSession.getAttribute(Authenticator.TURF_USERNAME).toString());

                tResponse = tResources.sendToTurfServer(jLoadRqst.toJson().toString());
            } else {
                tResponse = TGStatus.createError("Not logged in", null).toJson().toString();
            }
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to load zone subscriptions data", e).toJson().toString();
        }
        return tResponse;
    }
}
