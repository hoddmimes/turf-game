package com.hoddmimes.turf.server.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.w3c.dom.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Turf
{
	public static final String URL_PATH = "http://api.turfgame.com/v4/";
	
	private static long DEFAULT_ERROR_WAIT_MS = 15000L;
	
	public static final SimpleDateFormat TurfSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat SDFSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat SDFSimpleDateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDFDate = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDFSimpleTime = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat SDFTime = new SimpleDateFormat("HH:mm:ss.SSS");
	
	
	public static  String formatTimeDiff( long pMs ) {
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
	
	
	public static  JsonElement turfServerGET( String pPath ) {
		return turfServerGET(pPath, true);
	}

	@SuppressWarnings("static-access")
	public static  JsonElement turfServerGET( String pPath, boolean pRecovery ) 
	{
		JsonElement tElement = null;
		boolean tGotAnswer = false;
		boolean tConnectSuccess = false;

		while (!tGotAnswer) {
			try {
				tConnectSuccess = false;
				URLConnection tConn = new URL(URL_PATH + pPath ).openConnection();
				tConnectSuccess = true;

				tConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				InputStream tInStream = new GZIPInputStream(tConn.getInputStream());


				BufferedReader tReader = new BufferedReader(new InputStreamReader(tInStream, "utf-8"), 8);
				tElement  =  new JsonParser().parse(tReader);
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
            } catch( ZipException ze) {
                    if (tConnectSuccess) {
                        return null;
                    }
			} catch (IOException e) {
				System.out.println( e.getMessage());
				if (!pRecovery) {
					return null;
				} else {
					if (tConnectSuccess) {
						return null;
					}
					try { Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS); }
					catch( InterruptedException ie) {}
				}
			}
		}
		return tElement;
	}
	
	public static  JsonElement turfServerPOST( String pPath, String pRequestData ) {
		return turfServerPOST(pPath, pRequestData, true);
	}
	
	@SuppressWarnings("static-access")
	public static JsonElement turfServerPOST( String pPath, String pRequestData, boolean pRecovery ) 
	{
		JsonElement tElement = null;
		boolean tGotAnswer = false;
		boolean tConnectSuccess = false;

		while (!tGotAnswer) {
			try {
				tConnectSuccess = false;
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
				tConnectSuccess = true;


				InputStream tInStream = new GZIPInputStream( tConn.getInputStream());
				BufferedReader tReader = new BufferedReader(new InputStreamReader(tInStream, "utf-8"), 8);
				tElement  =  new JsonParser().parse(tReader);
				tGotAnswer = true;

			} catch (MalformedURLException e) {
				System.out.println( e.getMessage());
				if (!pRecovery) {
					return null;
				} else {
					try { Thread.currentThread().sleep(DEFAULT_ERROR_WAIT_MS); }
					catch( InterruptedException ie) {}
				}
			}
			catch( ZipException ze) {
			    if (tConnectSuccess) {
			        return null;
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
