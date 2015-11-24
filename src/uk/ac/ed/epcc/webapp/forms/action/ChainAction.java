// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
/** Action that executes a nested DirectTransition
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ChainAction.java,v 1.2 2014/09/15 14:30:17 spb Exp $")

public class ChainAction<T> extends FormAction{
	private final DirectTransition<T> transition;
	private final T target;
	private final AppContext c;
	public ChainAction(T target, AppContext c,DirectTransition<T> transition){
		this.target=target;
		this.c=c;
		this.transition=transition;
	}
	@Override
	public FormResult action(Form f)
			throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
		return transition.doTransition(target, c);
	}
	
}