//| Copyright - The University of Edinburgh 2013                            |
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** Combine multiple {@link SQLExpression} by concatenation in SQL.
 * @author spb
 *
 */

public class ConcatSQLExpression implements SQLExpression<String>{

	private LinkedList<SQLExpression<String>> values=new LinkedList<>();
	public ConcatSQLExpression(){
		
	}
	public ConcatSQLExpression(SQLExpression<String> ...sqlValues ){
		for(SQLExpression<String> v : sqlValues){
			values.add(v);
		}
	}
	public void add(SQLExpression<String> v){
		values.add(v);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<String> getTarget() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		
		boolean seen=false;
		sb.append("CONCAT(");
		for(SQLValue<String> v : values){
			if( seen ){
				sb.append(",");
			}else{
				seen=true;
			}
			v.add(sb, qualify);
		}
		sb.append(")");
		return 1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(SQLValue<String> v: values){
			list = v.getParameters(list);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public String makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return rs.getString(pos);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		return null;
	}
	@Override
	public String getFilterTag() {
		for(SQLExpression<String> s : values) {
			String t = s.getFilterTag();
			if( t!= null) {
				return t;
			}
		}
		return null;
	}

	
}