// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

public interface Matcher<T> {
	/** compare the target property and form value for use in the accept
	 * @param fac 
	 * @param target
	 * @param form_value
	 * @return AcceptFilter
	 */
	public <X extends DataObject> AcceptFilter<X>  getAcceptFilter(DataObjectFactory<X> fac, String target, T form_value);
	
}