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
package uk.ac.ed.epcc.webapp.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;

/** Interface representing a set of create/update forms 
 * 
 * @see FormFactoryProviderRegistry
 * @author spb
 *
 * @param <T> type of target
 */
public interface FormFactoryProvider<T> extends FormPolicy, Comparable{
	/** generate a FormUpdate object appropriate to the specified user.
	 * If user specialisation is required the SessionService can be retrieved from 
	 * the AppContext
	 * @param c AppContext
	 * @return FormUpdate
	 * @throws Exception
	 */
	public FormUpdate<T> getFormUpdate(AppContext c) throws Exception;
	/** generate a FormCreator appropriate to the specified USer
	 * If user specialisation is required the SessionService can be retrieved from 
	 * the AppContext
	 * @param c AppContext
	 * @return FormCreator
	 * @throws Exception
	 */
	public FormCreator getFormCreator(AppContext c) throws Exception;
	/**Name of the object type as presented to the User
	 * 
	 * @return String name
	 */
	public String getName();
	/** Convert id string to target object
	 * This is needed in order to transfer the target between transitions
	 * @param c AppContext
	 * @param id
	 * @return T target object
	 */
	public T getTarget(AppContext c, String id);
	/** convert target object to id string.
	 * This is needed in order to transfer the target between transitions
	 * 
	 * @param target
	 * @return unique id-string
	 */
	public String getID(T target);
}