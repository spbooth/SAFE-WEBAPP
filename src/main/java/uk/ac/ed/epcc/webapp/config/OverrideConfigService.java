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

import java.util.Enumeration;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.resource.ResourceService;

/**
 * A <code>ConfigService</code> that allows a set of properties to override
 * properties provided by another <em>parent</em> <code>ConfigService</code>
 * 
 * @author spb, jgreen4
 * 
 */
@PreRequisiteService({ConfigService.class})


public class OverrideConfigService extends AbstractConfigService {
	private final ConfigService parent;
	private final Properties overrides;
	private boolean setup=false;

	/**
	 * Constructs a new <code>OverrideConfigService</code> that will return
	 * properties specified in <code>overrides</code> if they are present.
	 * <code>overrides</code> does not contain the requested property,
	 * <code>parent</code> will be used to fetch the requested property.
	 * 
	 * @param overrides
	 *          a collection of properties that will override properties stored in
	 *          <code>parent</code>
	 * @param c AppContext
	 */
	public OverrideConfigService(Properties overrides, AppContext c) {
		super(c);
		this.parent = c.getService(ConfigService.class);
		this.overrides = processAdditions(overrides,c.getService(ResourceService.class));
		setup=true;
	}

	/** Constructs {@link OverrideConfigService} where the override properties are loaded
	 * from a file.
	 * 
	 * @param parent parent {@link Properties} for the overrides
	 * @param config_list file to load
	 * @param c {@link AppContext}
	 */
	public OverrideConfigService(Properties parent,String config_list,AppContext c) {
		super(c);
		this.parent = c.getService(ConfigService.class);
		this.overrides =loadFile(parent, config_list, false);
		setup=true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.ConfigService#clearServiceProperties()
	 */
	public void clearServiceProperties() {
		parent.clearServiceProperties();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.ConfigService#getServiceProperties()
	 */
	public Properties getServiceProperties() {
		if( ! setup ) {
			return this.parent.getServiceProperties();
		}
		Properties result = new Properties(this.parent.getServiceProperties());
		//Add ALL properties from override tree

		for(Enumeration e = overrides.propertyNames(); e.hasMoreElements();){
			String key = e.nextElement().toString();
			String val = overrides.getProperty(key);
			result.setProperty(key, val);
			}
		
		return result;
	}
	public Properties getOverrides(){
		return overrides;
	}

	
	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		String old_value = getServiceProperties().getProperty(name);
		if( old_value != null && old_value.equals(value)){
			return;
		}
		parent.setProperty(name, value);	
	}

	public void cleanup() {
		
		parent.cleanup();
		
	}

	public void addListener(ConfigServiceListener listener) {
		parent.addListener(listener);
		
	}

}