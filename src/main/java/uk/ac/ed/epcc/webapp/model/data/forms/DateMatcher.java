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