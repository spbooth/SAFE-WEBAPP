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
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Like Selector this interface means the implementing class can provide Form Inputs.
 * In this case the Input is customised for the AppUser requesting the form.
 * Available selections may be restricted to those where the AppUser has a specified role 
 * with respect to the target objects. How this role is defined is implementation specific. 
 * 
 * @see Selector
 * @author spb
 *
 * @param <B> Type of object relationship is for.
 */
public interface RoleSelector<B extends DataObject> {

	/** Get the Input where the AppUser has the specified role with 
	 * respect to the Inputs Target
	 * 
	 * @param role String specifying the role
	 * @param user person making request
	 * @return Input
	 */
	 DataObjectItemInput<B> getInput(String role, SessionService user);
	 
	 /** Are there any target objects where the user has the target role.
	  * 
	  * If this returns false the corresponding input has no legal values
	  * for the specified user.
	  * 
	  * @param role
	  * @param user
	  * @return boolean
	  */
	 boolean hasRole(String role, SessionService user);
	 
	 /** Does this sesssion have  the desired role on the target
	  * 
	  * @param sess 
	 * @param target
	  * @param role
	  * @return
	  */
	 public abstract boolean hasRole(SessionService sess,B target, String role);
		

	 
	 /** get the {@link DataObjectFactory} for the target type
	  * 
	  * @return {@link DataObjectFactory}
	  */
	public DataObjectFactory<B> getTargetFactory();
}