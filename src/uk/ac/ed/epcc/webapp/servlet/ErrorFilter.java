//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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
import uk.ac.ed.epcc.webapp.CleanupService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.OverrideConfigService;
import uk.ac.ed.epcc.webapp.email.logging.EmailLoggerService;
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

@WebFilter(filterName="FaultFilter", urlPatterns = {"/*"} )
public class ErrorFilter implements Filter {
	private static final Feature SESSION_STEALING_CHECK_FEATURE = new Feature("session-stealing-check",false,"reset session if ip address changes");
	private static final Feature CONTEXT_CONFIG_FEATURE = new Feature("context.configuration",false,"Allow additional properties files based on the application Context");
	private static final Feature CLEANUP_THREAD_FEATURE = new Feature("appcontext.cleanup_thread",true,"Close the AppContext in a thread if CleanupServices are defined");
	private static final Feature EMAIL_LOGGING_FEATURE = new Feature("logging.send_email",true,"Send error reports by email");
	public static final Feature TIMER_FEATURE = new Feature("Timer",false,"gather timing information for performance analyis");
	
	private static final String LAST_ADDR_ATTR = "LastAddr";
	public static final String APP_CONTEXT_ATTR = "AppContext";
	public static final String SERVLET_CONTEXT_ATTR = "ServletContext";

	public ErrorFilter(){
		
	}

