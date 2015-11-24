// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;

public abstract class FormRegistry<T extends FormEntry> extends FormFactoryProviderRegistry<T>{

public FormRegistry(AppContext conn) {
		super(conn);
}

}