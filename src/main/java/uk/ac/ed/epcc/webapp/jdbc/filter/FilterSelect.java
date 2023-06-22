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
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.model.data.Repository;



/** Base class that holds the rules for turning filters into SQL 
 * selections.
 * 
 * @author spb
 *
 * @param <T> type of object being filtered.
 */
public class FilterSelect<T> {
	/** is the filter known to match no records without running any sql.
	 * 
	 * @param my_filter
	 * @return
	 */
	protected  boolean isEmpty(BaseFilter<? super T> my_filter){
		if( my_filter == null){
			return false;
		}
		try {
			return my_filter.acceptVisitor(new CheckEmptyVisitor<>());
		} catch (Exception e) {
			return false;
		}
	}
	/**  create the WHERE clause for the filter
   * @param tables  set of {@link Repository}s used for the query
   * @param my_filter the {@link BaseFilter} optional can be null
   * @param query a {@link StringBuilder} to add where clause to
   * @param qualify should the field names be qualified with table names
   */
  protected void makeWhere(Set<Repository> tables,BaseFilter<? super T> my_filter,StringBuilder query,boolean qualify){
	  if( my_filter == null){
		  query.append(" true ");
		  return;
	  }
	  try {
		  my_filter.acceptVisitor(new MakeSelectVisitor<>(tables,query, qualify, false));
	  } catch (Exception e) {
		  // should not get an exception with require_sql=false
		  throw new ConsistencyError("Unexpected exception",e);
	  }
  }
  
  protected List<PatternArgument> getFilterArguments(BaseFilter<? super T> my_filter, List<PatternArgument> list) {
	  if( my_filter == null ){
		  return list;
	  }
	  try {
		return my_filter.acceptVisitor(new GetListFilterVisitor<>(list, false));
	} catch (Exception e) {
		// should not get an exception with require_sql=false
		throw new ConsistencyError("Unexpected exception",e);
	}
  }
protected final int setParams(int pos, StringBuilder q, PreparedStatement stmt,
		List<PatternArgument> args) throws SQLException {
	if( args == null ){
		return pos;
	}
	if( q != null ){
		q.append(" nparam:");
		q.append(args.size());
	}
	for (Iterator<PatternArgument> it = args.iterator(); it.hasNext();) {
		PatternArgument arg =  it.next();
		if( q != null ){
			q.append(" ");
			q.append(pos);
			q.append(":");
			q.append(arg.getField());
			q.append(":");
			if( arg.canLog() ){
				Object o = arg.getArg();
				if( o != null ){
					q.append(o.toString());
					if( o instanceof Date){
						q.append("(");
						q.append(((Date)o).getTime()/1000);
						q.append(")");
					}
				}else{
					q.append("NULL");
				}
			}else{
				q.append("[hidden]");
			}
		}
		arg.addArg(stmt, pos++);

	}
	return pos;
}

}