	public static class Closer implements Runnable{
		/**
		 * @param conn
		 * @param log
		 */
		public Closer(AppContext conn, CleanupService serv, Logger log) {
			super();
			this.conn = conn;
			this.serv = serv;
			this.log = log;
		}
		private final AppContext conn;
		private final CleanupService serv;
		private final Logger log;
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try{
				if( EMAIL_LOGGING_FEATURE.isEnabled(conn)) {
					// we no longer have the request so use normal logger service
					// this will automatically pick up the nested logger if the
					// current logger is a [Servlet]EmailLoggerservice 
					conn.setService(new EmailLoggerService(conn));
				}
				// Make sure Cleanup runs first
				serv.cleanup();
				conn.close();
			}catch(Throwable t){
				log.error("Error closing AppContext", t);
			}
			
		}
		
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
			// Can't log
			throw new ServletException("Problem in Filter init",e);
		}
		
   }
	public final void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, java.io.IOException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		boolean toplevel=false;  // Is this the highest nested instance of this filter
		
		if( req.getAttribute(SERVLET_CONTEXT_ATTR) == null){
			toplevel=true;
			req.setAttribute(SERVLET_CONTEXT_ATTR, ctx);
		}
		
		
		
		
		long start = System.currentTimeMillis();
		
		
		try {
			
			chain.doFilter(req, res);
		} catch(java.net.SocketException se){
			// usually just the browser has gone away just log this
			getCustomLogger(req, res).warn("Socket exception "+se.getMessage());
	    }catch (ServletException e) {
	    	
			Throwable root = e.getRootCause();
			if (root == null) {
				root=e.getCause();
				if( root == null ){
					root = e;
				}
			}
			getCustomLogger(req, res).error("caught exception in filter",root);
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
			try {
				getCustomLogger(req, res).error("caught Error in filter",t);
			}catch(Throwable t2) {
				// Things are really bad
				System.err.println("Unloggable error in filter");
				t.printStackTrace(); // should go to catalina.out
			}
			// Note that Servlet2.4 spec says exceptions thrown from a filter
			// are not handled by error-page but error codes are
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}finally{
			
			
			AppContext conn = (AppContext) req.getAttribute(APP_CONTEXT_ATTR);
			if (conn != null && toplevel) {
				long elapsed = System.currentTimeMillis()-start;
				long max_wait=conn.getLongParameter("max.request.millis", 30000L);
				if( elapsed > max_wait){
					long milli = elapsed % 1000L;
					long seconds = (elapsed / 1000L)%60;
					long min = elapsed / 60000L;
					conn.getService(LoggerService.class).getLogger(getClass()).error("Long running page "+String.format("%d:%02d.%03d", min,seconds,milli));
				}
				// remove the cached AppContext as this request object may be passed to
				// the error-page and we are about to invalidate it
				req.setAttribute(APP_CONTEXT_ATTR, null);
				
				CleanupService cleanup = conn.getService(CleanupService.class);
				// Run cleanup in a seperate thread IF interactive and cleanup actions
				// non interactive jobs always wait as they may be processing a lot of data in
				// a loop and we don't want to exhaust the connection pool
				SessionService sess = conn.getService(SessionService.class);
				boolean interactive = sess != null && sess.haveCurrentUser();
				if( interactive && CLEANUP_THREAD_FEATURE.isEnabled(conn) && cleanup != null && cleanup.hasActions()){
					conn.clearService(CleanupService.class);
					// cleanup in thread
					Thread t = new Thread(new Closer(conn, cleanup, getLocalLogger(req,res)));
					t.start();
				}else{
					try{
						if( cleanup != null && cleanup.hasActions()){
							cleanup.action();
						}
						conn.close();
					}catch(Throwable t){
						getLocalLogger(req,res).error("Error closing AppContext",t);
					}
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
		
		// This includes the default services like DeaultDatabaseService
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
			// Check for a per view override
			
// Allow additional configuration files based on the application context.
// This allows view customisation from a single war-file mapped to multiple contexts
// using context.xml files with docBase set
// Unfortunately this is cumbersome to use with parallel deployment
// as versioned context.xml files need to be uploaded
			if( request instanceof HttpServletRequest && CONTEXT_CONFIG_FEATURE.isEnabled(conn)) {
				String path = ((HttpServletRequest)request).getContextPath();
				String view_prop = "view_properties"+path.replace('/', '.');
				String config_list = conn.getInitParameter(view_prop);
				if( config_list != null) {
					conn.setService(new OverrideConfigService(null, config_list, conn));
				}
			}
		}
		return conn;
	}

	/** Get a logger object that does not require the use of an AppContext
	 * Needed for logging problems with creating the AppContext.
	 * 
	 * @return Logger
	 */
	public static Logger getLocalLogger(HttpServletRequest req,HttpServletResponse res ){
		ServletContext ctx = (ServletContext) req.getAttribute(SERVLET_CONTEXT_ATTR);
	    if( ctx != null ){	
	    	return new ServletWrapper(ctx);
	    }
		return new PrintWrapper();
	 }
	
	public static Logger getCustomLogger(HttpServletRequest req,HttpServletResponse res ){
		AppContext conn=null;
		try {
			conn = retrieveAppContext(req,res);
		} catch (ServletException e) {
			// already want a logger
			Logger log = getLocalLogger(req, res);
			log.error("Error getting custom logger", e);
			return log;
		}
		if( conn == null){
			return getLocalLogger(req,res);
		}
		return conn.getService(LoggerService.class).getLogger(ErrorFilter.class);
	}
	/**
	 * create the required AppContext. We use a factory method like this so we
	 * have the choice of either using a constructor. Or or retrieving an
	 * existing object cached as an attribute.
	 * 
	 * Currently we crate it in a lazy fashion (so no AppContext created for static content)
	 * then cache in the request.
	 * 
	 * @param req
	 * @param res 
	 * @return ServletAppContext
	 * @throws ServletException 
	 */
	public static AppContext retrieveAppContext(
			HttpServletRequest req, HttpServletResponse res) throws ServletException {
		AppContext conn = (AppContext) req
				.getAttribute(APP_CONTEXT_ATTR);
		// IF there is already an AppContext in the request then this is a recursive filter call so skip
		if( req.getAttribute(APP_CONTEXT_ATTR) == null ){
			try {

				conn = makeContext((ServletContext) req.getAttribute(SERVLET_CONTEXT_ATTR),req,res);
				if( EMAIL_LOGGING_FEATURE.isEnabled(conn)) {
					// report error logs by email with page info need to replace this within closer
					conn.setService( new ServletEmailLoggerService(conn,req));
				}
				if( TIMER_FEATURE.isEnabled(conn)){
					DefaultTimerService timer_service = new DefaultTimerService(conn);
					conn.setService( timer_service);
					// Note this timer won't be stopped explicitly
					timer_service.startTimer(req.getServletPath());
				}else{
					conn.clearService(TimerService.class);
				}

				//check for session stealing
				HttpSession sess = req.getSession(false);
				if( sess != null && SESSION_STEALING_CHECK_FEATURE.isEnabled(conn) ){
					String host=(String) sess.getAttribute(LAST_ADDR_ATTR);
					if( host != null ){
						if( ! host.equals(req.getRemoteAddr())){ // host address has changed in a session
							getCustomLogger(req, res).error("Possible session stealing remote address has changed "+host+" != "+req.getRemoteAddr());
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
				throw new ServletException("Exception making AppContext", e1);
			}
		}
		return conn;

	}

	

}