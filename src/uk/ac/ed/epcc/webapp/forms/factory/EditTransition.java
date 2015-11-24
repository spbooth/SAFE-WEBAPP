// Copyright - The University of Edinburgh 2011
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
import uk.ac.ed.epcc.webapp.session.SessionService;
/** transition that generates an update form based on a {@link EditFormBuilder}
 * 
 * @author spb
 *
 * @param <X>
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
			update.getContext().error(e,"Error updating object");
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