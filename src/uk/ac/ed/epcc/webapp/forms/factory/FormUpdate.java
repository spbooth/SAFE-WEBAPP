// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.forms.Form;


/**
 * FormUpdate is implemented by a classes that support Form based editing on
 * objects. An update operation is one where an existing Object is selected then
 * edited with a Form. This can also cover other actions on existing objects
 * like retire.
 * <p>
 * The FormUpdate interface defines a selection form for selecting the object to edit. 
 * How the object is actually updated is handled by
 * sub interfaces 
 * @see StandAloneFormUpdate
 * @see DirectFormUpdate
 * <p> 
 *
 * @author spb
 * @param <T> type of object being updated
 * 
 */
public interface FormUpdate<T> extends FormFactory{
	/**
	 * Build a form to select object to edit
	 * 
	 * @param f
	 *            Form to build
	 * @param label
	 *            label to use for selector
	 * @param dat
	 *            default Object to edit if we know it already
	 */
	public void buildSelectForm(Form f, String label, T dat);
	/**
	 * read the Object to edit out of the validated Selector form.
	 * 
	 * @param f
	 * @return Object to edit
	 */
	public T getSelected(Form f);
	
	
	
	
}