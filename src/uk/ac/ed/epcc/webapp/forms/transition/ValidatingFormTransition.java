// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;


/** The same as a {@link FormTransition} except that the 
 * the initial state of the form is validated and errors displayed
 * rather than waiting for the form to be submitted.
 * 
 * @author spb
 * @param <X> type of object transition is on
 *
 */
public  interface ValidatingFormTransition<X> extends BaseFormTransition<X>{
	
}