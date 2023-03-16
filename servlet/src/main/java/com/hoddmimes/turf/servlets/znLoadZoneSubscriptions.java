package com.hoddmimes.turf.servlets;

import com.hoddmimes.turf.authorize.Authenticator;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.ZN_LoadZoneSubscriptionsRqst;
import com.hoddmimes.turf.servlets.aux.ServletResources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
