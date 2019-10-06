package com.hoddmimes.turf.server.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Zone events are clllected periodically by doing pools to the turf server.
 * The response returned contains the take been taken for the last 'n' minutes, max
 * 15 min. There is no esy way of seeing what of the events that has been been new and being already
 * processed in an earlier response, pretty much since the granularity is seconds.
 *
 * This class somewhat tries to keep track on what zone takes that has been processed and which has not.
 * The method newEvent will take a get-feed-zone-ecvents and return a list with the events being new
 */
public class EventFilterNewZoneTakeOver {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");
	private EndMarker mEndMarker;

	public EventFilterNewZoneTakeOver() {
		mEndMarker  = null;
	}

	public List<ZoneEvent> getNewTakeover( JsonArray pTakeOverArray) {
		ArrayList<ZoneEvent> tList = new ArrayList<>();
		boolean tLatestTakeOverFound = (mEndMarker == null) ? true : false;

		for( int i= pTakeOverArray.size() - 1; i >= 0; i--) {
			JsonObject jObject = pTakeOverArray.get(i).getAsJsonObject();
			if (jObject.get("type").getAsString().compareTo("takeover") == 0) {
				ZoneEvent ze = new ZoneEvent(jObject);

				if (tLatestTakeOverFound) {
					tList.add(ze);
				} else {
					if (mEndMarker.equalTakeOverZone(ze)) {
						tLatestTakeOverFound = true;
					}
				}
			}
		}


		// Locate the latest take over event
		for( int i = 0; i < pTakeOverArray.size(); i++) {
			JsonObject jObject = pTakeOverArray.get(i).getAsJsonObject();
			if (jObject.get("type").getAsString().compareTo("takeover") == 0) {
				mEndMarker = new EndMarker( new ZoneEvent( jObject ));
				break;
			}
		}

		// return filter list
		return tList;
	}


	private class EndMarker {
		private long mLatestTakeOverTime;
		private int mZoneId;


		public EndMarker(ZoneEvent pZoneEvent) {
			mLatestTakeOverTime = pZoneEvent.getLatestTakeOverTime();
			mZoneId = pZoneEvent.getZoneId();
		}


		public String toString() {
			return "z: " + mZoneId + " tot: " + SDF.format(mLatestTakeOverTime);
		}


		public boolean equalTakeOverZone(ZoneEvent ze) {
			if ((ze.getZoneId() == mZoneId) && (ze.getLatestTakeOverTime() == mLatestTakeOverTime)) {
				return true;
			}
			return false;
		}
	}


}
