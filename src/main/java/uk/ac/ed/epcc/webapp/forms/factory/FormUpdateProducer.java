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
/** A factory class that can create {@link FormUpdate} objects for editing its targets
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public interface FormUpdateProducer<T> {
	/** produce a FormUpdate for the target type appropriate for
	 * the requesting user
	 * 
	 * @param c AppContext
	 * @return FormUpdate or null
	 */
  public FormUpdate<T> getFormUpdate(AppContext c);
  
  /** Can the producer provide a FormUpdate for the current user.
   * This is intended to reflect a functional inability to generate the
   * FormUpdate as access control is handled by the {@link FormPolicy}
   * 
   * @param c
   * @return true if current user can update
   */
  public boolean canUpdate(SessionService c);
}