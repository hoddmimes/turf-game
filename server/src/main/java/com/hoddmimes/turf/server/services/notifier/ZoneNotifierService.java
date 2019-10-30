package com.hoddmimes.turf.server.services.notifier;

import com.google.gson.JsonElement;
import com.hoddmimes.jsontransform.MessageInterface;
import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.*;
import com.hoddmimes.turf.server.TurfServerInterface;
import com.hoddmimes.turf.server.TurfServiceInterface;
import com.hoddmimes.turf.server.common.*;
import com.hoddmimes.turf.server.configuration.PasswordRules;
import com.hoddmimes.turf.server.configuration.ZoneNotifyConfiguration;
import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.Subscription;
import com.hoddmimes.turf.server.generated.User;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ZoneNotifierService implements TurfServiceInterface
{
    private static SimpleDateFormat SUBSCR_TIME = new SimpleDateFormat("dd/MM HH:ss:ss");

    private TurfServerInterface mTurfIf;
    private Logger mLogger;
    private EventFilterNewZoneTakeOver tZoneFilter = null;
    private Mailer mMailer;
    private MongoAux mDbAux;

    public ZoneNotifierService( ) {
        mLogger = LogManager.getLogger( this.getClass().getSimpleName());
    }

    @Override
    public String execute( MessageInterface tRqstMsg ) {
        if (tRqstMsg instanceof ZN_RegisterUserRqst) {
            return executeRegisterUser((ZN_RegisterUserRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_LoadZoneNamesRqst) {
            return executeLoadZoneName((ZN_LoadZoneNamesRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_MailConfirmation) {
            return executeMailConfirmation((ZN_MailConfirmation) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_RemoveZoneSubscriptionRqst) {
            return executeRemoveSubscription((ZN_RemoveZoneSubscriptionRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_ResetPasswordRqst) {
            return executeResetPassword((ZN_ResetPasswordRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_AddZoneSubscriptionRqst) {
            return executeaddZoneSubscription((ZN_AddZoneSubscriptionRqst) tRqstMsg);
        }
        if (tRqstMsg instanceof ZN_LoadZoneSubscriptionsRqst) {
            return executeLoadZoneSubscriptions((ZN_LoadZoneSubscriptionsRqst) tRqstMsg);
        }

        if (tRqstMsg instanceof ZN_UpdatePasswordRqst) {
            return executeUpdatePassword((ZN_UpdatePasswordRqst) tRqstMsg);
        }

        return TGStatus.createError("No " + this.getClass().getSimpleName() + " service method found for request \"" +
                    tRqstMsg.getMessageName() + "\"", null ).toJson().toString();

    }

    @Override
    public void initialize( TurfServerInterface pTurfServerInterface ) {
        mTurfIf = pTurfServerInterface;
        tZoneFilter = new EventFilterNewZoneTakeOver();

        // Connect to Database
        ZoneNotifyConfiguration tCfg = pTurfServerInterface.getServerConfiguration().getZoneNotifyConfiguration();

        // Get database handle
        mDbAux = pTurfServerInterface.getDbAux();

        // Initialize mailer
        mMailer =   new  Mailer(tCfg.getMailerHost(),
                587,
                tCfg.getMailerUser(),
                tCfg.getMailerPassword(),
                "text/html", true);
    }

    private boolean validMailAddress( String pMailAddress ) {
        String tMailPatternString = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern tMailPattern = Pattern.compile( tMailPatternString );
        Matcher m = tMailPattern.matcher( pMailAddress );
        return m.matches();
    }

    private String createUpdatePasswordBody( User pUser ) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><br><br>");
        sb.append("<h3>Turf zone taken notification,  user password update</h3><br>");
        sb.append("Reset user password for mail address\"" + pUser.getMailAddr().get() + "\" by clicking on the reset link below");
        sb.append("<br><br>");
        sb.append("<p style=\"margin-left:30px;\">");
        sb.append("<a  href=\"" + mTurfIf.getServerConfiguration().getBaseURL() + "turf/updatePassword.html?id=" + pUser.getConfirmationId().get() +
                "&addr=" + pUser.getMailAddr().get()+ "\">");
        sb.append("Update Password</a>");
        sb.append("</p></body></html>");
        return sb.toString();
    }

    private String createRegisterUserBody( User pUser ) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><br><br>");
        sb.append("<h3>Turf zone taken notification mail address registered</h3><br>");
        sb.append("If you have not registered your mail address \"" + pUser.getMailAddr().get() + "\" or is not aware of the registration you should delete this mail.");
        sb.append("<br>");
        sb.append("Otherwise please confirm your mail address by <i>clicking</i> on the link below within 24 hours.");
        sb.append("<br><br>");
        sb.append("<p style=\"margin-left:30px;\">");
        sb.append("<a  href=\"" + mTurfIf.getServerConfiguration().getBaseURL() + "turf/app/znMailConfirmation?id=" + pUser.getConfirmationId().get() +
                    "&addr=" + pUser.getMailAddr().get()+ "\">");
        sb.append("Confirm mail adress</a>");
        sb.append("</p></body></html>");
        return sb.toString();
    }

    private boolean sendUpdatePasswordMail( User pUser ) {
        String tBody = createUpdatePasswordBody(pUser);
        try {
            mMailer.sendMessage(true, "no-reply@turf-zone-taken.com", pUser.getMailAddr().get(), null,
                    "Turf Zone Notification (update password)", tBody);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private boolean sendConfirmationMail( User pUser ) {
        String tBody = createRegisterUserBody(pUser);
        try {
            mMailer.sendMessage(true, "no-reply@turf-zone-taken.com", pUser.getMailAddr().get(), null,
                    "Turf Zone Notification (mail confirmation)", tBody);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private String executeLoadZoneName( ZN_LoadZoneNamesRqst pRqstMsg ) {
        String tResponseString = null;
        String jResponseString = null;

        ZN_LoadZoneNamesRsp jZoneNameRsp = new ZN_LoadZoneNamesRsp();
        Map<String, List<Zone>> tRegionMap = mTurfIf.getZonesByRegionNames();

        List<String> tRegionNames = tRegionMap.keySet().stream().collect(Collectors.toList());
        jZoneNameRsp.setRegionNames( tRegionNames );

        for( String tKey : tRegionMap.keySet()) {
            ZN_ZoneNames zns = new ZN_ZoneNames();
            zns.setRegion( tKey );
            List<String> tNames = tRegionMap.get( tKey ).stream().map(z->z.getName()).collect(Collectors.toList());
            zns.setNames( tNames );
            jZoneNameRsp.addRegions( zns );
        }

        return jZoneNameRsp.toJson().toString();
    }

    private String executeLoadZoneSubscriptions( ZN_LoadZoneSubscriptionsRqst pRqst ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ZN_LoadZoneSubscriptionsRsp tResponse = new ZN_LoadZoneSubscriptionsRsp();

        List<Subscription> tSubscrList =  mDbAux.findSubscriptionByMailAddr( pRqst.getMailAddress().get());
        for ( Subscription s: tSubscrList ) {
            ZN_SubscriptionData tZoneData = new ZN_SubscriptionData();
            tZoneData.setNotifications( s.getNotifications().get());
            tZoneData.setSubscrId( s.getMongoId() );
            tZoneData.setSubscrTime( s.getCreateTime().get());
            tZoneData.setNotificationTime( s.getNotificationTime().get());
            tZoneData.setZoneName( s.getZoneName().get());
            tResponse.addSubscriptions( tZoneData );
        }

        return tResponse.toJson().toString();
    }

    private String executeRemoveSubscription( ZN_RemoveZoneSubscriptionRqst pRqstMsg ) {
        Subscription tSubscr = mDbAux.findSubscriptionByMongoId( pRqstMsg.getSubscrId().get() );
        if (tSubscr == null) {
            return TGStatus.createError("Subscription id: \"" + pRqstMsg.getSubscrId().get() + "\" is not found", null).toJson().toString();
        }
        try {
            if (mDbAux.deleteSubscriptionByMongoId( pRqstMsg.getSubscrId().get()) > 0) {
                return TGStatus.create(true, "Subscription id: \"" + pRqstMsg.getSubscrId().get() + "\" successfully removed").toJson().toString();
            } else {
                return TGStatus.createError("Subscription id: \"" + pRqstMsg.getSubscrId().get() + "\" is not found", null).toJson().toString();
            }
        }
        catch( Exception e ) {
            return TGStatus.createError("Failed to remove subscription id: \"" + pRqstMsg.getSubscrId().get() + "\"", e).toJson().toString();
        }
    }

    private String executeaddZoneSubscription( ZN_AddZoneSubscriptionRqst pAddZoneSubscr ) {
        try {
            Subscription tSubscr = new Subscription();
            tSubscr.setMailAddr( pAddZoneSubscr.getMailAddress().get());
            Zone tZone = this.mTurfIf.getZoneByName( pAddZoneSubscr.getZone().get());
            if (tZone == null) {
                return TGStatus.createError("Zone \"" + pAddZoneSubscr.getZone().get() + "\" is not found", null).toJson().toString();
            }
            tSubscr.setZoneName( pAddZoneSubscr.getZone().get());
            tSubscr.setZoneId( tZone.getId());
            tSubscr.setCreateTime( SUBSCR_TIME.format( System.currentTimeMillis()));
            tSubscr.setNotificationTime("00:00:00");
            tSubscr.setNotifications(0);

            mDbAux.insertSubscription(tSubscr);
            return TGStatus.createSuccessResponse().toJson().toString();
        }
        catch( Exception e ) {
            return TGStatus.createError("Failed to add zone subscription", e).toJson().toString();
        }
    }

    private String executeMailConfirmation(ZN_MailConfirmation pRqstMsg) {
        if (!validMailAddress( pRqstMsg.getMailAddress().get() )) {
            return TGStatus.createError("Invalid mail address", null).toJson().toString();
        }

        List<User> tUsers = mDbAux.findUser( pRqstMsg.getMailAddress().get());
        if ((tUsers == null) || (tUsers.size() == 0)) {
            return TGStatus.createError("Mail address does not exist", null).toJson().toString();
        }

        User tUser = tUsers.get(0);
        if (tUser.getConfirmationId().get().compareTo( pRqstMsg.getConfirmationId().get()) != 0) {
            return TGStatus.create(false,"Confirmation id did not match").toJson().toString();
        }

        if (tUser.getConfirmed().get()) {
            return TGStatus.create(true,"User mail address already confirmed","/turf/znlogon.html").toJson().toString();
        }

        tUser.setConfirmed( true );
        mLogger.info("Mail address " + pRqstMsg.getMailAddress().get() + " is now confirmed");

        UpdateResult tUpdResult = mDbAux.updateUser( pRqstMsg.getMailAddress().get(), tUser, false);
        if (tUpdResult.getModifiedCount() == 1) {
            return TGStatus.create(true,"User mail address successfully confirmed","/turf/znLogon.html").toJson().toString();
        }

        return TGStatus.create(false,"User confirmation was not done (modify count == " + tUpdResult.getModifiedCount() + ")").toJson().toString();
    }

    private String executeUpdatePassword(ZN_UpdatePasswordRqst pRqstMsg) {
        if (!validMailAddress( pRqstMsg.getMailAddress().get() )) {
            return TGStatus.createError("Invalid mail address", null).toJson().toString();
        }

        List<User> tUsers = mDbAux.findUser( pRqstMsg.getMailAddress().get());
        if ((tUsers == null) || (tUsers.size() == 0)) {
            return TGStatus.createError("Mail address does not exist", null).toJson().toString();
        }

        User tUser = tUsers.get(0);
        if (tUser.getConfirmationId().get().compareTo( pRqstMsg.getConfirmationId().get()) != 0) {
            return TGStatus.create(false,"Confirmation id did not match").toJson().toString();
        }
        tUser.setPassword( PasswordRules.hashPassword( pRqstMsg.getPassword().get()));


        mLogger.info("User " + pRqstMsg.getMailAddress().get() + ", password is now updated");

        UpdateResult tUpdResult = mDbAux.updateUser( pRqstMsg.getMailAddress().get(), tUser, false);
        if (tUpdResult.getModifiedCount() == 1) {
            return TGStatus.create(true,"User \"" + pRqstMsg.getMailAddress().get() + "\" is successfully updated","/turf/znLogon.html").toJson().toString();
        }

        return TGStatus.create(false,"Failed to update user password (mod count = " + tUpdResult.getModifiedCount() + ")").toJson().toString();
    }

    private String executeResetPassword( ZN_ResetPasswordRqst pRqstMsg ) {
        if (!validMailAddress(pRqstMsg.getMailAddress().get())) {
            return TGStatus.createError("Invalid mail address", null).toJson().toString();
        }

        List<User> tUsers = mDbAux.findUser(pRqstMsg.getMailAddress().get());
        if ((tUsers != null) && (tUsers.size() == 0)) {
            return TGStatus.createError("mail address does not exist", null).toJson().toString();
        }

        User tUser = tUsers.get(0);
        if (!tUser.getConfirmed().get()) {
            return TGStatus.createError("mail address not yet confirmed", null).toJson().toString();
        }

        String tConfirmId = UUID.randomUUID().toString();
        tUser.setConfirmationId(tConfirmId);

        if (sendUpdatePasswordMail(tUser)) {
            try {
                mDbAux.updateUser(tUser.getMailAddr().get(), tUser, false);
            } catch (Exception e) {
                mLogger.error("Failed to update user " + tUser.getMailAddr().get() + " in DB", e);
                return TGStatus.createError("failed to update user in DB", e).toJson().toString();
            }
            mLogger.info("User " + tUser.getMailAddr().get() + " reset password  confirm id updated");
            return TGStatus.create(true, "Successfully completed, update password link mailed", null).toJson().toString();
        } else {
            return TGStatus.create(false, "Filed to send update password link mailed", null).toJson().toString();
        }
    }

    private String executeRegisterUser( ZN_RegisterUserRqst pRqstMsg ) {

        if (!validMailAddress( pRqstMsg.getMailAddress().get() )) {
            return TGStatus.createError("Invalid mail address", null).toJson().toString();
        }

        PasswordRules tPwdRules = mTurfIf.getServerConfiguration().getZoneNotifyConfiguration().getPasswordRules();
        try {
            tPwdRules.validatePassword( pRqstMsg.getPassword().get());
        }
        catch( Exception e) {
            return TGStatus.createError("Invalid password", e).toJson().toString();
        }


        List<User> tUsers = mDbAux.findUser( pRqstMsg.getMailAddress().get());
        if ((tUsers != null) && (tUsers.size() > 0)) {
            return TGStatus.createError("mail address already registered/used", null).toJson().toString();
        }

        String tConfirmId = UUID.randomUUID().toString();

        User tUser = new User();
        tUser.setMailAddr( pRqstMsg.getMailAddress().get() );
        tUser.setPassword( tPwdRules.hashPassword( pRqstMsg.getPassword().get()));
        tUser.setLoginCounts(0);
        tUser.setLastLogin( Turf.SDF.format( System.currentTimeMillis()));
        tUser.setConfirmed( false );
        tUser.setConfirmationId(tConfirmId);

        if (sendConfirmationMail( tUser )) {
            try {
                mDbAux.insertUser(tUser);
            } catch (Exception e) {
                mLogger.error("Failed to register user " + tUser.getMailAddr().get() + " in DB", e);
                return TGStatus.createError("failed to register user in DB", e).toJson().toString();
            }
            mLogger.info("User " + tUser.getMailAddr().get() + " successfully registered");
            return TGStatus.create( true,"Successfully register user","/turf/znLogon.html").toJson().toString();
        } else {
            mLogger.error("Failed to register user (sending confirmation mail)");
            return TGStatus.createError("failed to register user (confirmation mail error)", null).toJson().toString();
        }

    }



    @Override
    public void processZoneUpdates( JsonElement pZoneUpdates ) {
        List<ZoneEvent> tZones = tZoneFilter.getNewTakeover( pZoneUpdates.getAsJsonArray() );

        for( ZoneEvent ze: tZones ) {
            List<Subscription> tSubscriptions = mDbAux.findSubscriptionByZoneId( ze.getZoneId() );
            for( Subscription sub : tSubscriptions ) {
                notifyUser( sub, ze );
            }
        }
    }



    private String createBody( ZoneEvent pZonEvt ) {
        String tTimStr = Turf.SDF.format(pZonEvt.getLatestTakeOverTime());
        StringBuilder sb = new StringBuilder();
        sb.append("<html><br><br><br>");
        sb.append("<p align=\"center\" style=\"font-family:verdana;font-size:100%;\">");
        sb.append("Zone \"" + pZonEvt.getZoneName() + "\" taken.<br>" );
        sb.append("Take over at " + tTimStr + "<br>");
        sb.append("Current owner\"" + pZonEvt.getCurrentOwner() +"\" previous owner \"" + pZonEvt.getPreviousOwner() + "\" <br>");
        sb.append("</p></html>");
        return sb.toString();
    }

    public void notifyUser( Subscription pSubscr, ZoneEvent pZonEvt )
    {
        String tBody = createBody( pZonEvt );
        String tHeader = "Zone \"" + pZonEvt.getZoneName() + "\" taken";

        mLogger.info("Notificat mail sent to " + pSubscr.getMailAddr().get() + " zone take \"" + pZonEvt +
                "\" take-time: " + Turf.SDFDate.format( pZonEvt.getLatestTakeOverTime()));

        pSubscr.setNotificationTime( SUBSCR_TIME.format( System.currentTimeMillis()));
        pSubscr.setNotifications( pSubscr.getNotifications().get() + 1);
        mDbAux.updateSubscriptionByMongoId( pSubscr.getMongoId(), pSubscr);

        mMailer.sendMessage( true,
                "turf-noreply@hoddmimes.com",
                pSubscr.getMailAddr().get(),
                null,
                tHeader, tBody);
    }
}
