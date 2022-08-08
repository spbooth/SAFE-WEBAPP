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
package uk.ac.ed.epcc.webapp.servlet.config;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigServiceListener;
/** ConfigService that augments the properties with Servlet InitParams.
 * This needs to be added to the {@link AppContext} within the servlet itself
 * after any caching {@link ConfigService}s and once the {@link ServletConfig} is available.
 * @author spb
 *
 */


public class ServletConfigService implements ConfigService {
    private final ConfigService nested_service;
    private final ServletConfig ctx;
    private final AppContext c;
    public ServletConfigService(ServletConfig c, AppContext conn){
    	this.c=conn;
    	nested_service = conn.getService(ConfigService.class);
    	ctx=c;
    }
   
	public void clearServiceProperties() {
		nested_service.clearServiceProperties();
	}

	public Properties getServiceProperties() {
		
			Properties props= new Properties( nested_service.getServiceProperties());
			try {
				if( ctx != null) {
					Enumeration e = ctx.getInitParameterNames();
					while (e.hasMoreElements()) {
						String name= (String) e.nextElement();
						props.setProperty(name, ctx.getInitParameter(name));
					}
				}
			}catch(Exception e) {
				c.error(e,"Error getting servlet properties");
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

	@Override
	public ConfigService getNested() {
		return nested_service;
	}

	
	
}