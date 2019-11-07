
package com.hoddmimes.turf.server.generated;

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

            case "HourRegionStat":
            {
            	HourRegionStat tMessage = new HourRegionStat();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "RegionStat":
            {
            	RegionStat tMessage = new RegionStat();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DayRankingInitUser":
            {
            	DayRankingInitUser tMessage = new DayRankingInitUser();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DayRankingUser":
            {
            	DayRankingUser tMessage = new DayRankingUser();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "DayRankingRegion":
            {
            	DayRankingRegion tMessage = new DayRankingRegion();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "User":
            {
            	User tMessage = new User();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "Subscription":
            {
            	Subscription tMessage = new Subscription();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            case "FirstEntry":
            {
            	FirstEntry tMessage = new FirstEntry();
            	tMessage.decode( new JsonDecoder(pJsonMessageString));
            	return tMessage;
            }
			
            default:
              return null;
		}	
	}
}

