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
 * Created on 20-May-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.servlet.config.ServletConfigService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * webappServlet Base class for servlets in the web application, it hold
 * generally useful methods for use by all servlets
 * 
 * @author spb
 * 
 */
public abstract class WebappServlet extends HttpServlet {

	/**
	 * 
	 */
	public static final String MESSAGES_JSP_URL = "/messages.jsp";
	
	/** The attribute name for the message type.
	 * This is looked up in the message bundle to get the text
	 * 
	 */
	public static final String MESSAGE_TYPE_ATTR = "message_type";
	/** The attribute name for the secondary message (optional)
	 * This can be passed as an attribute to add secondary text to a message 
	 * default extra text can also be included in the message bundle 
	 * with a name based on the message type.
	 * 
	 */
	public static final String MESSAGE_EXTRA_ATTR = "message_extra";
	/** attribute for a boolean added to the request which
	 * indicates the current request does not modify state and is safe to
	 * re-run on a page refresh
	 * 
	 */
	public static final String NON_MODIFY_ATTR = "non_modifying";
	public static final String CONFIRM_NO = "no";
	public static final String CONFIRM_YES = "yes";
	public static final String EXTRA_HTML = "extra_html";
	public static final String ARGS = "args";
	public static final String SCRIPTS_CONFIRM_JSP = "/scripts/confirm.jsp";
	public static final String CONFIRM_TYPE = "confirm_type";
	public static final String CONFIRM_POST_URL = "post_url";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public WebappServlet() {
		super();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, java.io.IOException {

		// Delegate GETs to doPost()
		doPost(req, res);
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		AppContext conn=null;
		try {
			conn = ErrorFilter.retrieveAppContext(req,res);
			// pick up servlet specific init params if there are any
			ServletConfig cfg = getServletConfig();
			Enumeration e = cfg.getInitParameterNames();
			if( e.hasMoreElements()){
				conn.setService(new ServletConfigService(cfg, conn));
			}
			doPost(req, res, conn);
		}catch(IOException e){
			throw e;
		}catch(Exception e2) {
			if( conn !=null ) {
				getLogger(conn).error("Error in servlet post",e2);
			}
			throw e2;
		}
	}

	@Override
	protected final void doPut(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		AppContext conn;

		conn = ErrorFilter.retrieveAppContext(req,res);
		// pick up servlet specific init params if there are any
		ServletConfig cfg = getServletConfig();
		Enumeration e = cfg.getInitParameterNames();
		if( e.hasMoreElements()){
			conn.setService(new ServletConfigService(cfg, conn));
		}
		doPut(req, res, conn);
	}
	/**
	 * Method that does the actual work
	 * 
	 * @param req
	 * @param res
	 * @param conn
	 * @throws ServletException
	 * @throws java.io.IOException
	 */
	protected abstract void doPost(HttpServletRequest req,
			HttpServletResponse res, AppContext conn)
			throws ServletException, java.io.IOException;
	
	/** Method to handle put requests
	 * 
	 * @param req
	 * @param res
	 * @param conn
	 * @throws ServletException
	 * @throws java.io.IOException
	 */
	protected void doPut(HttpServletRequest req,
			HttpServletResponse res, AppContext conn)
			throws ServletException, java.io.IOException{
		res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Put not allowed");
		return;
	}
	
	/**
	 * get a logger based on the class name
	 * @param c 
	 * 
	 * @return Logger
	 */
	public Logger getLogger(AppContext c) {
		return c.getService(LoggerService.class).getLogger(getClass());
	}
	
	private static final String SUSPICIOUS_ARGUMENT_ATTR = "SuspiciousArgumentCount";

	/** Call this method when invalid data is passed to something that
	 * should have been application generated and is therefore might be 
	 * indicative of somebody probing for vulnerabilities. 
	 * 
	 * @param conn
	 */
	public void badInputCheck(AppContext conn) {
		SessionService sess = conn.getService(SessionService.class);
		Logger logger = getLogger(conn);
		doBadInputCheck(conn, sess, logger);
	}

	public static void checkBadInput(SessionService sess) {
		doBadInputCheck(sess.getContext(), sess, sess.getContext().getService(LoggerService.class).getLogger(WebappServlet.class));
	}
	/** static version of {@link #badInputCheck(AppContext)}
	 * for use by jsp pages
	 * 
	 * @param conn
	 * @param sess
	 * @param logger
	 */
	private static void doBadInputCheck(AppContext conn, SessionService sess, Logger logger) {
		if( sess != null ) {
			// This is a test for somebody/fuzzing probing the 
			// interface too many fails suggest something odd is going on. 
			Integer fail_count = (Integer) sess.getAttribute(SUSPICIOUS_ARGUMENT_ATTR);
			if( fail_count == null ) {
				fail_count = Integer.valueOf(1);
			}else {
				fail_count = Integer.valueOf(fail_count.intValue()+1);
			}
			sess.setAttribute(SUSPICIOUS_ARGUMENT_ATTR, fail_count);
			if( fail_count.intValue() > conn.getIntegerParameter("transition.fail_count_thresh", 10)) {
				
				logger.error("Too many bad transition targets, possible probing?");
			}
		}
	}

	/**
	 * Show a standard pre-formatted message page from the servlet. This can
	 * only be called once as it won't work if any output is committed its the
	 * standard way for a servlet to generate output other than redirecting to a
	 * submitting page.
	 * 
	 * Pre-formatted messages are defined in bundles and pass through {@link AppContext#expandText(String)}
	 * before being shown
	 * 
	 * @see MessageBundleService
	 * @param context
	 * @param req
	 * @param res
	 * @param message_type
	 * @throws ServletException
	 * @throws IOException
	 */
	public void message(AppContext context, HttpServletRequest req,
			HttpServletResponse res, String message_type)
			throws ServletException, IOException {
		// make sure that this call does not recurse
		sendMessageWithArgs(context, req, res, message_type, null);
	}
	
	/**
	 * Show a standard pre-formatted message page from the servlet. This can
	 * only be called once as it won't work if any output is committed its the
	 * standard way for a servlet to generate output other than redirecting to a
	 * submitting page.
	 * 
	 * Pre-formatted messages are defined in bundles and pass through {@link AppContext#expandText(String)}
	 * before being shown
	 * 
	 * @see MessageBundleService
	 * 
	 * @param context
	 *            AppContext
	 * @param req
	 * @param res
	 * @param message_type
	 *            name of message
	 * @param args
	 *            array of arguments.
	 * @throws ServletException
	 * @throws IOException
	 */
	public  void message(AppContext context, HttpServletRequest req,
			HttpServletResponse res, String message_type, Object... args)
			throws ServletException, IOException {
		sendMessageWithArgs(context,req,res,message_type,args);
	}
	protected void sendMessageWithArgs(AppContext context,HttpServletRequest req,
			HttpServletResponse res, String message_type, Object args[])
			throws ServletException, IOException {
		messageWithArgs(context, req, res, message_type, args);
	}
	
	public static void messageWithArgs(AppContext context, HttpServletRequest req,
				HttpServletResponse res, String message_type, Object args[])
				throws ServletException, IOException {
		Logger log = context.getService(LoggerService.class).getLogger(context.getClass());
		 // If we can use MessageServlet on modifying post/put to avoid re-submit
		 String method = req.getMethod();
		 if( (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) && ! WebappServlet.isNonModifying(req)) {
			 String r = MessageServlet.mapMessageToRedirect(context, message_type,args);
			 if( r != null) {
				 context.getService(ServletService.class).redirect(r);
				 return;
			 }
		 }
		log.debug("sending message " + message_type);
		// verify the message is valid
		try {
			MessageBundleService serv = context.getService(MessageBundleService.class);
			ResourceBundle mess = serv.getBundle();
			mess.getString(message_type + ".title");
			mess.getString(message_type + ".text");
		} catch (MissingResourceException e) {
			// report the bad message including call site
			log.error("Bad message " + message_type, e);
		}
		req.setAttribute(MESSAGE_TYPE_ATTR, message_type);
		if( args != null ) {
			req.setAttribute(ARGS, args);
		}else {
			req.removeAttribute(ARGS);
		}
		// Forward to message page with appropriate arguments
		context.getService(ServletService.class).forward(MESSAGES_JSP_URL);
	}
	/** process a confirm page
     * It either returns a Boolean object if the answer is available or 
     * redirects to the confirm page and returns null.
     * The title and text of the confirm message is defined in confirm.properties
     * @param req
     * @param res
     * @param conn 
     * @param type type of message 
     * @param args 
     * @return TRUE or FALSE or NULL
     * @throws IOException 
     * @throws ServletException 
     */

	 public Boolean confirm(HttpServletRequest req, HttpServletResponse res, AppContext conn, String type, Object args[]) throws IOException, ServletException{
	   return confirm(req,res,conn,type,args,null);
	 }
	 /** Does the {@link #confirm(HttpServletRequest, HttpServletResponse, AppContext, String, Object[])}
	  * method need to show the dialog form.
	  * This will return true if a call to confirm would return null.
	  * @param req
	  * @return
	  */
	 public boolean needConfirmDialog(HttpServletRequest req) {
		 return req.getParameter(CONFIRM_YES) == null && req.getParameter(CONFIRM_NO) == null;
	 }
	 /** Handle  confirm dialog
	  * 
	  * The method will return null if the dialog form is shown.
	  * 
	  * @param req
	  * @param res
	  * @param conn
	  * @param type
	  * @param args
	  * @param extra
	  * @return
	  * @throws IOException
	  * @throws ServletException
	  */
	 public Boolean confirm(HttpServletRequest req, HttpServletResponse res, AppContext conn, String type, Object args[],SimpleXMLBuilder extra) throws IOException, ServletException{	 
    	String yes = req.getParameter(CONFIRM_YES);
    	String no = req.getParameter(CONFIRM_NO);
    	if( no != null ){
    		return Boolean.FALSE;
    	}
    	if( yes != null ){
    		return Boolean.TRUE;
    	}
    	/* we have to show the dialog box and return here */
    	String my_url = req.getRequestURI();
    	if( my_url == null ){
    		getLogger(conn).error("null Request URI");
    		return null;
    	}
    	req.setAttribute(CONFIRM_POST_URL,my_url);
    	req.setAttribute(CONFIRM_TYPE,type);
    	req.setAttribute(ARGS,args);
    	if( extra != null ){
    		req.setAttribute(EXTRA_HTML, extra.toString());
    	}
    	getServletConfig().getServletContext().getRequestDispatcher(SCRIPTS_CONFIRM_JSP).forward(req, res);
    	
    	return null;
    }

public void handleFormResult(AppContext conn,HttpServletRequest req, HttpServletResponse res, FormResult result)throws Exception{
		 if( result == null ) {
			 return;
		 }
		 ServletFormResultVisitor vis = new ServletFormResultVisitor(conn, req, res);
		 result.accept(vis);
}




	/**
	 * Translates a string into CGI format (used for encoding form values in
	 * URLs)
	 * 
	 * @param input
	 *            the <code>String</code> to encode
	 * @return String a CGI encoded string
	 */
	public static String encodeCGI(String input) {
		if (input == null)
			return null;

		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (ch == ' ') {
				output.append('+');
				continue;
			}
			// unreserved chars as per RFC3986
			if (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))
					|| ((ch >= '0') && (ch <= '9')) || (ch == '_')
					|| (ch == '-') || (ch == '.') || ( ch == '~')) {
				output.append(ch);
			} else {
				output.append('%');
				output.append(Integer.toString(ch, 16));
			}
		}
		return output.toString();
	}
	public static void setNonModifying(HttpServletRequest req, boolean non_modifying) {
		req.setAttribute(NON_MODIFY_ATTR, non_modifying);
	}
	public static boolean isNonModifying(HttpServletRequest req) {
		Boolean val = (Boolean) req.getAttribute(NON_MODIFY_ATTR);
		if( val != null) {
			return val.booleanValue();
		}
		return false;
	}
}