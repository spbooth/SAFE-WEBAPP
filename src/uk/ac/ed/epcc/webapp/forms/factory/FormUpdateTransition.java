// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
@uk.ac.ed.epcc.webapp.Version("$Id: FormUpdateTransition.java,v 1.3 2015/04/11 14:56:58 spb Exp $")


public class FormUpdateTransition<K,X> extends AbstractFormUpdateTransition<K, X> {

	private final FormUpdate<X> update;
	public FormUpdateTransition(String label,FormUpdate<X> update, TransitionProvider<K, X> tp,
			K next) {
		super(label, tp, next);
		this.update=update;
	}

	@Override
	public FormUpdate<X> getUpdate(AppContext c) {
		return update;
	}

}