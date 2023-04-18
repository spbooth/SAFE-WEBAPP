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

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.content.UIProvider;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory;

/** A TargetLessTransition for creating DataObjects.
 * This class extends DataObjectFromFactory directly rather than wrapping a Creator object
 * making it easier to customise.
 * @author spb
 * @see Creator
 *
 * @param <BDO>
 */
public abstract  class CreateTransition<BDO extends DataObject> extends DataObjectFormFactory<BDO> implements TargetLessTransition<BDO>, CreateTemplate<BDO>{
    private final String name;
	protected CreateTransition(String name,DataObjectFactory<BDO> fac) {
		super(fac);
		this.name=name;
	}

	@Override
	public void buildForm(Form f, AppContext c) throws TransitionException {
		try {
			
			if( buildForm(f,getInitialFixtures())) {
				f.addAction("Create", new CreateAction<>(name, getActionText(),this));
			}
			customiseCreationForm(f);
			for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
				comp.customiseCreationForm(f);		
			}
		} catch (Exception e) {
			getContext().error(e,"Error creating object");
			throw new TransitionException("Error creating object");
		}
		
	}
	/** Override the text for the create button
	 * 
	 * @return object added as button content
	 */
	public Object getActionText() {
		return null;
	}

	@Override
	public FormResult getResult(TransitionVisitor<BDO> vis)
			throws TransitionException {
		return vis.doTargetLessTransition(this);
	}

	
	
	@Override
	public void postCreate(BDO dat, Form f) throws Exception {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.postCreate(dat, f);		
		}
		
	}

	@Override
	public void preCommit(BDO dat, Form f) throws DataException, ActionException {
		for(CreateCustomizer comp : getFactory().getComposites(CreateCustomizer.class)){
			comp.preCommit(dat, f);
		}
	}
	@Override
	public String getConfirm(Form f) {
		return null;
	}
	/**
	 * Perform target specific customisation of a creation Form. For example
	 * adding a special validator. Note that this is called in addition to the
	 * basic customiseForm call
	 * 
	 * @param f
	 *            Form to be modified
	 * @throws Exception 
	 */
	@Override
	public void customiseCreationForm(Form f) throws Exception {

	}
	
	public HashMap getInitialFixtures() {
		return null;
	}
}