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
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;


/** A {@link SQLValue} that extracts the millisecond value from {@link Date} {@link SQLExpression}
 * 
 * @author Stephen Booth
 *
 */
public class MillisecondSQLValue implements NestedSQLValue<Long,Date>{
	private final SQLValue<Date> a;
	public MillisecondSQLValue(SQLValue<Date> a){
		this.a=a;
	}
	
	public Class<Long> getTarget() {
		return Long.class;
	}
	@Override
	public String toString(){
		return "Millis("+a.toString()+")";
	}

	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb, qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	

	public Long makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return Long.valueOf(a.makeObject(rs, pos).getTime());
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.NestedSQLValue#getNested()
	 */
	@Override
	public SQLValue<Date> getNested() {
		return a;
	}

	@Override
	public String getFilterTag() {
		return a.getFilterTag();
	}
}