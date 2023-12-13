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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate;

/**
 * {@link FormAction} class to create the target {@link DataObject} from a form
 * 
 * This class needs access to package level methods from {@link DataObjectFactory}.
 * @author spb
 * @param <BDO> type we are creating
 * 
 */


public final class CreateAction<BDO extends DataObject> extends FormAction {

	
	/**
	 * 
	 */
	private final CreateTemplate<BDO> creator;
    private final Object text;
	/**
	 * @param dataObjectFactory
	 */
	public CreateAction(CreateTemplate<BDO> dataObjectFactory) {
		this(null,dataObjectFactory);
	}
	public CreateAction(Object text,CreateTemplate<BDO> dataObjectFactory) {
		creator = dataObjectFactory;
		this.text=text;
	}

	@Override
	public FormResult action(Form f) throws ActionException {
        DataObjectFactory<BDO> factory = creator.getFactory();
		Logger log = factory.getLogger();
        
		BDO o = null;
		log.debug("In create action");
		try {
			// populate as a record in case factory is polymorphic and needs form parameters to create object.
			o = creator.makeObject(f);
			// we may have default values supressed in the form
			Map<String,Object> defs = creator.getCreationDefaults();
			if( defs != null){
				log.debug("set default contents");
				o.setContents(defs);
			}
			o.formUpdate(f);
			preCommit(o,f);
			log.debug("commit");
			o.commit();
			// commit transaction to ensure new objet visible as soon as possible
			DatabaseService db = factory.getContext().getService(DatabaseService.class);
			db.commitTransaction();
			
			
			log.debug("postCreate");
			postCreate(o,f);
		}catch(ActionException ae) {
			throw ae; // allow preCommit to throw action exception directly
		} catch (Exception e) {
			log.error("exception in CreateAction.action",e);
			throw new ActionException("Create failed", e);
		}
		return creator.getResult(o, f);
		
	}
	public final void preCommit(BDO dat, Form f) throws DataException, ActionException {
		creator.preCommit(dat, f);
	}
	public void postCreate(BDO dat, Form f) throws Exception {
		creator.postCreate(dat,f);
	}
	public String getConfirm(Form f) {
		String res = super.getConfirm(f);
		if( res == null ){
			res = creator.getConfirm(f);
		}
		return res;
	}
	@Override
	public Object getText() {
		return text;
	}
}