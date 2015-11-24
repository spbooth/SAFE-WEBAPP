// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;

/** Interface for types that have a view transition
 * 
 * @author spb
 *
 */
public interface Viewable {
	public ViewTransitionResult getViewTransition();
  
}