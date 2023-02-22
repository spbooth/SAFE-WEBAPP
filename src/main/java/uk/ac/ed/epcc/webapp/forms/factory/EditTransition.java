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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** transition that generates an update form based on a {@link EditFormBuilder}
 * 
 * @author spb
 *
 * @param <X> type of object edited
 */
public abstract class EditTransition<X> implements
		ValidatingFormTransition<X> , ExtraContent<X>{
	private String type_name;
	public EditTransition(String type_name){
		this.type_name=type_name;
	}
	public void buildForm(Form f,X dat, AppContext c) throws TransitionException {
		EditFormBuilder<X> update = getUpdate(c,dat);
		try {
			update.buildUpdateForm(type_name,f, dat,c.getService(SessionService.class));
		} catch (Exception e) {
			update.getContext().getService(LoggerService.class).getLogger(getClass()).error("Error updating object",e);
			throw new TransitionException("Update failed");
		}
	}

	public abstract EditFormBuilder<X> getUpdate(AppContext c, X dat);
	public final FormResult getResult(TransitionVisitor<X> vis)
			throws TransitionException {
		return vis.doValidatingFormTransition(this);
	}
	public <C extends ContentBuilder> C getExtraHtml(C cb,
			SessionService<?> op, X target) {
		EditFormBuilder<X> update = getUpdate(op.getContext(),target);
		if( update instanceof ExtraContent){
			return (C) ((ExtraContent)update).getExtraHtml(cb, op, target);
		}
		
		return cb;
	}

}