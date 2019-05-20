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
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Objects that customize a creation form.
 * These can be a {@link CreateTemplate} or a {@link Composite} attached to the factory.
 * @author spb
 *
 */

public interface CreateCustomizer <BDO extends DataObject>{
	/**
	 * Perform target specific customisation of a creation Form. For example
	 * adding a special validator. Note that this is called in addition to the
	 * basic customiseForm call
	 * 
	 * @param f
	 *            Form to be modified
	 * @throws Exception 
	 */
	public void customiseCreationForm(Form f) throws Exception;
	
	/** Populate target from creation form.
	 * This can be sub-classes to add additional information such as the requesting Person
	 * or other information derived other than from the form parameters.
	 * Normally the form contents have already been set in the Object but the form is passed to this method in case any
	 * form parameters are used to control the way the object is created.
	 * 
	 * @param dat Object being created
	 * @param f {@link Form} providing parameters
	 * @throws DataException 
	 * @throws ActionException 
	 */
	public abstract void preCommit(BDO dat, Form f) throws DataException, ActionException;

	/** Take any action after object commit.
	 * 
	 * @param dat 
	 * @param f
	 * @throws Exception
	 */
	public abstract void postCreate(BDO dat, Form f) throws  Exception;

}