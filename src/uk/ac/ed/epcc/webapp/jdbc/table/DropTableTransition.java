// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
@uk.ac.ed.epcc.webapp.Version("$Id: DropTableTransition.java,v 1.3 2014/12/10 15:43:29 spb Exp $")


public class DropTableTransition<T extends TableTransitionTarget> extends
		AbstractDirectTransition<T> {
	AppContext conn;
	public DropTableTransition(AppContext conn){
		this.conn=conn;
	}
	public FormResult doTransition(T target,
			AppContext c) throws TransitionException {
		DataBaseHandlerService serv = conn.getService(DataBaseHandlerService.class);
		try{
		if( serv != null ){
			serv.deleteTable(target.getTableTransitionID());
			return new TableListResult();
		}
		}catch(Exception e){
			conn.error(e,"Error dropping table");
		}
		return new MessageResult("internal_error");
	}

}