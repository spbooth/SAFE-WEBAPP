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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** SQLAccessor that converts an SQLAccessor of a different type to a double
 * 
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */


public class DoubleConvertSQLValue<T>  implements NestedSQLValue<Double,T> {
	private SQLValue<T> a;
    public DoubleConvertSQLValue(SQLValue<T> a){
    	this.a = a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Double makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return Double.valueOf(((Number)temp).doubleValue());
	    	}
	    	if( temp instanceof String){
	    		return Double.parseDouble((String)temp);
	    	}
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Double(");
    	sb.append(a.toString());
    	sb.append(")");
    	return sb.toString();
    }
	
	public Class<Double> getTarget() {
		return Double.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.NestedSQLValue#getNested()
	 */
	@Override
	public SQLValue<T> getNested() {
		return a;
	}
	@Override
	public String getFilterTag() {
		return a.getFilterTag();
	}
}