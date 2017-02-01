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
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An Update Transition that extends DataObjectFormFactory directly rather than 
 * including a nested Updater. 
 * 
 * @author spb
 *
 * @param <BDO>
 */
public abstract class UpdateTransition<BDO extends DataObject> extends DataObjectUpdateFormFactory<BDO> implements FormTransition<BDO>, UpdateTemplate<BDO>{

	public static final String UPDATE = " Update ";
	private final String name;
	protected UpdateTransition(String name,DataObjectFactory<BDO> fac) {
		super(fac);
		this.name=name;
	}

	public final String getName(){
		return name;
	}
	
	public final void buildForm(Form f, BDO dat, AppContext conn)
			throws TransitionException {
		try{
		  buildUpdateForm(getName(), f, dat, conn.getService(SessionService.class));
		}catch(Exception e){
			getLogger().error("Error making update transition form",e);
			throw new TransitionException("Internal Error in update");
		}
	}

	public final FormResult getResult(TransitionVisitor<BDO> vis)
			throws TransitionException {
		return vis.doFormTransition(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
	 */
	@Override
	public void preCommit(BDO dat, Form f, Map<String, Object> orig) throws DataException {
		
		
	}

	
}