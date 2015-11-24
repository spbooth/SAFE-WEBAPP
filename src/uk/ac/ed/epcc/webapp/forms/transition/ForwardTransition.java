// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/

package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
/** direct transition that returns a nested result
 * 
 * @author spb
 *
 * @param <T> target type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ForwardTransition.java,v 1.3 2014/12/10 15:43:29 spb Exp $")

public class ForwardTransition<T> extends AbstractDirectTransition<T>{
	private final FormResult result;
	
	public ForwardTransition(FormResult result){
		this.result=result;
	}

	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		return result;
	}
	
}