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

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.Repository;



/** Type of filter that specifies the SQL using  parameterised query
 * 
 * These should implement as working {@link #hashCode()} and {@link #equals(Object)}
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface PatternFilter<T> extends BaseSQLFilter<T> {
	

	@Override
	default <X> X acceptVisitor(FilterVisitor<X, T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}

	/** Add parameters for this filter to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list);

	/**
	 * get a Parameterised selection SQL clause
	 * 
	 * The <b>tables</b> parameter is only needed by nested sub-queries (e.g. EXISTS) so they can
	 * supress unwanted joins back the the outer query.
	 * 
	 * @param tables Set of {@link Repository}s in the SQL 
	 * @param sb StringBuilder to modify
	 * @param qualify request field names to be qualified with table name.
	 * @see PreparedStatement
	
	 * @return modified StringBuilder
	 */
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify);
	
	
}