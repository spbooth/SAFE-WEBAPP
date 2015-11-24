// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
/** Interface for objects that can augment the table {@link Transition}s of a
 * {@link TableTransitionTarget}.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface TransitionSource<T extends TableTransitionTarget> {
	/** Generate a {@link Map} of {@link Transition}s to be added to the
	 * table transitions of the {@link TableTransitionTarget}. 
	 * 
	 * @return
	 */
	public Map<TransitionKey<T>,Transition<T>> getTransitions();

}