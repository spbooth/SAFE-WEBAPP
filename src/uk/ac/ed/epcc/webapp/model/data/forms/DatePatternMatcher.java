//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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