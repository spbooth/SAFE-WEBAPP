// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.config;

import java.io.InputStream;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.Version;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
/** Base {@link ConfigService} that supports the recursive loading of properties from
 * a {@link ResourceService}
 * 
 * @author spb
 *
 */
@Version("$Id: AbstractConfigService.java,v 1.3 2015/03/17 14:40:51 spb Exp $")
@PreRequisiteService({ResourceService.class})
public abstract class AbstractConfigService implements Contexed, ConfigService{

	private static final String SPLIT_REGEX = "\\s*,\\s*";
	private static final String ADD_PROPERTIES = "add_properties";
	private static final String CONFIG_LOADED = "config.loaded.";
	private AppContext conn;
	

	public AbstractConfigService(AppContext conn) {
		assert(conn!=null);
		this.conn=conn;
	}
	

	protected Properties loadFile(Properties parent_props, String config_list, boolean required) {
		//Logger log=conn.getLogger();
		Properties props = new Properties(parent_props);
		Properties result = props;
		
		//log.debug("param="+param+" def="+def+" config_path="+config_path);
		ResourceService serv = conn.getService(ResourceService.class);
		for(String config_path : config_list.split(SPLIT_REGEX)){
			
			//log.debug("stream is "+service_props_stream);
			try {
				InputStream service_props_stream = serv.getResourceAsStream(config_path);
				if (service_props_stream != null) {
					props.load(service_props_stream);
					service_props_stream.close();
					// mark this resource as loaded
					props.setProperty(CONFIG_LOADED+config_path, "true");
				} else {
					if( required ){
						conn.error("Failed to find config "+config_path);
						//Throw fatal error rather than attempting to continue
						// useful if logging is also not working
						throw new ConsistencyError("No properties file found ["+config_path+">");
					}
				}
			} catch (Exception e) {
				conn.error("Exception while loading service properties file: "
						+ config_path);
			}
		}
		result = processAdditions(props, serv);
		return result;
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
						try{
							InputStream service_props_stream = serv.getResourceAsStream(file);
							if (service_props_stream != null) {
								seen=true;
								result.load(service_props_stream);
								service_props_stream.close();
								// mark this resource as loaded
								result.setProperty(CONFIG_LOADED+file, "true");

							}
						}catch(Exception e){
							conn.error("Exception while loading service properties file: "
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

	public final AppContext getContext() {
		return conn;
	}



	

	public final Class<ConfigService> getType() {
		return ConfigService.class;
	}

}