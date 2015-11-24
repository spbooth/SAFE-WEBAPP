// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
@uk.ac.ed.epcc.webapp.Version("$Id: FormCreatorTransition.java,v 1.2 2014/09/15 14:30:17 spb Exp $")


public class FormCreatorTransition<X> extends CreatorTransition<X> {
    private final FormCreator creator;
    public FormCreatorTransition(String type_name,FormCreator creator){
    	super(type_name);
    	this.creator=creator;
    }
	@Override
	public FormCreator getCreator(AppContext c) {
		return creator;
	}

}