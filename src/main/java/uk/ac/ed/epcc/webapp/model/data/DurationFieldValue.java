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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionFilter;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionNullFilter;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpressionOrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A wrapper that converts a {@link NumberFieldExpression} into 
 * an {@link SQLAccessor} for a {@link Duration} with a configurable resolution.
 * 
 * Note that {@link NumberFieldExpression} can implement {@link Duration} directly
 * (as a {@link SQLExpression} if millisecond resolution is used as {@link Duration} is a number.
 * This class is only a {@link SQLValue} as the database resolution is different from the numerical
 * value of the {@link Duration}.
 * 
 * 
 * @author spb
 *
 * @param <X>
 */

public class DurationFieldValue<X extends DataObject> implements    SQLAccessor<Duration,X> , FilterProvider<X, Duration>{

	protected final NumberFieldExpression<Number,X> expression;
	protected final long resolution;
	/** convert a {@link NumberFieldExpression} into a {@link DurationFieldValue}
	 * 
	 * @param expression underlying expression
	 * @param resolution resolution of stored value in milliseconds.
	 */
	public DurationFieldValue(NumberFieldExpression<Number,X> expression,long resolution) {
		this.expression = expression;
		this.resolution=resolution;
	}
	
	public Class<Duration> getTarget() {
		return Duration.class;
		
	}

	public Duration getValue(X r) {
		Number number = expression.getValue(r);
		if( number == null){
			return null;
		}
		if( number instanceof Duration){
			return (Duration) number;
		}
		Duration duration = new Duration(number,resolution);
		return duration;
	}

	public int add(StringBuilder sb, boolean qualify) {
		return expression.add(sb, qualify);
		
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return expression.getParameters(list);
	}
	
	public Duration makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		Number number = expression.makeObject(rs, pos);
		if( number == null){
			return null;
		}
		if( number instanceof Duration){
			return (Duration) number;
		}
		Duration duration = new Duration(number,resolution);
		return duration;
	
	}

	public void setValue(X rec, Duration duration) {
		expression.setValue(rec, duration.getTime(resolution));
		
	}

	public void setObject(PreparedStatement stmt, int pos, Duration value)
			throws SQLException {
		stmt.setLong(pos, value.getTime(resolution));
		
	}

	public String getFieldName() {
		return expression.getFieldName();
	}

	@Override
	public String toString() {
		
		return "Duration("+expression.toString()+","+resolution+")";
	}

	public SQLFilter getRequiredFilter() {
		return null;
	}

	public boolean canSet() {
		return true;
	}

	public SQLFilter<X> getFilter(MatchCondition match, Duration val)
			throws CannotFilterException {
		return SQLExpressionFilter.getFilter(expression, match,val.getTime(resolution));
	}

	public SQLFilter<X> getNullFilter(boolean is_null)
			throws CannotFilterException {
		return SQLExpressionNullFilter.getFilter(expression.getTag(),expression, is_null);
	}

	public SQLFilter<X> getOrderFilter(boolean descending)
			throws CannotFilterException {
		
		return SQLExpressionOrderFilter.getFilter(expression.getTag(),descending, expression);
	}

	public String getFilterTag() {
		return expression.getTag();
	}

}