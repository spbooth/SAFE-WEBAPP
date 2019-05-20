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
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;


/** A {@link SQLValue} that converts a millisecond value into a Date.
 * 
 * @author spb
 *
 */
public class DateSQLValue implements NestedSQLValue<Date,Number>{
	private final SQLValue<Number> a;
	public DateSQLValue(SQLValue<Number> a){
		this.a=a;
	}
	
	public Class<Date> getTarget() {
		return Date.class;
	}
	@Override
	public String toString(){
		return "Date("+a.toString()+")";
	}

	public int add(StringBuilder sb, boolean qualify) {
		return a.add(sb, qualify);
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	

	public Date makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		return new Date(a.makeObject(rs, pos).longValue());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.NestedSQLValue#getNested()
	 */
	@Override
	public SQLValue<Number> getNested() {
		return a;
	}
}