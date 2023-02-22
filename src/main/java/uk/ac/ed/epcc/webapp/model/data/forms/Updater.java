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

import uk.ac.ed.epcc.webapp.editors.xml.DomTransitionProvider.EditTransition;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.UpdateContributor;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;

/** default {@link FormUpdate} for DataObjects
 * 
 * @see UpdateTransition
 * @see EditTransition
 * @author spb
 * @param <BDO> type we are updating
 *
 */


public class Updater<BDO extends DataObject> extends DataObjectUpdateFormFactory<BDO> implements StandAloneFormUpdate<BDO>, UpdateTemplate<BDO>{

	
	/** Form key used for object selections
	 * 
	 */
	public static final String TARGET = "Target";
	/**
	 * @param dataObjectFactory
	 */
	public Updater(DataObjectFactory<BDO> dataObjectFactory) {
		super(dataObjectFactory);
	}

	public void buildSelectForm(Form f, String label, BDO dat) {
		Input<Integer> i = getSelectInput();

		f.addInput(TARGET, label, i);
		if (dat != null && factory.isMine(dat)) {
		    try {
				i.setValue(Integer.valueOf(dat.getID()));
			} catch (TypeException e) {
				throw new TypeError(e);
			}
		}
	}

	public DataObjectItemInput<BDO> getSelectInput() {
		return factory.getInput();
	}
	@SuppressWarnings("unchecked")
	public BDO getSelected(Form f) {
		//DataObjectItemInput<BDO> i = (DataObjectItemInput<BDO>) f.getInput(TARGET);
		return (BDO) f.getItem(TARGET);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
	 */
	@Override
	public void preCommit(BDO dat, Form f, Map<String, Object> orig) throws DataException {
		for(UpdateContributor<BDO> comp : getFactory().getComposites(UpdateContributor.class)) {
			comp.preCommit(dat, f, orig);
		}
		
	}
	
}