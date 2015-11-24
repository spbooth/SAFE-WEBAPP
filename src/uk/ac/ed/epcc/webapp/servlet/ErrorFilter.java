// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/*
 * Created on 30-Jul-2004
 *
 
 */

package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.email.logging.ServletEmailLoggerService;
import uk.ac.ed.epcc.webapp.jdbc.JNDIDatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.logging.log4j.Log4JLoggerService;
import uk.ac.ed.epcc.webapp.logging.print.PrintWrapper;
import uk.ac.ed.epcc.webapp.servlet.config.ServletContextConfigService;
import uk.ac.ed.epcc.webapp.servlet.logging.ServletWrapper;
import uk.ac.ed.epcc.webapp.servlet.resource.ServletResourceService;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.DefaultTimerService;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/** Traps uncaught errors and initialises the AppContext.
 * 
 * A Filter is a type of servlet that wraps other servlets using a decorator pattern. 
 * For those URLs a filter is deployed for the filter is called first to handle the request.
 * Usually the filter then invokes nested filters and ultimate servlet/jsp-page via the
 * <code>FilterChain</code>.
 * <p>
 * This filter is a key part of the webapp framework. This class or a subclass should be mapped to cover the entire
 * URL space of the application. It has two main purposes:
 * <ul>
 * <li> The Filter creates the AppContext for use by the request and stores this as an
 * attribute. The exact type of <code>AppContext</code> created is determined by implementing the 
 * <code>getContext</code> method. 
 * <li>The filter attempts to intercept any uncaught exceptions and report them via the <code>AppContext</code>
 * </ul>
 * 
 * @author stephen booth
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ErrorFilter.java,v 1.83 2015/10/12 19:49:46 spb Exp $")
@WebFilter(filterName="FaultFilter", urlPatterns = {"/*"} )
public class ErrorFilter implements Filter {
	private static final Feature SESSION_STEALING_CHECK_FEATURE = new Feature("session-stealing-check",false,"reset session if ip address changes");
	private static final Feature TIMER_FEATURE = new Feature("Timer",false,"gather timing information for performance analyis");
	private static final String LAST_ADDR_ATTR = "LastAddr";
	public static final String APP_CONTEXT_ATTR = "AppContext";

	public ErrorFilter(){
		
	}

	

	protected ServletContext ctx=null;
    
    public void destroy(){
	  ctx = null;
	
    }
   public void init(FilterConfig filterConfig)
	throws ServletException {	
	try {
			// This is where we can initialise parameters using the filterConfig 
		  ctx=filterConfig.getServletContext();
	    
		} catch (Throwable e) {
			 getLogger().fatal("ErrorFilter: Exception ", e);	
			 throw new ServletException("Problem in Filter init",e);
		}
		
   }
	public final void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, java.io.IOException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		AppContext conn = null;
		Logger log = getLogger();
		
		AppUser user=null;
		// IF there is already an AppContext in the request then this is a recursive filter call so skip
		if( req.getAttribute(APP_CONTEXT_ATTR) == null ){
		try {
		
			conn = makeContext(ctx,request,response);
			// report error logs by email with page info
			conn.setService( new ServletEmailLoggerService(conn,req));
			if( TIMER_FEATURE.isEnabled(conn)){
				DefaultTimerService timer_service = new DefaultTimerService(conn);
				conn.setService( timer_service);
				timer_service.startTimer(req.getServletPath());
			}else{
				conn.clearService(TimerService.class);
			}
			log = conn.getService(LoggerService.class).getLogger(getClass());
			//check for session stealing
			HttpSession sess = req.getSession(false);
			if( SESSION_STEALING_CHECK_FEATURE.isEnabled(conn) && sess != null ){
				String host=(String) sess.getAttribute(LAST_ADDR_ATTR);
				if( host != null ){
					if( ! host.equals(req.getRemoteAddr())){ // host address has changed in a session
						conn.error("Possible session stealing remote address has changed "+host+" != "+req.getRemoteAddr());
						if(  conn.getBooleanParameter("session.ipcheck", true)){
						   conn.getService(SessionService.class).clearCurrentPerson();
						}
					}
				}else{
					sess.setAttribute(LAST_ADDR_ATTR, req.getRemoteAddr());
				}
			}
			
			req.setAttribute(APP_CONTEXT_ATTR, conn);
			
		} catch (Throwable e1) {

			log.error("Exception making AppContext " , e1);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e1.getMessage());
			return;
		}
		}
		try {
			
			chain.doFilter(req, res);
			
		} catch(java.net.SocketException se){
			// usually just the browser has gone away just log this
			log.warn("Socket exception "+se.getMessage());
	    }catch (ServletException e) {
	    	
			Throwable root = e.getRootCause();
			if (root == null) {
				root=e.getCause();
				if( root == null ){
					root = e;
				}
			}
			log.error("caught exception in filter",root);
			String show = ctx.getInitParameter("showExceptions");
			if( show != null && show.equals("yes")){
				// we are trying to debug a remote deployment without logger access
				// so we want to show exceptions to the web-user
				// try to get exception shown in error page by rethrowing.
				throw e;
			}
			
			// Note that Servlet2.4 spec says exceptions thrown from a filter
			// are not handled by error-page but error codes are
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch( Throwable t){
			
			// generic throwable catch
			log.error("caught Error in filter",t);
			// Note that Servlet2.4 spec says exceptions thrown from a filter
			// are not handled by error-page but error codes are
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}finally{
			if( user != null ){
				user.release();
			}
			if (conn != null) {
				// remove the cached AppContext as this request object may be passed to
				// the error-page and we are about to invalidate it
				req.setAttribute(APP_CONTEXT_ATTR, null);
				TimerService timer_service = conn.getService(TimerService.class);
				if( timer_service != null ){
					timer_service.stopTimer(req.getServletPath());
				}
				try{
				  conn.close();
				}catch(Throwable t){
					getLogger().error("Error closing AppContext",t);
				}
				conn = null;
			}
		}

	}
	 
	/**
	 * Construct the correct AppContext for use in the Application
	 * 
	 * This is static so classes like ContextListers can make their own
	 * AppContext even if they never see a request object. 
	 * @param ctx 
	 * @param request 
	 * @param response 
	 * @return ServletAppContext
	 * @throws Exception
	 */
	public static AppContext makeContext(ServletContext ctx,ServletRequest request,ServletResponse response) throws Exception{
		AppContext  conn = new AppContext();
		
		// allow use of ServletContext for resource location
		conn.setService(new ServletResourceService(conn,ctx));
		// Add in servlet config parameters. These are application global 
		//so It is ok to add a property cache
		// later This is to pick up global context params that can be used
		// to configure the database service.
		conn.setService(new ServletContextConfigService(ctx,conn));
		// Look for a connection pool. The pool name or database params may be in the 
		// servlet config parameters so we need the ServletConfig first.
		conn.setService( new JNDIDatabaseService(conn));
		// Now add support for DataBase overrides. This needs the db connection
		// so requires the db service.
		conn.setService(new DataBaseConfigService(conn));
		
		// cache the parameters between requests.
		// We want to be able to configure the use of the cache but it completely removes the whole point of 
		// the cache if we query the nested services for a config parameter before installing the cache service
		// instead the the service has to disable itself based on the values it is caching.
		
		// DISABLE this it is too fragile especially in galera clusters where
		// initial DB queries may fail on restart
		//conn.setService(new CachedConfigService(conn));
		


		// This is going to use the config service so apply after the cache is in place
		conn.setService(conn.makeObjectWithDefault(LoggerService.class, Log4JLoggerService.class, "logger.service"));
		if( request != null && response != null ){
			// null request/response means this is a dummy context generated for
			// a context listener
			Class<? extends ServletService> clazz = conn.getPropertyClass(ServletService.class,DefaultServletService.class,  "servlet.service");
			conn.setService(conn.makeParamObject(clazz, conn,ctx,request,response));
			conn.setService(conn.makeObject(ServletSessionService.class, "session.service"));
		}
		return conn;
	}

	/** Get a logger object that does not require the use of an AppContext
	 * Needed for logging problems with creating the AppContext.
	 * 
	 * @return Logger
	 */
	public Logger getLogger(){
	    if( ctx != null ){	
	    	return new ServletWrapper(ctx);
	    }
		return new PrintWrapper();
	 }
	/**
	 * create the required AppContext. We use a factory method like this so we
	 * have the choice of either using a constructor. Or or retrieving an
	 * existing object cached as an attribute.
	 * 
	 * @param req
	 * @return ServletAppContext
	 */
	public static AppContext retrieveAppContext(
			HttpServletRequest req) {
		// AppContext c = new ServletAppContext(serv.getServletContext());
		AppContext c = (AppContext) req
				.getAttribute(APP_CONTEXT_ATTR);
		return c;
	
	}

	

}