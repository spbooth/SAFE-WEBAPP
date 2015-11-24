// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

public abstract class DataObjectFormEntry<T extends DataObject> extends IndexedFormEntry<DataObjectFactory<T>, T> {

	protected DataObjectFormEntry(String name, Class<? extends DataObjectFactory<T>> c,
			FormPolicy policy) {
		super(name, c, policy);
	}

}