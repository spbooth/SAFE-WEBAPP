// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
@uk.ac.ed.epcc.webapp.Version("$Id: ViewTableResult.java,v 1.2 2014/09/15 14:30:27 spb Exp $")


public class ViewTableResult extends ViewTransitionResult<TableTransitionTarget, TransitionKey> {

	public ViewTableResult(TableTransitionTarget target) {
		super(new TableTransitionProvider(target.getContext()),target);
	}
   
}