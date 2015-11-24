// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.preferences;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.preferences.UserSettingFactory.UserSetting;

/** A {@link Preference} is a {@link Feature} that only affects presentation logic so a user is allowed to
 * override the system-wide value with their own setting.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Preference.java,v 1.3 2014/09/15 14:30:34 spb Exp $")
public class Preference extends Feature {

	/**
	 * @param name
	 * @param def
	 * @param description
	 */
	public Preference(String name, boolean def, String description) {
		super(name, def, description);
	}

	@Override
	public boolean isEnabled(AppContext conn) {
		// Preference queries may occur often so cache the result in the context
		Boolean b = (Boolean) conn.getAttribute(this);
		if (b == null) {
			UserSettingFactory<UserSetting> fac = new UserSettingFactory<UserSetting>(conn);
			b = new Boolean(fac.getPreference(this));
			conn.setAttribute(this, b);
		}
		return b.booleanValue();
	}

	public boolean defaultSetting(AppContext conn){
		return super.isEnabled(conn);
	}
	
}
