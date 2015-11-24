// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Interface for Inputs used to select DataObjects
 * 
 * @author spb
 *
 * @param <I> type of DataObject
 */
public interface DataObjectItemInput<I extends DataObject> extends Input<Integer>, ItemInput<I>,OptionalInput{
	public I getDataObject(); 
}