// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Interface for {@link FormTransition} or {@link TargetLessTransition}s 
 * that provide additional content
 * together with the Form
 * 
 * @author spb
 * @param <O> target object of transition
 *
 */

public interface ExtraContent<O> {	 
	public <X extends ContentBuilder> X getExtraHtml(X cb,SessionService<?> op, O target);
}