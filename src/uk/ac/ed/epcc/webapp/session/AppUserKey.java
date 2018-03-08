//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

/**
 * @author spb
 *
 */

public abstract class AppUserKey extends TransitionKey<AppUser> {
	private final String button_text;
	/**
	 * @param name
	 * @param help
	 */
	public AppUserKey(String name, String help) {
		super(AppUser.class, name, help);
		button_text=null;
	}

	public AppUserKey(String name, String text,String help) {
		super(AppUser.class, name, help);
		button_text=text;
	}
	/**
	 * @param t
	 * @param name
	 */
	public AppUserKey( String name) {
		super(AppUser.class, name);
		button_text=null;
	}
	
	public abstract boolean allow(AppUser user,SessionService op);

	/** get the button text to use
	 * 
	 * @return
	 */
	public String getText() {
		if( button_text != null) {
			return button_text;
		}
		return toString();
	}
}