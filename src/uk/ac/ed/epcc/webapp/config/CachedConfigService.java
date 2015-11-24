// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.config;

import java.util.Enumeration;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
/** Cache Properties in a global static variable. 
 * This can be turned off with configuration parameters but it has to be done
 * within this class to make sure that the queries to the underlying services to check the feature
 * are not made when the cache IS active.
 * 
 * The dependencies between this and the {@link DataBaseConfigService} are very fragile so edit this
 * at your own peril.
 * 
 * @author spb
 *
 */
@PreRequisiteService({ConfigService.class})
@uk.ac.ed.epcc.webapp.Version("$Id: CachedConfigService.java,v 1.7 2014/12/10 12:48:05 spb Exp $")
/**
 * 
 * @author spb
 *
 */
public class CachedConfigService implements ConfigService {
    private final ConfigService service;
    private boolean disabled=false;  //This instance of the CacheService is disabled.
	public static final Feature CACHED_CONFIG_FEATURE = new Feature("cached_config",true,"allow config to be cached statically between requests");
    public CachedConfigService(AppContext c){
    	service=c.getService(ConfigService.class);
    }
    private static Properties cached_properties=null;
	public void clearServiceProperties(){
		invalidate();
		service.clearServiceProperties();
	}

	public static void invalidate() {
		synchronized (CachedConfigService.class) {
			if( cached_properties != null ){
				cached_properties.clear();
				cached_properties=null;
			}
		}
	}

	public Properties getServiceProperties() {
		if( disabled ){
			return service.getServiceProperties();
		}
		synchronized(getClass()){
			if( cached_properties == null ){
				
				
				disabled=true; // don't enable until cache is valid 
				cached_properties = new Properties();
				
				// this call may trigger an invalidate that clears the cache
				// so check value afterwards, may also trigger recursive calls
				// to getServiceProperties which is why w set disable.
				Properties prop = service.getServiceProperties();
				if( cached_properties == null ){
					// we have had an invalidate while populating the cache
					// return nested for this call but don't cache.
					disabled=false;
					return prop;
				}
				if( ! CACHED_CONFIG_FEATURE.isEnabled(prop)){
					invalidate();
					disabled=true; // don't attempt cache for rest of life
					return prop;
				}
				// important to copy as underlying Properties explicitly 
				// the underlying service may 
				// be cleared on close of parent AppContext
				// also need to make sure we use the enumerator to loop
				// over ALL the underlying properties including the defaults
				for(Enumeration e = prop.propertyNames(); e.hasMoreElements();){
					
					String name = (String) e.nextElement();
					String property = prop.getProperty(name);
					//System.out.println("Caching "+name+"="+property);
					cached_properties.setProperty(name,property);
				}
				disabled=false; // cache now valid
			}
			
			// Don't allow calling code opportunity to modify cache.
			return new Properties(cached_properties);
			
			
		}
	}

	public AppContext getContext() {
		return service.getContext();
	}

	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		service.setProperty(name, value);
		invalidate();
		
	}

	public void cleanup() {
		service.cleanup();
	}

	public void addListener(ConfigServiceListener listener) {
		if( listener != this){
			service.addListener(listener);
		}
	}

	

	public Class<ConfigService> getType() {
		return ConfigService.class;
	}



	
	
}