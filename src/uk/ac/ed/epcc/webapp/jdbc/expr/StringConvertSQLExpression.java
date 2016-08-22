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

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** {@link SQLExpression} that converts an {@link SQLExpression} of a different type to a string
 * 
 * @author spb
 *
 * @param <T> type of nested {@link SQLExpression}
 */


public class StringConvertSQLExpression<T>  implements SQLExpression<String> {
	private SQLExpression<T> a;
    public StringConvertSQLExpression(SQLExpression<T> a){
    	this.a=a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	public String makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getString(pos);
		} catch (SQLException e) {
			throw new DataFault("Error reading value as string",e);
		}

	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("String(");
    	a.add(sb, true);
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super String> getTarget() {
		return String.class;
	}
}