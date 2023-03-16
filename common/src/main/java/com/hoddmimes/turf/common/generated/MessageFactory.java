

/*
* Copyright (c)  Hoddmimes Solution AB 2021.
*
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.hoddmimes.turf.common.generated;

import com.hoddmimes.jsontransform.*;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NameNotFoundException;

	
import com.hoddmimes.turf.common.generated.*;
	

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
			
            case "TG_WhenInitializedRqst":
            {
            	TG_WhenInitializedRqst tMessage = new TG_WhenInitializedRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "TG_WhenInitializedRsp":
            {
            	TG_WhenInitializedRsp tMessage = new TG_WhenInitializedRsp();
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
			
            case "DR_RegionRqst":
            {
            	DR_RegionRqst tMessage = new DR_RegionRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DR_RegionRsp":
            {
            	DR_RegionRsp tMessage = new DR_RegionRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DR_RankingRqst":
            {
            	DR_RankingRqst tMessage = new DR_RankingRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DR_RankingRsp":
            {
            	DR_RankingRsp tMessage = new DR_RankingRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "HM_ZoneRqst":
            {
            	HM_ZoneRqst tMessage = new HM_ZoneRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "HM_ZoneRsp":
            {
            	HM_ZoneRsp tMessage = new HM_ZoneRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_DensityRqst":
            {
            	ZD_DensityRqst tMessage = new ZD_DensityRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_DensityRsp":
            {
            	ZD_DensityRsp tMessage = new ZD_DensityRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_ZonesRqst":
            {
            	ZD_ZonesRqst tMessage = new ZD_ZonesRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_ZonesRsp":
            {
            	ZD_ZonesRsp tMessage = new ZD_ZonesRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspSaveRqst":
            {
            	ZD_TspSaveRqst tMessage = new ZD_TspSaveRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspSaveRsp":
            {
            	ZD_TspSaveRsp tMessage = new ZD_TspSaveRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspLoadZonesRqst":
            {
            	ZD_TspLoadZonesRqst tMessage = new ZD_TspLoadZonesRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspLoadZonesRsp":
            {
            	ZD_TspLoadZonesRsp tMessage = new ZD_TspLoadZonesRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspRqst":
            {
            	ZD_TspRqst tMessage = new ZD_TspRqst();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "ZD_TspRsp":
            {
            	ZD_TspRsp tMessage = new ZD_TspRsp();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            default:
              return null;
		}	
	}
}
		