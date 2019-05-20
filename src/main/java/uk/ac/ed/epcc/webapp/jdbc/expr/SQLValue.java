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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** An object that can create a value from a SQL fragment.
 * We encode both the SQL
 *  fragment used in the select and the java code needed to convert this to and from a java type in
 *  the same class as there is an implicit dependency between the two.
 * Note that though there must be a one-to-one relation between the result of the select and the returned
 * object the makeObject method may apply an arbitrary mapping.
 * 
 * 
 * 
 * @author spb
 *
 * @param <T> type produced
 * @see SQLExpression 
 * @see GroupingSQLValue
 * @see NestedSQLValue
 * 
 */
public interface SQLValue<T> extends Targetted<T>{
	
	/** Add the expression to a StringBuilder
	 * 
	 * @param sb StringBuilder to modify
	 * @param qualify boolean should fields be qualified with the table name
	 * @return number of fields added
	 */
	public int add(StringBuilder sb, boolean qualify);
	/** Add parameters for this value to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list);

	/** Extract a result of the expression from a ResultSet into an object of the specified type.
	 * 
	 * Note that this method is also used to extract the result of functions over the result type.
	 * @param rs  ResultSet
	 * @param pos
	 * @return produced object
	 * @throws DataException 
	 * @throws SQLException 
	 */
	public T makeObject(ResultSet rs, int pos) throws DataException,SQLException;
	
	/** Get an SQLFilter required to be added to the filter set.
	 * This is usually to implement a join.
	 * 
	 * @return null or SQLFilter
	 */
	default public SQLFilter getRequiredFilter() {
		return null;
	}
}