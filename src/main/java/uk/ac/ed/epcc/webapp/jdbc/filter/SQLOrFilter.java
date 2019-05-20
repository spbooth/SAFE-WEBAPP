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
package uk.ac.ed.epcc.webapp.jdbc.filter;


/**
 * Combine SQL filters using an OR expression
 * Its only safe to use an OR combination with pure SQL 
 * @author spb
 * @param <T> type of object selected
 * 
 */


public class SQLOrFilter<T> extends BaseSQLCombineFilter<T> {
	
	
	public SQLOrFilter(Class<T> target) {
		super(target);
	}
	public SQLOrFilter(Class<T> target,SQLFilter<? super T> ...filters ){
		super(target);
		for(SQLFilter<? super T> f : filters){
			addFilter(f);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseCombineFilter#getFilterCombiner()
	 */
	@Override
	protected FilterCombination getFilterCombiner() {
		return FilterCombination.OR;
	}
	

	
}