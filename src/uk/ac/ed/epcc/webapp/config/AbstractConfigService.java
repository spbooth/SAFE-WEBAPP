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
package uk.ac.ed.epcc.webapp.config;

import java.io.InputStream;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.Version;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
import uk.ac.ed.epcc.webapp.timer.TimerService;
/** Base {@link ConfigService} that supports the recursive loading of properties from
 * a {@link ResourceService}
 * 
 * @author spb
 *
 */

@PreRequisiteService({ResourceService.class})
public abstract class AbstractConfigService extends AbstractContexed implements ConfigService{

	private static final String SPLIT_REGEX = "\\s*,\\s*";
	private static final String ADD_PROPERTIES = "add_properties";
	private static final String CONFIG_LOADED = "config.loaded.";
	

	public AbstractConfigService(AppContext conn) {
		super(conn);
	}
	

	protected Properties loadFile(Properties parent_props, String config_list, boolean required) {
		//Logger log=conn.getLogger();
		Properties props = new Properties(parent_props);
		Properties result = props;
		TimerService timer = conn.getService(TimerService.class);
		if( timer != null ){ timer.startTimer(config_list);}
		//log.debug("param="+param+" def="+def+" config_path="+config_path);
		ResourceService serv = conn.getService(ResourceService.class);
		for(String config_path : config_list.split(SPLIT_REGEX)){
			
			//log.debug("stream is "+service_props_stream);
			try(InputStream service_props_stream = serv.getResourceAsStream(config_path)) {
			
				if (service_props_stream != null) {
					props.load(service_props_stream);
					// mark this resource as loaded
					props.setProperty(CONFIG_LOADED+config_path, "true");
				} else {
					if( required ){
						error("Failed to find config "+config_path);
						//Throw fatal error rather than attempting to continue
						// useful if logging is also not working
						throw new ConsistencyError("No properties file found ["+config_path+"]");
					}
				}
			} catch (Exception e) {
				error("Exception while loading service properties file: "
						+ config_path);
			}
		}
		result = processAdditions(props, serv);
		if( timer != null ){ timer.stopTimer(config_list);}
		return result;
	}
	
	/**
	 * Report an application error.
	 * Needs to handle the possiblity of the LoggerService not being present as
	 * we can't make it a pre-requisite here
	 * 
	 * @param errors
	 *            Text of error.
	 */
	
	final void error(String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors);
			}
		}
	}


	public Properties processAdditions(Properties props, ResourceService serv) {
		// a property of the form add_properties.* is a list of additional property files to 
		// be loaded to avoid circular loops we ignore any such parameter where the name is declared in the parent
		// process files in order so we can specify override files
		boolean seen=false;
		Properties result=new Properties(props);
		for(Object key : props.keySet() ){
			String name = key.toString();
			if( name.startsWith(ADD_PROPERTIES)){
				for(String file : props.getProperty(name).split(SPLIT_REGEX)){
					if( null ==  props.getProperty(CONFIG_LOADED+file)){
						try(InputStream service_props_stream = serv.getResourceAsStream(file)){
							if (service_props_stream != null) {
								seen=true;
								result.load(service_props_stream);
								// mark this resource as loaded
								result.setProperty(CONFIG_LOADED+file, "true");

							}else{
								error("failed to find included properties file "+file);
							}
						}catch(Exception e){
							error("Exception while loading service properties file: "
									+ file);
						}
					}

				}

			
			}
		}
		if( seen==false ){
			return props;
		}else{
			
			//make sure we don't override non-default props
			// in original
			// overriding defaults is ok
			for(Object key : props.keySet()){
				String name=key.toString();
				if( ! name.startsWith(ADD_PROPERTIES)){
					String val = props.getProperty(name);
					result.setProperty(name, val);
				}
			}
			return processAdditions(result, serv);
		}
	}

	public final Class<ConfigService> getType() {
		return ConfigService.class;
	}

}