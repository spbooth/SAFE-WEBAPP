// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;



/** A Transition Provider that can use the generic view_target pages.
 * These show a html summary of the object and give buttons to invoke the possible
 * transitions on the target.
 * 
 * @author spb
 *
 * @param <K> key type
 * @param <T> target type
 */
public interface ViewTransitionProvider<K, T> extends ViewTransitionFactory<K, T>,TransitionProvider<K, T> {
	
}