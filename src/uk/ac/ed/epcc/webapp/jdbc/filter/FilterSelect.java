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



/** Base class that holds the rules for turning filters into SQL 
 * selections.
 * 
 * @author spb
 *
 * @param <T>
 */
public class FilterSelect<T> {
  /**  create the WHERE clause for the filter
   * 
   * @param my_filter
   * @param query
   * @param qualify
   */
  protected void makeWhere(BaseFilter<? super T> my_filter,StringBuilder query,boolean qualify) {
	  //TODO Use {@link SQLFilterVisitor} this assumes all queries are {@link PatternFilter}
		boolean seen = false;
		if (my_filter != null && my_filter instanceof PatternFilter) {
			((PatternFilter) my_filter).addPattern(query, qualify);
		}else{
			// match all condition
			query.append(" 1=1 ");
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