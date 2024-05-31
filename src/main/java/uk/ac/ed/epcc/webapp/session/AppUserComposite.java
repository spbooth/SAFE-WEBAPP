//| Copyright - The University of Edinburgh 2017                            |
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

import uk.ac.ed.epcc.webapp.model.data.Composite;

/** A superclass for {@link Composite}s on The {@link AppUserFactory}
 * 
 * The only purpose of this class is to make the class hierarchy easier to navigate
 * @author spb
 * @param <AU> type of {@link AppUser}
 * @param <X> registration type
 *
 */
public abstract class AppUserComposite<AU extends AppUser,X extends AppUserComposite> extends Composite<AU, X> {

	/**
	 * @param fac
	 */
	protected AppUserComposite(AppUserFactory<AU> fac,String tag) {
		super(fac,tag);
	}

	public AppUserFactory<AU> getAppUserFactory(){
		return (AppUserFactory<AU>) getFactory();
	}
}
