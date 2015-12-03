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
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
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
    private final String type_name;
	/**
	 * @param type_name User presetned name of type being created.
	 * @param dataObjectFactory
	 */
	public CreateAction(String type_name,CreateTemplate<BDO> dataObjectFactory) {
		creator = dataObjectFactory;
		this.type_name=type_name;
	}

	@Override
	public FormResult action(Form f) throws ActionException {
        DataObjectFactory<BDO> factory = creator.getFactory();
		Logger log = factory.getLogger();
        
		BDO o = null;
		log.debug("In create action");
		try {
			// populate as a record in case factory is polymorphic and needs form parameters to create object.
			Repository.Record rec = factory.makeRecord();
			// we may have default values supressed in the form
			Map<String,Object> defs = factory.getDefaults();
			if( defs != null){
				log.debug("set default contents");
				rec.putAll(defs);
			}
			rec.putAll(f.getContents());
			o = (BDO) factory.makeBDO(rec);
			log.debug("set form contents");
			preCommit(o,f);
			log.debug("commit");
			o.commit();
			log.debug("postCreate");
			postCreate(o,f);
		} catch (Exception e) {
			log.error("exception in CreateAction.action");
			throw new ActionException("Create failed", e);
		}
		return creator.getResult(type_name,o, f);
		
	}
	public final void preCommit(BDO dat, Form f) throws DataException {
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
   
}