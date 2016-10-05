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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;



public class LocateSQLValue implements SQLValue<Integer> {
	private SQLValue<String> str;
    private SQLValue<String> col;
    private SQLValue<Integer> pos;
    
    public LocateSQLValue(AppContext conn, SQLValue<String> str, SQLValue<String> col, SQLValue<Integer> pos) {
    	this.str=str;
    	this.col=col;
    	this.pos=pos;
    }
   
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("LOCATE('");
    	str.add(sb, false);
    	sb.append("',");
    	col.add(sb, qualify);
    	sb.append(",");
    	pos.add(sb, false);
    	sb.append(")");
    	return 1;
	}
  
    
    public Integer makeObject(ResultSet rs, int pos) throws DataException{
    	try {
			return rs.getInt(pos);
		} catch (SQLException e) {
			throw new DataFault("Error finding '" + str
					+ "' in column " + col
					+ " starting at position " + pos + ".",e);
		}
    }

	public Class<Integer> getTarget() {
		return Integer.class;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		add(sb, true);
    	return sb.toString();
	}

	public SQLFilter getRequiredFilter() {
		return null;
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return null;
	}

}