package com.hoddmimes.turf.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.TG_Response;
import com.hoddmimes.turf.common.generated.ZN_MailConfirmation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;


@Path("/znMailConfirmation")
public class znMailConfirmation
{
    @GET
    public String  mailConfirmation(@QueryParam("id") String pConfirmationId, @QueryParam("addr") String pMailAddr) {
        ServletResourcesIf tResources = ServletResources.getInstance();
        String tResponse = "Uninitialized";
        JsonParser tParser = new JsonParser();

        if ((pConfirmationId == null) || (pMailAddr == null)) {
            return "Error: Required confirmation parameters \"id\" and \"addr\" are not present.";
        }

        ZN_MailConfirmation tMailConfRqst = new ZN_MailConfirmation();
        tMailConfRqst.setMailAddress( pMailAddr );
        tMailConfRqst.setConfirmationId( pConfirmationId );

        try {
            tResponse = tResources.sendToTurfServer(tMailConfRqst.toJson().toString());
            TG_Response tTgRsp = new TG_Response( tResponse );
            return tTgRsp.getReason().get();
        }
        catch( IOException e) {
            tResponse = TGStatus.createError("Failed to register user", e).getReason().get();
        }
        return tResponse;
    }
}
