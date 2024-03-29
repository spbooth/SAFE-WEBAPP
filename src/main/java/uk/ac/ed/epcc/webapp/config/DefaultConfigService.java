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
/** The default {@link ConfigService}
 * 
 * This starts with a default set of {@link Properties} usually the system properties
 * but this can be augmented depending on the context by passing in an explicit {@link Properties} object on
 * the constructor. A web-context may augment the properties from the Servlet config. When testing an additional set of
 * properties is loaded from <b>test.properties</b>.
 * Then a series of standard property files are loaded. For each standard file there is a property (that can be set in the initial set)
 * that can be used to change the name of the property file from the default. Each file corresponds to a different
 * module/jar-file in the final build.
 * <table>
 * <tr><th>prop-name</th><th>default filename</th><th>Intended purpose</th></tr>
 * <tr><td>default.path</td><td>default-config.properties</td><td>Generic application configuration</td></tr>
 * <tr><td>config.path</td><td>service-config.properties</td><td>Instance specific configuration</td></tr>
 * <tr><td>deploy.path</td><td>deploy-config.properties</td><td>View configuration within a instance.</td></tr>
 * <tr><td>build.path</td><td>build-config.properties</td><td>Generated automatically by the build to hold version information</td></tr>
 * <caption>Configuration parameters</caption>
 * </table>
 * 
 * 
 * @author Stephen Booth
 *
 */
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
	private Properties service_props=null;
	// This is the bootstrap set of properties where the
	// property path values can be set.
	private final Properties bootstrap;
	protected Set<ConfigServiceListener> listeners = null;
	public DefaultConfigService(AppContext conn){
		this(conn,null);
	}
	/** Initialise with a specific set of default props rather than the system properties.
	 * 
	 * A null value means lazy evaluation using {@link System#getProperties()}.
	 * @param conn
	 * @param p
	 */
	public DefaultConfigService(AppContext conn,Properties p){
		super(conn);
		if( p == null ) {
			try {
				p = System.getProperties();
			}catch(SecurityException sex) {
				// must have a restrictive manager inplace
				// load from default names
				p = new Properties();
			}
		}
		bootstrap=p;
	}
	/**
	 * Service Context Attributes - loadAttributes if the config.path property is
	 * defined then load from that file. otherwise use the ClassLoader to load a
	 * files from the System classpath called service-config.properties and deploy-config.properties
	 * In this second case values in deploy-properties take precedence.
	 * 
	 * Look for a build-config.properies file containing info from the build system
	 * 
	 */
	@Override
	public synchronized Properties getServiceProperties() {
			// Only load them once:
			if (service_props == null ) {
				// set this now to avoid looping when we call getInitParameter in this
				// method
				setServiceProps(bootstrap);
				
				
			}
			return service_props;
		
	}

	private void setServiceProps(Properties props) {
		service_props=props;
		// default is fine for deployment but may want to override in Junit tests

		//optional default props common to all 
		service_props = loadFile(service_props,service_props.getProperty(DEFAULT_PATH_PROP_NAME,"default-config.properties"),false);
		service_props = loadFile(service_props,service_props.getProperty(CONFIG_PATH_PROP_NAME,"service-config.properties"),true);
		service_props = loadFile(service_props,service_props.getProperty(DEPLOY_PATH_PROP_NAME,"deploy-config.properties"),false);
		service_props = loadFile(service_props,service_props.getProperty(BUILD_PATH_PROP_NAME,"build-config.properties"),false);
	}
	

	@Override
	public void addListener(ConfigServiceListener listener) {
		if( listeners == null ){
			listeners=new HashSet<>();
		}
		listeners.add(listener);
		
	}

	
	@Override
	public void clearServiceProperties() {
		service_props=null;
		if( listeners != null ){
			for(ConfigServiceListener l : listeners){
				l.resetConfig();
			}
		}
	}

	@Override
	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Set property not supported");
		
	}

	@Override
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
	@Override
	public ConfigService getNested() {
		return null;
	}

	

}