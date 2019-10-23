package com.hoddmimes.turf.authorize;



import com.hoddmimes.turf.common.TGStatus;
import com.hoddmimes.turf.common.generated.TG_Response;
import com.hoddmimes.turf.servlets.ServletResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class AuthorizationFilter implements Filter
{
    private static Logger cLogger = LogManager.getLogger( AuthorizationFilter.class );
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private boolean mVerbose = false;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession tSession = request.getSession(false);

        if ((tSession == null) || ((tSession.getAttribute(Authenticator.TURF_SSID) == null) && (tSession.getAttribute(Authenticator.TURF_USERNAME) == null))) {
            if (tSession == null) {
                log("[FAILURE] No session, path: " + request.getServletPath());
            } else {
                log("[FAILURE]  path: " + request.getServletPath() + " session: " + displaySession( tSession ));
            }

            /**
             * No authorized session context is  found
             * Reject further processing by sending a response and cut the filter chain
             */
            TG_Response jTgStatus = TGStatus.create( false, "Authorization failure, not logged in", "/turf/znLogon.html");
            try {
                ServletResources.sendResponse( jTgStatus.toJson(), response );
            }
            catch( Exception e ) {
                throw new ServletException( e );
            }
            return; // cut the filter chain
        }

        log("[Authorized]  path: " + request.getServletPath() + " session: " + displaySession( tSession ));


        filterChain.doFilter( servletRequest,servletResponse );
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        String tVerbosValueString = filterConfig.getInitParameter("verbose");
        mVerbose = (tVerbosValueString == null) ? false : Boolean.parseBoolean(tVerbosValueString);
    }

    @Override
    public void destroy() {

    }

    private String  displaySession( HttpSession pSession ) {
        SimpleDateFormat tSDF = new SimpleDateFormat("HH:mm:ss.SSS");
        return "user: " + pSession.getAttribute( Authenticator.TURF_USERNAME) + " cretim: " + tSDF.format( pSession.getCreationTime()) + " id: " + pSession.getId() +
                "acctim: " + tSDF.format( pSession.getLastAccessedTime());
    }

    private void log( String pMessage ) {
        if (mVerbose) {
            System.out.println( SDF.format( System.currentTimeMillis()) + " [AuthorizationFilter] " + pMessage );
            cLogger.info( pMessage );
        }
    }



}
