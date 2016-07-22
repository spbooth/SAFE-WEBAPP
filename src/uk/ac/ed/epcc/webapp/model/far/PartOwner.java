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
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;



/** abstract superclass for objects that can own part of a dynamic form.
 * @author spb
 *
 */

public abstract class PartOwner extends DataObject {

	/**
	 * @param r
	 */
	protected PartOwner(Record r) {
		super(r);
	}

	/** get the owning {@link DynamicForm}
	 * 
	 * @return the {@link DynamicForm}
	 */
	public abstract DynamicForm getForm();
	
	/** Get the local name for the object.
	 * 
	 * If the object has a parent this will be unique amongst siblings.
	 * 
	 * @return
	 */
	public abstract String getName();

	
	/** Get the qualified name for the object.
	 * 
	 * This will be the local name qualified by the name of its parent 
	 * (if any) resulting in a globally unique name.
	 * 
	 * @return
	 */
	public abstract String getQualifiedName();
	/** get a {@link FormResult} to view this object.
	 * 
	 * @return
	 */
	public abstract FormResult getViewResult();
	
	/** get the {@link PartOwnerFactory} corresponding to this {@link PartOwner}
	 * 
	 * @return {@link PartOwnerFactory}
	 */
	public abstract PartOwnerFactory getFactory();
}