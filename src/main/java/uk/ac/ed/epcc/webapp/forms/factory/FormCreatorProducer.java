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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Interface for objects that create {@link FormCreator}s
 * 
 * @author spb
 *
 */
public interface FormCreatorProducer {
	/** Create a default FormCreator for the target object
	 * 
	 * The form may be customised according to the requesting AppUser
	 * @param c AppContext
	 * @return FormCreator or null;
	 */
  public FormCreator getFormCreator(AppContext c);
  /** Can the producer provide a FormUpdate for the current user.
   * This is intended to reflect a functional inability to generate the
   * FormUpdate as access control is handled by the {@link FormPolicy}
   * 
   * @param p
   * @return true if current user allowed to create objects
   */
   default public boolean canCreate(SessionService p) {
	   return true;
   }
}