package com.hoddmimes.turf.server.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;


import org.apache.coyote.http2.Http2Exception;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Turf
{
	public static final String URL_PATH = "http://api.turfgame.com/v4/";
	
	private static long DEFAULT_ERROR_WAIT_MS = 15000L;
	private static long PACER_DELAY = 3000L;

	
	public static final SimpleDateFormat TurfSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat SDFSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat SDFSimpleDateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDFDate = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDFSimpleTime = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat SDFTime = new SimpleDateFormat("HH:mm:ss.SSS");
	public static final SimpleDateFormat SDFHHSS = new SimpleDateFormat("HH:mm");

	private static Turf cInstance = null;

	private long mLastRequestTime = 0L;

	synchronized static public Turf getInstance() {
		if (cInstance == null) {
			cInstance = new Turf();
		}
		return cInstance;
	}

	private void pace() {
		long tNow = System.currentTimeMillis();
		if (tNow < (mLastRequestTime + PACER_DELAY)) {
			long tWait = (mLastRequestTime + PACER_DELAY) - tNow;
			try { Thread.sleep( tWait );}
			catch( InterruptedException ie) {}
		}
		mLastRequestTime = System.currentTimeMillis();
	}


	public static String formatTimeDiff( long pMs ) {
		if (pMs == 0) {
			return "00:00";
		}
		int tDiffSec = (int) (pMs / 1000L);
		int tMin = (int) ((double) tDiffSec / (double) 60);
		int tSec = tDiffSec - (tMin * 60);
		
		int tHrs = (int) ((double) tMin / (double) 60);
		tMin = tMin - (tHrs * 60);
		
		String s = null;
		if (tHrs == 0) {
			s = String.format("%02d:%02d", tMin,tSec);
		} else if (tHrs < 100) {
			s = String.format("%02d:%02d:%02d", tHrs,tMin,tSec);
		} else {
			s = String.format("%4d:%02d:%02d", tHrs,tMin,tSec);
		}
		return s;
	}
	
	public static long parseTurfTime( String pTimeString ) 
	{
	   Date d = null;
	   try { d = TurfSDF.parse(pTimeString); }
	   catch( Exception e ) { e.printStackTrace(); }
	   return d.getTime();
	}
	
	public static  boolean attributePresent( Element pElement, String pAttributeName ) {
		if ((pElement.getAttribute(pAttributeName) != null) && (pElement.getAttribute(pAttributeName).length() > 0)) {
			return true;
		}
		return false;
	}
	
	public static String toXmlAttribute( String pAttributename, long pValue ) {
		return " " + pAttributename + "=\"" + String.valueOf(pValue) + "\"";
	}
	
	public static String toXmlAttribute( String pAttributename, int pValue ) {
		return " " + pAttributename + "=\"" + String.valueOf(pValue) + "\"";
	}
	
	public static String toXmlAttribute( String pAttributename, String pValue ) {
		return " " + pAttributename + "=\"" + pValue + "\"";
	}
	



	public synchronized  JsonElement turfServerGET( String pPath, boolean pRecovery, Logger pLogger )
	{
		JsonElement tElement = null;
		boolean tGotAnswer = false;

		pace();

		while (!tGotAnswer) {
			try {
				HttpURLConnection tConn = (HttpURLConnection) new URL(URL_PATH + pPath).openConnection();
				tConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				tConn.setRequestMethod("GET");
				tConn.setRequestProperty("Content-Type", "application/json;charset=iso-8859-1");
				tConn.setDoOutput(false);
				tConn.setDoInput(true);
				tConn.setUseCaches (false);


				// Check status code
				if (tConn.getResponseCode() != 200) {
					pLogger.error("[turfServerGET] path: " + pPath + " error response code: " + tConn.getResponseCode());
					return null;
				}


				InputStream tInStream = tConn.getInputStream();
				if ((tConn.getContentEncoding() != null) && (tConn.getContentEncoding().toLowerCase().contains("gzip"))) {
					tInStream = new GZIPInputStream( tConn.getInputStream());
				}

				BufferedReader tReader = new BufferedReader(new InputStreamReader(tInStream, "utf-8"), 8);
				tElement  =   JsonParser.parseReader(tReader);
				tGotAnswer = true;
			} catch (MalformedURLException e) {
                e.printStackTrace();
                if (!pRecovery) {
                    return null;
                } else {
                    try {
                        Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS);
                    } catch (InterruptedException ie) {
                    }
                }
            } catch (IOException e) {
				System.out.println( e.getMessage());
				if (!pRecovery) {
					return null;
				} else {
					try { Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS); }
					catch( InterruptedException ie) {}
				}
			}
		}
		return tElement;
	}


	public  synchronized JsonElement turfServerGETSignal(String pPath, Logger pLogger) throws HttpException {
		JsonElement tElement = null;


		pace();

		try {
			HttpURLConnection tConn = (HttpURLConnection) new URL(URL_PATH + pPath).openConnection();
			tConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			tConn.setRequestMethod("GET");
			tConn.setRequestProperty("Content-Type", "application/json;charset=iso-8859-1");
			tConn.setDoOutput(false);
			tConn.setDoInput(true);
			tConn.setUseCaches(false);


			// Check status code
			if (tConn.getResponseCode() != 200) {
				pLogger.warn("[turfServerGETSignal] error response code: " + tConn.getResponseCode());
				throw new HttpException(null, tConn.getResponseCode());
			}


			InputStream tInStream = tConn.getInputStream();
			if ((tConn.getContentEncoding() != null) && (tConn.getContentEncoding().toLowerCase().contains("gzip"))) {
				tInStream = new GZIPInputStream(tConn.getInputStream());
			}

			BufferedReader tReader = new BufferedReader(new InputStreamReader(tInStream, "utf-8"), 8);
			tElement = JsonParser.parseReader( tReader );
		} catch (IOException e) {
			throw new HttpException(e, 0);
		}
		return tElement;
	}



	public   synchronized JsonElement turfServerPOST( String pPath, String pRequestData, Logger pLogger ) {
		return turfServerPOST(pPath, pRequestData, true, pLogger);
	}
	

	public  JsonElement turfServerPOST( String pPath, String pRequestData, boolean pRecovery, Logger pLogger )
	{
		JsonElement tElement = null;
		boolean tGotAnswer = false;

		pace();

		while (!tGotAnswer) {
			try {
				HttpURLConnection tConn = (HttpURLConnection) new URL(URL_PATH + pPath).openConnection();
				tConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				tConn.setRequestMethod("POST");
				tConn.setRequestProperty("Content-Type", "application/json;charset=iso-8859-1");
				tConn.setDoOutput(true);
				tConn.setDoInput(true);
				tConn.setRequestProperty("Content-Length", "" + Integer.toString(pRequestData.getBytes().length));
				tConn.setUseCaches (false);

				DataOutputStream tOut = new DataOutputStream(tConn.getOutputStream ());
				tOut.write(pRequestData.getBytes("iso-8859-1"));
				tOut.flush();
				tOut.close();

				if (tConn.getResponseCode() == 429) {
					pLogger.warn("[turfServerPOST] path: " + pPath + " rqst: " + pRequestData + " statuss: 429 requesting too frequently, dismiss 2 sec");
					try {Thread.sleep( 2000L );}
					catch( InterruptedException ie) {}
					tGotAnswer = false;
				} else {

					// Check status code
					if (tConn.getResponseCode() != 200) {
						pLogger.error("[turfServerPOST] path: " + pPath + " rqst: " + pRequestData + "   error response code: " + tConn.getResponseCode());
						return null;
					}


					InputStream tInStream = tConn.getInputStream();
					if ((tConn.getContentEncoding() != null) && (tConn.getContentEncoding().toLowerCase().contains("gzip"))) {
						tInStream = new GZIPInputStream(tConn.getInputStream());
					}

					BufferedReader tReader = new BufferedReader(new InputStreamReader(tInStream, "utf-8"), 8);
					tElement = new JsonParser().parse(tReader);
					tGotAnswer = true;
				}

			} catch (MalformedURLException e) {
				pLogger.error("failed to POST rqst to Turf Game Server, reason: " + e.getMessage());
				if (!pRecovery) {
					return null;
				} else {
					try { Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS); }
					catch( InterruptedException ie) {}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				if (!pRecovery) {
					return null;
				} else {
					try { Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS); }
					catch( InterruptedException ie) {}
				}
			}
		}
		return tElement;
	}

	
}
