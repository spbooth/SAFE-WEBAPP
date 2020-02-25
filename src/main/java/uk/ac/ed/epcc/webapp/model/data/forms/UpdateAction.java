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
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionValidationException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/**	
 * FormAction class to create the target DataObject from a form
 * 
 * @author spb
 * @param <BDO> type we are updating
 * 
 */


public class UpdateAction<BDO extends DataObject> extends FormAction {
	/**
 * 
 */
protected final UpdateTemplate<BDO> updater;
	protected BDO dat;
    protected String type_name;
	public UpdateAction(String type_name,UpdateTemplate<BDO> u, BDO dat) {
		updater = u;
		this.dat = dat;
		this.type_name=type_name;
	}

	@Override
	public FormResult action(Form f) throws ActionException {

		try {
			AppContext conn = dat.getContext();
			Map<String,Object> orig=dat.getMap();
			dat.formUpdate(f);
			preCommit(dat,f,orig);
			boolean changed = dat.commit();
			if (changed) {
				postUpdate(dat,f,orig);
			}
			return updater.getResult(type_name, dat, f);
			
		} catch (Exception e) {
			throw new ActionException("Update failed", e);
		}
	}
	public void postUpdate(BDO dat,Form f,Map<String,Object> orig) throws Exception {
		updater.postUpdate(dat,f,orig);
	}
	public void preCommit(BDO dat,Form f,Map<String,Object> orig) throws DataException, TransitionValidationException {
		updater.preCommit(dat, f, orig);
	}
	
	
}