//| Copyright - The University of Edinburgh 2014                            |
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

/** A variant of {@link SQLValue} which is legal to use for group-by clauses.
 * This allows the class to generate an alternative
 * SQL fragment. This allows complex transformations
 * that do not change the grouping result to be optimised. Not all {@link SQLValue}s can be used
 * to implement a group-by so this interface needs to be implemented explicitly though there is a default
 * implementation that works for most simple cases.
 * 
 * If the SQL representation of the {@link SQLValue} is a literal constant it
 * is required to implement this to suppress the group-by clause as literal constants 
 * are illegal in group-by clauses but won't actually change the grouping.
 * 
 * 
 * @author spb
 * @see SQLGroupMapper
 * @param <T> type of {@link SQLValue}
 *
 */

public interface GroupingSQLValue<T> extends SQLValue<T> {

	/** Add the group-by clause to a query.
	 * Note this can be a null operation in which case it
	 * will return zero.
	 * 
	 * @param sb
	 * @param qualify
	 * @return actual number of fields added
	 * @throws CannotGroupException 
	 */
	default public int addGroup(StringBuilder sb,boolean qualify) {
		return add(sb, qualify);
	}
	/** Get the parameters for a group-by clause.
	 * 
	 * @param list
	 * @return modified list
	 */
	default public List<PatternArgument> getGroupParameters(List<PatternArgument> list){
		return getParameters(list);
	}
	/** return false if this instance is not able to perform grouping.
	 * 
	 * @return
	 */
	default public boolean checkContentsCanGroup() {
		return true;
	}
	
	/** Is there always a one-to-one mapping between groups and values
	 * 
	 * @return
	 */
	default public boolean groupingIsomorphic() {
		return false;
	}
}