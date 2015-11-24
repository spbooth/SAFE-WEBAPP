// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
@uk.ac.ed.epcc.webapp.Version("$Id: StandAloneFormUpdateTransition.java,v 1.3 2015/04/11 14:56:58 spb Exp $")


public class StandAloneFormUpdateTransition<T> extends EditTransition<T> {
    private final StandAloneFormUpdate<T> update;
    public StandAloneFormUpdateTransition(String type_name,StandAloneFormUpdate<T> update){
    	super(type_name);
    	this.update=update;
    }
	@Override
	public StandAloneFormUpdate<T> getUpdate(AppContext c,T dat) {
		return update;
	}

}