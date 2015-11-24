// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Classes that implement this interface don't provide a form for the update
 * operation but generate the FormResult directly from the select form.
 * Typically this might be used to return a ChainedTransitionResult for a view
 * transition on the selected object
 * 
 * @author spb
 * @param <T> target type
 */
public interface DirectFormUpdate<T> extends FormUpdate<T> {
	/** Generate the FormResult based on the selected object.
	 * 
	 * @param target
	 * @return FormResult
	 */
	public FormResult getNext(T target);
	
}