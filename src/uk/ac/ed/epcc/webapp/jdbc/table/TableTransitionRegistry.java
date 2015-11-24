// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Set;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

public interface TableTransitionRegistry {
	/** Access control method
	 * 
	 * @param name
	 * @param operator
	 * @return boolean true if operation allowed
	 */
	public boolean allowTableTransition(TransitionKey name, SessionService operator);

	/** get content to be shown in transition form.
	 * @param hb 
	 * 
	 * @param operator
	 */
	public void getTableTransitionSummary(ContentBuilder hb,SessionService operator);

	/** lookup Transition using key
	 * 
	 * @param name TransitionKey
	 * @return Transition
	 */
	public Transition<TableTransitionTarget> getTableTransition(TransitionKey name);

	/** What operations are supported.
	 * 
	 * @return Set of TransitionKey
	 */
	public Set<TransitionKey> getTableTransitionKeys();

}