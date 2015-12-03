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

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Interfaces for use with an {@link UpdateAction}
 * This allows a standard action to be used with the customisation points moved into the enclosing class.
 * @see UpdateTransition
 * @see Updater
 * @author spb
 *
 * @param <BDO>
 */
public interface UpdateTemplate<BDO extends DataObject> {

	/** perform side-effects after a form update
	 * 
	 * 
	 * @param o object being updated
	 * @param f Form used for update
	 * @param orig Map of object state before update.
	 * @throws DataException
	 */
	public abstract void postUpdate(BDO o, Form f,Map<String,Object> orig) throws DataException;
	
	public AppContext getContext();
	
	/** select the {@link FormResult} after an update.
	 * 
	 * @param type_name
	 * @param dat
	 * @param f
	 * @return
	 */
	public abstract FormResult getResult(String type_name,BDO dat, Form f);
}