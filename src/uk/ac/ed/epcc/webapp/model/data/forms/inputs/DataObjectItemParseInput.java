// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

public interface DataObjectItemParseInput<I extends DataObject> extends DataObjectItemInput<I>,
		ParseInput<Integer> {

}