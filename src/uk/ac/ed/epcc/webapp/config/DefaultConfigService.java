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
package uk.ac.ed.epcc.webapp.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;

public class DefaultConfigService extends AbstractConfigService implements ConfigService, Contexed{
	public static final String BUILD_PATH_PROP_NAME = "build.path";
	/**
	 * 
	 */
	public static final String DEPLOY_PATH_PROP_NAME = "deploy.path";
	/**
	 * 
	 */
	public static final String CONFIG_PATH_PROP_NAME = "config.path";
	/**
	 * 
	 */
	public static final String DEFAULT_PATH_PROP_NAME = "default.path";
	Properties service_props=null;
	protected Set<ConfigServiceListener> listeners = null;
	public DefaultConfigService(AppContext conn){
		super(conn);
	}
	
	/**
	 * Service Context Attributes - loadAttributes if the config.path property is
	 * defined then load from that file. otherwise use the ClassLoader to load a
	 * files from the System classpath called service-config.properties and deploy-config.properties
	 * In this second case values in deploy-properties take precidence
	 * 
	 */
	public synchronized Properties getServiceProperties() {
			// Only load them once:
			if (service_props == null ) {
				// set this now to avoid looping when we call getInitParameter in this
				// method
			
				// Initialise to an empty Properties object:
				try {
					service_props = System.getProperties();
				}catch(SecurityException sex) {
					// must have a restrictive manager inplace
					service_props = new Properties();
				}
			
				// default is fine for deployment but may want to override in Junit tests
			
				//optional default props common to all 
				service_props = loadFile(service_props,service_props.getProperty(DEFAULT_PATH_PROP_NAME,"default-config.properties"),false);
				service_props = loadFile(service_props,service_props.getProperty(CONFIG_PATH_PROP_NAME,"service-config.properties"),true);
				service_props = loadFile(service_props,service_props.getProperty(DEPLOY_PATH_PROP_NAME,"deploy-config.properties"),false);
				service_props = loadFile(service_props,service_props.getProperty(BUILD_PATH_PROP_NAME,"build-config.properties"),false);
				
				
			}
			return service_props;
		
	}
	

	public void addListener(ConfigServiceListener listener) {
		if( listeners == null ){
			listeners=new HashSet<ConfigServiceListener>();
		}
		listeners.add(listener);
		
	}

	
	public void clearServiceProperties() {
		service_props=null;
		if( listeners != null ){
			for(ConfigServiceListener l : listeners){
				l.resetConfig();
			}
		}
	}

	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Set property not supported");
		
	}

	public void cleanup() {
		if( service_props != null ){
			service_props.clear();
			service_props=null;
		}
		if( listeners != null){
			listeners.clear();
			listeners=null;
		}
		
	}

	

}