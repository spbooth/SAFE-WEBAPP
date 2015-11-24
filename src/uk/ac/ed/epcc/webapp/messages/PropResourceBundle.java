// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.messages;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/** A {@link ResourceBundle} implemented as a {@link Properties} collection.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PropResourceBundle.java,v 1.3 2015/06/23 15:29:14 spb Exp $")
public class PropResourceBundle extends ResourceBundle {

	private final Properties props;
	
	/**
	 * 
	 */
	public PropResourceBundle(Properties p) {
		this.props=p;
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#getKeys()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getKeys() {
		return props.keys();
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	@Override
	protected Object handleGetObject(String key) {
		return props.getProperty(key);
	}

}
