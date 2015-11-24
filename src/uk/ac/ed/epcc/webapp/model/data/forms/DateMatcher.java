// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.TimeAcceptFilter;
/** FilterMatcher to compare against date fields.
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DateMatcher.java,v 1.3 2014/09/15 14:30:31 spb Exp $")

public class DateMatcher<T extends Date> extends DatePatternMatcher<T> implements FilterMatcher<T> {

	public DateMatcher(boolean match_before) {
		super(match_before);
	}


	public <X extends DataObject> AcceptFilter<X> getAcceptFilter(DataObjectFactory<X> fac,String target, T form_value) {
		if( matchBefore()){
			return new DataObjectFactory.TimeAcceptFilter(fac.getTarget(),target,MatchCondition.LT,form_value);
		}else{
			return new DataObjectFactory.TimeAcceptFilter(fac.getTarget(),target,MatchCondition.GE,form_value);
		}
	}

}