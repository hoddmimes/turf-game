package com.hoddmimes.turf.server.services.regionstat;


public class DistanceCalculator
{
	public static void main (String[] args) throws Exception
	{
		System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506) + " meter\n");
	}

	public static double distance(double pLat1, double pLong1, double pLat2, double pLong2) {

		/**
		 * 
		 * dlon = lon2 - lon1
		 * 
		 * dlat = lat2 - lat1
		 * 
		 * a = (sin(dlat/2))^2 + cos(lat1) * cos(lat2) * (sin(dlon/2))^2
		 * 
		 * c = 2 * atan2( sqrt(a), sqrt(1-a) )
		 * 
		 * d = R * c (where R is the radius of the Earth)
		 */

		double tDiffLong = Math.toRadians(pLong2 - pLong1);
		double tDiffLat = Math.toRadians(pLat2 - pLat1);
		double a = Math.pow(Math.sin(tDiffLat / 2), 2)
				+ Math.cos(Math.toRadians(pLat1))
				* Math.cos(Math.toRadians(pLat2))
				* Math.pow(Math.sin(tDiffLong / 2), 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d - a));
		double d = c * (6365d * 1000d); // optimized for LAT 59 degrees
		return d;

	}
}
