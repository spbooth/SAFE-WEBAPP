// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/

package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.ChainAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** FormTransition that selects a choice of two DirectTransitions as
 * a result of a yes/no question
 * @see ForwardTransition
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConfirmTransition.java,v 1.3 2014/09/15 14:30:22 spb Exp $")

public class ConfirmTransition<T> implements ExtraFormTransition<T>{
	private final DirectTransition<T> yes_transition;
	private final DirectTransition<T> no_transition;
	private final String name;
	public ConfirmTransition(String name, DirectTransition<T> yes, DirectTransition<T> no){
		this.name=name;
		this.yes_transition=yes;
		this.no_transition=no;
	}
	public void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		f.addAction("Yes", new ChainAction<T>(target,c,yes_transition));
		f.addAction("No", new ChainAction<T>(target,c,no_transition));
		
		
	}
	public <X extends ContentBuilder> X getExtraHtml(X cb,SessionService<?> op, T target) {
		ExtendedXMLBuilder text = cb.getText();
		text.clean(name);
		text.appendParent();
		return cb;
	}
	public FormResult getResult(TransitionVisitor<T> vis) throws TransitionException {
		return vis.doFormTransition(this);
	}
}