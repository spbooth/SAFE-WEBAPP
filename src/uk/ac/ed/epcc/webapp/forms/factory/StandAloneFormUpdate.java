// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

/** Classes that implement this interface build a single update form to select then edit the object.
 * 
 * @author spb
 *
 * @param <T> Type of target object
 */
public interface StandAloneFormUpdate<T> extends FormUpdate<T>, EditFormBuilder<T> {
}