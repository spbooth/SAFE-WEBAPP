// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



/** Servlet to initialise logging based on an embedded configuration file
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Log4jInit.java,v 1.13 2014/09/15 14:30:34 spb Exp $")
@Deprecated
public class Log4jInit extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
	   // We don't use an AppContext here as this is 100% servlet code
	   // and we may need logging initialised to debug problems with the
	   // AppContext code itself so lets keep the dependencies simple.
		try{
		String file = getServletContext().getRealPath( getInitParameter("log4j-init-file") );
		// if the log4j-init-file is not set, then no point in trying
		if(file != null ) {
			PropertyConfigurator.configure(file);
			Logger.getRootLogger().info("Config from "+file);
		}else{
		    Logger.getRootLogger().warn("No config file found "+file);
		}
		}catch(Exception e){
			Logger.getRootLogger().error("Failed to init log4J",e);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		

		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		
		out.println("Log4jInit.doGet()");
		Logger log = Logger.getLogger(getClass().getCanonicalName());
		log.debug("doGet called");
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		
		out.println("Log4jInit.doPost()");
		Logger log = Logger.getLogger(getClass().getCanonicalName());
		log.debug("doPost called");
	}	
}
