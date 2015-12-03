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