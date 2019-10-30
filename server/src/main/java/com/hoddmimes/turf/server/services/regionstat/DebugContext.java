package com.hoddmimes.turf.server.services.regionstat;


import org.apache.logging.log4j.Logger;

public class DebugContext
{

	public static final int TAKE_OVER_FEED = 2;
	public static final int TAKE_OVER_FEED_VERBOSE = 4;
	public static final int MEMORY = 8;
	public static final int STATISTICS = 16;

		
		
	private int		mDbgFlgs;
	private Logger 	mLogger;
		
	public DebugContext() {
		mLogger = null;
		mDbgFlgs = 0;
	}

	public void setLogger( Logger pLogger ) {
		mLogger = pLogger;
	}

	public void addDebugFlag( int pFlag) {
			mDbgFlgs += pFlag;
		}

	public void debug( int pFlag, String pMessage ){
		if ((mDbgFlgs&pFlag) != 0) {
			mLogger.debug(pMessage);
		}
	}

		
	public boolean ifDebug( int pFlag) {
		if ((mDbgFlgs & pFlag ) != 0) {
			return true;
		}
		return false;
	}
		
		
}
