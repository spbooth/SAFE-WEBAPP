//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLValue} that wraps a different one
 * 
 * It includes default methods to handle any nested {@link GroupingSQLValue}.
 * @author Stephen Booth
 * @param <T> type of {@link SQLValue}
 * @param <N> type of nested {@link SQLValue}
 *
 */
public interface NestedSQLValue<T,N> extends SQLValue<T>, GroupingSQLValue<T> {

	@Override
	default SQLFilter getRequiredFilter() {
		return getNested().getRequiredFilter();
	}

	@Override
	default int addGroup(StringBuilder sb, boolean qualify)  {
		GroupingSQLValue<N> n = (GroupingSQLValue<N>) getNested();

		return n.addGroup(sb, qualify);

	}

	@Override
	default List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		SQLValue<N> n = getNested();
		if( n instanceof GroupingSQLValue) {
			return ((GroupingSQLValue)n).getGroupParameters(list);
		}
		return getParameters(list);
	}

	public SQLValue<N> getNested();

	@Override
	default boolean checkContentsCanGroup() {
		return getNested() instanceof GroupingSQLValue;
	}
}
