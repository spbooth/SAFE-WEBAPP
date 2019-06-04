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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.data.filter.BackJoinFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.JoinerFilter;

/**
 * Combine SQL filters using an OR expression
 * Its only safe to use an OR combination with pure SQL 
 * @author spb
 * @param <T> type of object selected
 * 
 */


public class SQLOrFilter<T> extends BaseSQLCombineFilter<T> {
	public static final Feature MERGE_BACK_JOIN = new Feature("filter.sqlorfilter.merge_back_join", true, "Merge multiple back join filters in OR combination");
	// For OR combinations we can merge back joins into a single exists clause
	// only works for OR as it makes no difference if branches are satisfied in the same
	// object or not only one branch needs to evaluate to true.
	private Map<JoinerFilter,SQLOrFilter> back_joins=new LinkedHashMap<JoinerFilter, SQLOrFilter>();
	
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
	@Override
	protected void addBackJoinFilter(BackJoinFilter filter) {
		if( MERGE_BACK_JOIN.isEnabled(filter.getContext())) {
			JoinerFilter link = filter.getLink();
			SQLOrFilter group = back_joins.get(link);
			if( group == null ) {
				group=new SQLOrFilter<T>(link.getTarget());
				back_joins.put(link, group);
			}
			group.addFilter(filter.getFil());
		}else{
			// Fallback to just treating as a pattern filter
			addPatternFilter(filter);
		}
	}
	@Override
	protected void listContents(StringBuilder sb) {
		super.listContents(sb);
		if( ! back_joins.isEmpty()) {
			sb.append(" back_joins=");
			sb.append(back_joins.toString());
		}
	}
	@Override
	public LinkedHashSet<PatternFilter> getPatternFilters() {
		LinkedHashSet<PatternFilter> set = super.getPatternFilters();
		for(Entry<JoinerFilter, SQLOrFilter> e : back_joins.entrySet()) {
			SQLOrFilter value = e.getValue();
			JoinerFilter key = e.getKey();
			if( value.size() == 1) {
				// remove redundant wrapper
				set.add(new BackJoinFilter(getTarget(), key, (SQLFilter) value.getSet().iterator().next()));
			}else {
				set.add(new BackJoinFilter(getTarget(), key, value));
			}
		}
		return set;
	}
	
	@Override
	public int size() {
		return super.size()+back_joins.size();
	}
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && back_joins.isEmpty();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((back_joins == null) ? 0 : back_joins.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SQLOrFilter other = (SQLOrFilter) obj;
		if (back_joins == null) {
			if (other.back_joins != null)
				return false;
		} else if (!back_joins.equals(other.back_joins))
			return false;
		return true;
	}
	

	
}