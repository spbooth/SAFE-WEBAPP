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
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;

/** SQLAccessor that converts an SQLAccessor of a different type to a Duration
 * 
 * @author spb
 *
 * @param <T> type of nested SQLAccessor
 */


public class DurationConvertSQLValue<T extends Number>  implements SQLValue<Duration> {
	private final SQLValue<T> a;
	private final long resolution;
    public DurationConvertSQLValue(SQLValue<T> a,long resolution){
    	this.a = a;
    	this.resolution=resolution;
    }
	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb,qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	
	public Duration makeObject(ResultSet rs, int pos) throws DataException {
		T temp = a.makeObject(rs, pos);
		if( temp != null ){
	    	if( temp instanceof Number ){
	    		return new Duration((Number)temp,resolution);
	    	}
	    	
	    }
		return null;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("Duration(");
		sb.append(a.toString());
		sb.append(",");
		sb.append(Long.toString(resolution));
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<? super Duration> getTarget() {
		return Duration.class;
	}
}