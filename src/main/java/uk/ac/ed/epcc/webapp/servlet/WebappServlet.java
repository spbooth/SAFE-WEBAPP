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
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.servlet.config.ServletConfigService;

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
		context.getService(LoggerService.class).getLogger(context.getClass()).debug("sending message " + message_type);
		// verify the message is valid
		try {
			MessageBundleService serv = context.getService(MessageBundleService.class);
			ResourceBundle mess = serv.getBundle();
			mess.getString(message_type + ".title");
			mess.getString(message_type + ".text");
		} catch (MissingResourceException e) {
			// report the bad message including call site
			context.error(e, "Bad message " + message_type);
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
}