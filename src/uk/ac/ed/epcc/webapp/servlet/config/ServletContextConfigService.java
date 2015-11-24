// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet.config;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigServiceListener;
/** ConfigService that augments the properties with the global
 * servlet context parameters. The {@link ServletContext} is available in the
 * filter so this can be installed there. These values can be cached.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ServletContextConfigService.java,v 1.2 2014/09/15 14:30:35 spb Exp $")

public class ServletContextConfigService implements ConfigService {
    private final ConfigService nested_service;
    private final ServletContext ctx;
    private final AppContext c;
    public ServletContextConfigService(ServletContext c, AppContext conn){
    	this.c=conn;
    	nested_service = conn.getService(ConfigService.class);
    	ctx=c;
    }
   
	public void clearServiceProperties() {
		nested_service.clearServiceProperties();
	}

	public Properties getServiceProperties() {
		
			Properties props= new Properties( nested_service.getServiceProperties());
			Enumeration e = ctx.getInitParameterNames();
			while (e.hasMoreElements()) {
				String name= (String) e.nextElement();
				props.setProperty(name, ctx.getInitParameter(name));
			}
		
		return props;
	}

	public AppContext getContext() {
		return c;
	}
	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		nested_service.setProperty(name, value);
	}
	public void cleanup() {
		nested_service.cleanup();
	}

	public void addListener(ConfigServiceListener listener) {
		nested_service.addListener(listener);
		
	}

	

	public Class<? super ConfigService> getType() {
		return ConfigService.class;
	}

	
	
}