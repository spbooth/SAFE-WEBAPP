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

import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;
import uk.ac.ed.epcc.webapp.model.data.TableStructureContributer;

/** Default FormCreator for this Factory
 * 
 * @author spb
 * @see CreateTransition
 * @param <BDO> type we are creating
 *
 */


public class Creator<BDO extends DataObject> extends DataObjectFormFactory<BDO> implements FormCreator, CreateTemplate<BDO>{
	/**
	 * @param dataObjectFactory
	 */
	public Creator(DataObjectFactory<BDO> dataObjectFactory) {
		super(dataObjectFactory);
		
	}
	@Override
	public void buildCreationForm(String type_name,Form f) throws Exception {
		if( buildForm(f) ) {
			setAction(type_name,f);
		}
		customiseCreationForm(f);
		for(TableStructureContributer comp : getFactory().getTableStructureContributers()){
			if( comp instanceof CreateCustomizer){
				((CreateCustomizer)comp).customiseCreationForm(f);
			}
		}
	}
    public void setAction(String type_name,Form f) {
    	f.addAction(" Create ", new CreateAction<>(type_name,getActionText(),this));
    }
    /** Override the text for the create button
	 * 
	 * @return object added as button content
	 */
	public Object getActionText() {
		return null;
	}
	/**
	 * Perform target specific customisation of a creation Form. For example
	 * adding a special validator. Note that this is called in addition to the
	 * basic {@link #customiseForm(Form)} call
	 * 
	 * @param f
	 *            Form to be modified
	 * @throws Exception 
	 */
	@Override
	public void customiseCreationForm(Form f) throws Exception {

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#preCommit(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	@Override
	public  void preCommit(BDO dat, Form f) throws DataException, ActionException {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.preCommit(dat, f);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.CreateTemplate#postCreate(BDO, uk.ac.ed.epcc.webapp.model.data.forms.Form)
	 */
	@Override
	public void postCreate(BDO dat, Form f) throws Exception {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.postCreate(dat, f);
		}
	}
	
	@Override
	public FormResult getResult(String type_name,BDO dat, Form f) {
		Object thing = type_name;
		if( dat instanceof UIGenerator || dat instanceof UIProvider || dat instanceof Identified) {
			thing = dat;
		}
		MessageResult res = new MessageResult("object_created",type_name,thing);
		
		return res;
	}
	@Override
	public String getConfirm(Form f) {
		return null;
	}

}