package com.hoddmimes.turf.authorize;


import com.hoddmimes.turf.common.generated.TG_LogonRqst;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.UUID;

public class Authenticator implements HttpSessionListener
{
    public static final String TURF_SSID = "TURF_SSID";
    public static final String TURF_USERNAME = "TURF_USERNAME";


    private static Logger cLogger = org.apache.logging.log4j.LogManager.getLogger(Authenticator.class);
    private static long cSessionTimeout = 5 * 60 * 1000;
    private static volatile Authenticator cInstance = null;

    private HashMap<HttpSession,HttpSession> mSessionMap;

    public static void setSessionTimeout( long pSeconds ) {
        cSessionTimeout = pSeconds * 1000L;
    }

    public static Authenticator getInstance() {
        synchronized ( Authenticator.class ) {
            if (cInstance == null) {
                return new Authenticator();
            }
        }
        return cInstance;
    }

    public  Authenticator() {
        mSessionMap = new HashMap<>();
        cInstance = this;
    }


    /**
     * This method is called when a user has been authorized
     * The method is setup/authorize  the session for the user
     * The method is invoked from the Logon servlet
     * @param pLogonRqst
     * @param pRequest
     * @return
     * @throws Exception
     */
    public HttpSession authorize(TG_LogonRqst pLogonRqst, HttpServletRequest pRequest ) throws Exception
    {
        // Check that the request is a POST if not reject the login
        if (!pRequest.getMethod().equals("POST")) {
            cLogger.warn("user Authenticator failure, login called using a GET request, must be a POST request");
            throw new LoginException("Authorization failure, must be a POST request");
        }
        // Check if user is alread logged in
        HttpSession tSession = pRequest.getSession( false );
        if (tSession != null) {
            if ((tSession.getAttribute(TURF_SSID) != null) && (tSession.getAttribute(TURF_USERNAME) != null)) {
                if (tSession.getAttribute(TURF_USERNAME).equals( pLogonRqst.getMailAddress().get())) {
                    return tSession; // User already logged in
                }
            }
        } else {
            tSession = pRequest.getSession( true );
        }

        // require a fresh login
        try {
            tSession.setAttribute( TURF_SSID, UUID.randomUUID().toString());
            tSession.setAttribute(TURF_USERNAME, pLogonRqst.getMailAddress().get());
            this.mSessionMap.put( tSession, tSession );
            return tSession;
        } catch (Exception e) {
            cLogger.warn("user authorization failure user: " + pLogonRqst.getMailAddress().get() + " reason: " + e.getMessage());
            throw new LoginException("Authorization failure");
        }
    }

    @Override
    public  void sessionCreated(javax.servlet.http.HttpSessionEvent pSession) {
        // by design
    }

    @Override
    public void sessionDestroyed(javax.servlet.http.HttpSessionEvent pSession) {
        this.mSessionMap.remove( pSession );
    }
}



