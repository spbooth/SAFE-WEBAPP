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

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** interface for objects that specify creation forms.
 * 
 * This is the interface targeted by the standard {@link CreateAction}
 * It also provides default implementations for methods in {@link CreateCustomizer}
 * which it extends.
 * @author Stephen Booth
 *
 * @param <BDO>
 */
public interface CreateTemplate<BDO extends DataObject> extends CreateCustomizer<BDO>{


	
	public abstract DataObjectFactory<BDO> getFactory();

    public default void customiseCompleteCreationForm(Form f) {
    	
    }
	default public String getActionName() {
		return "Create";
	}
    /** Override the text for the create button
	 * 
	 * @return object added as button content
	 */
	default public Object getActionText() {
		return null;
	}
	/** create the uncomitted blank object to be populated
	 * 
	 * @return
	 * @throws DataFault
	 */
	public default BDO makeObject() throws DataException {
		return getFactory().makeBDO();
	}
	public default void addActions(Form f) {
		f.addAction(getActionName(), new CreateAction<>(getActionText(),this));
	}
	@Override
	default public void postCreate(BDO dat, Form f) throws Exception {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.postCreate(dat, f);		
		}
		
	}

	@Override
	default public void preCommit(BDO dat, Form f) throws DataException, ActionException {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.preCommit(dat, f);
		}
	}
	/** should a confirm dialog be presented. 
	 * A null value means no confirm dialog.
	 * @param f
	 * @return confirm type or null 
	 */
	default public String getConfirm(Form f) {
		return null;
	}
	
	
	
	
	public abstract FormResult getResult(BDO dat, Form f);
	
	
	
	/** get the default values to use on creation.
	 * 
	 * @return MAp
	 */
	public Map<String,Object> getDefaults();
	
	
}