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

public class LocateSQLValue implements SQLValue<Integer>{
	private SQLValue<String> str;
	private SQLValue<String> col;
	private SQLValue<Integer> pos;
	
	public LocateSQLValue(SQLValue<String> str, SQLValue<String> col, SQLValue<Integer> pos){
		this.str = str;
		this.col = col;
		this.pos = pos;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		int added=0;
    	added+=str.add(sb, qualify);
    	sb.append(",");
    	added+=col.add(sb, qualify);
    	sb.append(",");
    	added+=pos.add(sb, qualify);
    	assert(added==3); // we assume one field per value
    	return added;
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
	public Class<? super Integer> getTarget() {
		return Integer.class;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public Integer makeObject(ResultSet rs, int loc) throws DataException {
		
		
		try {
			String str_val = rs.getString(loc);
			String col_val = rs.getString(loc+1);
			int pos_val = rs.getInt(loc+2);
			return col_val.indexOf(str_val, pos_val-1); // Java index from zero but SQL and our syntax from 1
		} catch (SQLException e) {
			throw new DataFault("Error finding '" + str
					+ "' in column " + col
					+ " starting at position " + pos + ".",e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list = str.getParameters(list);
		list = col.getParameters(list);
		list = pos.getParameters(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		SQLAndFilter required= null;
		for( SQLValue part : new SQLValue[]{str,col,pos} ){
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
	
	
	public SQLValue getString() {
		return str;
	}

	public SQLValue getColumn() {
		return col;
	}

	public SQLValue getPosition() {
		return pos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + ((col == null) ? 0 : col.hashCode());
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
		LocateSQLValue other = (LocateSQLValue) obj;
		if (str == null) {
			if (other.str != null)
				return false;
		} else if (!str.equals(other.str))
			return false;
		if (col == null) {
			if (other.col != null)
				return false;
		} else if (!col.equals(other.col))
			return false;
		if (pos != other.pos)
			return false;
		
		return true;
	}
}