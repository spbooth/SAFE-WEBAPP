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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Date;

import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** FieldExpression that views a numeric field as a date timestamp
 * 
 * @author spb
 * @param <T> type of owning object
 *
 */


public class TimestampDateFieldExpression<T extends DataObject> extends FieldExpression<Date,T> implements DateSQLExpression,FilterProvider<T,Date>{
    private final long res;
    private final NumberFieldExpression<Long, T> num_field;
    private final DateSQLExpression date_expr;
	protected TimestampDateFieldExpression(Repository rep,String field) {
		super(rep, Date.class,field);
		this.res=rep.getResolution();
		num_field=rep.getNumberExpression(Long.class, field);
		date_expr = rep.getSQLContext().convertToDate(num_field, res);
	}
	@Override
	public Date getValue(Record r) {
		return r.getDateProperty(name);
	}
	@Override
	public void setValue(Record r, Date value) {
		r.setProperty(name, value);
	}
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		return date_expr.add(sb, qualify);
	}
	
	@Override
	public SQLExpression<? extends Number> getMillis() {
		return date_expr.getMillis();
	}
	@Override
	public SQLExpression<? extends Number> getSeconds() {
		return date_expr.getSeconds();

	}

	@Override
	public  SQLFilter<T> getFilter(MatchCondition match, Date val) {
		//Repository will convert this to the correct number.
		if( match == null ){
			// null implies equality test
			return new SQLValueFilter<>(repository,name,val);
		}
		// This to avoid unecessary conversions in the filter
		return new SQLValueFilter<>(repository,name,match,val);
	}
	@Override
	public SQLFilter<T> getNullFilter(boolean is_null)
			throws CannotFilterException {
		return new NullFieldFilter<>(repository, name,is_null);
	}
	@Override
	public SQLFilter<T> getOrderFilter(boolean descending)
			throws CannotFilterException {
		return new FieldOrderFilter<>(repository, name, descending);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression#preferSeconds()
	 */
	@Override
	public boolean preferSeconds() {
		return res == 1000L;
	}
	@Override
	public int addField(StringBuilder sb, boolean qualify) {
		return num_field.add(sb, qualify);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date_expr == null) ? 0 : date_expr.hashCode());
		result = prime * result + ((num_field == null) ? 0 : num_field.hashCode());
		result = prime * result + (int) (res ^ (res >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimestampDateFieldExpression other = (TimestampDateFieldExpression) obj;
		if (date_expr == null) {
			if (other.date_expr != null)
				return false;
		} else if (!date_expr.equals(other.date_expr))
			return false;
		if (num_field == null) {
			if (other.num_field != null)
				return false;
		} else if (!num_field.equals(other.num_field))
			return false;
		if (res != other.res)
			return false;
		return true;
	}
	
}