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
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** Create locate expression in SQL.
 * @author spb
 *
 */

public class LocateSQLExpression implements SQLExpression<Integer>{
	private SQLExpression<String> substr;
	private SQLExpression<String> str;
	private SQLExpression<Integer> pos;
	
	public LocateSQLExpression(SQLExpression<String> substr, SQLExpression<String> str, SQLExpression<Integer> pos){
		this.substr = substr;
		this.str = str;
		this.pos = pos;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("LOCATE(");
		substr.add(sb, qualify);
    	sb.append(",");
    	str.add(sb, qualify);
    	sb.append(",");
    	pos.add(sb, qualify);
    	sb.append(")");
    	return 1;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		add(sb, true);
    	return sb.toString();
    }
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<Integer> getTarget() {
		return Integer.class;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public Integer makeObject(ResultSet rs, int loc) throws DataException, SQLException {
			return rs.getInt(loc);
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list = substr.getParameters(list);
		list = str.getParameters(list);
		list = pos.getParameters(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		SQLAndFilter required= null;
		for( SQLExpression part : new SQLExpression[]{substr,str,pos} ){
			SQLFilter f = part.getRequiredFilter();
			if( f != null ){
				if( required == null){
					required = new SQLAndFilter(f.getTarget(),f);
				}else{
					required.addFilter(f);
				}
			}
			
		}
		return required;
	}
	
	
	public SQLExpression getSubstring() {
		return substr;
	}

	public SQLExpression getString() {
		return str;
	}

	public SQLExpression getPosition() {
		return pos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((substr == null) ? 0 : substr.hashCode());
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocateSQLExpression other = (LocateSQLExpression) obj;
		if (substr == null) {
			if (other.substr != null)
				return false;
		} else if (!substr.equals(other.substr))
			return false;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		if (pos != other.pos)
			return false;
		
		return true;
	}
}