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



/** Type of filter that specifies the SQL using  parameterised query
 * 
 * These should implement as working {@link #hashCode()} and {@link #equals(Object)}
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface PatternFilter<T> extends BaseSQLFilter<T> {
	

	/** Add parameters for this filter to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list);

	/**
	 * get a Parameterised selection SQL clause
	 * @param sb StringBuilder to modify
	 * 
	 * @see PreparedStatement
	 * @param qualify request field names to be qualified with table name.
	 * @return modified StringBuilder
	 */
	public StringBuilder addPattern(StringBuilder sb,boolean qualify);
	
	
}