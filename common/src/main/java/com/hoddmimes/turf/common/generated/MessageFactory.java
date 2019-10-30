
package com.hoddmimes.turf.common.generated;

import com.hoddmimes.jsontransform.*;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NameNotFoundException;

	

@SuppressWarnings({"WeakerAccess","unused","unchecked"})
public class MessageFactory implements MessageFactoryInterface
{
	public static Pattern JSON_MESSAGE_NAME_PATTERN = Pattern.compile("^\\s*\\{\\s*\"(\\w*)\"\\s*:\\s*\\{");


	public String getJsonMessageId( String pJString ) throws NameNotFoundException
	{
		Matcher tMatcher = JSON_MESSAGE_NAME_PATTERN.matcher(pJString);
		if (tMatcher.find()) {
		  return tMatcher.group(1);
		}
		throw new NameNotFoundException("Failed to extract message id from JSON message");
	}

	@Override
	public MessageInterface getMessageInstance(String pJsonMessageString) {
		String tMessageId = null;

		try { tMessageId = getJsonMessageId( pJsonMessageString ); }
		catch( NameNotFoundException e ) { return null; }
	
		switch( tMessageId ) 
		{

            case "TG_LogonRqst":
            {
            	TG_LogonRqst tMessage = new TG_LogonRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "TG_Response":
            {
            	TG_Response tMessage = new TG_Response();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_RegisterUserRqst":
            {
            	ZN_RegisterUserRqst tMessage = new ZN_RegisterUserRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_MailConfirmation":
            {
            	ZN_MailConfirmation tMessage = new ZN_MailConfirmation();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_ResetPasswordRqst":
            {
            	ZN_ResetPasswordRqst tMessage = new ZN_ResetPasswordRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_UpdatePasswordRqst":
            {
            	ZN_UpdatePasswordRqst tMessage = new ZN_UpdatePasswordRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_LoadZoneNamesRqst":
            {
            	ZN_LoadZoneNamesRqst tMessage = new ZN_LoadZoneNamesRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_ZoneNames":
            {
            	ZN_ZoneNames tMessage = new ZN_ZoneNames();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_LoadZoneNamesRsp":
            {
            	ZN_LoadZoneNamesRsp tMessage = new ZN_LoadZoneNamesRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_LoadZoneSubscriptionsRqst":
            {
            	ZN_LoadZoneSubscriptionsRqst tMessage = new ZN_LoadZoneSubscriptionsRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_LoadZoneSubscriptionsRsp":
            {
            	ZN_LoadZoneSubscriptionsRsp tMessage = new ZN_LoadZoneSubscriptionsRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_RemoveZoneSubscriptionRqst":
            {
            	ZN_RemoveZoneSubscriptionRqst tMessage = new ZN_RemoveZoneSubscriptionRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZN_AddZoneSubscriptionRqst":
            {
            	ZN_AddZoneSubscriptionRqst tMessage = new ZN_AddZoneSubscriptionRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "RS_RegionStatisticsRqst":
            {
            	RS_RegionStatisticsRqst tMessage = new RS_RegionStatisticsRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "RS_RegionStatisticsRsp":
            {
            	RS_RegionStatisticsRsp tMessage = new RS_RegionStatisticsRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            default:
              return null;
		}	
	}
}

