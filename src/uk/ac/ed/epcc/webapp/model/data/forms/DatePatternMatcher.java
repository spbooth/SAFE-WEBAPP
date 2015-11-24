// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: DatePatternMatcher.java,v 1.3 2014/09/15 14:30:31 spb Exp $")


public class DatePatternMatcher<T> implements SQLMatcher<T>{
	private boolean match_before;
    public DatePatternMatcher(boolean match_before){
    	this.match_before = match_before;
    }
    protected boolean matchBefore(){
    	return match_before;
    }
	
	public SQLFilter<T> getSQLFilter(Class<? super T> clazz,Repository res, String target, Object form_value) {
		Date d = (Date) form_value;
		if( matchBefore()){
			return new SQLValueFilter<T>(clazz,res,target,MatchCondition.LT,d);
		}else{
			return new SQLValueFilter<T>(clazz,res,target,MatchCondition.GE,d);
		}
		
	}
}