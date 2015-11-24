// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: OverrideConfigService.java,v 1.3 2014/09/15 14:30:14 spb Exp $")

public class OverrideConfigService extends AbstractConfigService {
	private final ConfigService parent;
	private final Properties overrides;

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
		Properties result = new Properties(this.parent.getServiceProperties());
		//Add ALL properties from override tree
		for(Enumeration e = overrides.propertyNames(); e.hasMoreElements();){
			String key = e.nextElement().toString();
			String val = overrides.getProperty(key);
			result.setProperty(key, val);
		}
		return result;
	}

	
	public void setProperty(String name, String value)
			throws UnsupportedOperationException {
		parent.setProperty(name, value);	
	}

	public void cleanup() {
		
		parent.cleanup();
		
	}

	public void addListener(ConfigServiceListener listener) {
		parent.addListener(listener);
		
	}

}