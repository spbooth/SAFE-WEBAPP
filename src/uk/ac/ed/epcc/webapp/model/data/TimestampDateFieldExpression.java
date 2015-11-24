// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: TimestampDateFieldExpression.java,v 1.36 2014/09/15 14:30:29 spb Exp $")

public class TimestampDateFieldExpression<T extends DataObject> extends FieldExpression<Date,T> implements DateSQLExpression,FilterProvider<T,Date>{
    private final long res;
    private final NumberFieldExpression<Integer, T> num_field;
    private final DateSQLExpression date_expr;
	protected TimestampDateFieldExpression(Class<? super T> target,Repository rep,String field) {
		super(target,rep, Date.class,field);
		this.res=rep.getResolution();
		num_field=rep.getNumberExpression(target,Integer.class, field);
		date_expr = rep.getSQLContext().convertToDate(num_field, res);
	}
	public Date getValue(Record r) {
		return r.getDateProperty(name);
	}
	public void setValue(Record r, Date value) {
		r.setProperty(name, value);
	}
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		return date_expr.add(sb, qualify);
	}
	
	public SQLExpression<? extends Number> getMillis() {
		return date_expr.getMillis();
	}
	public SQLExpression<? extends Number> getSeconds() {
		return date_expr.getSeconds();

	}

	public  SQLFilter<T> getFilter(MatchCondition match, Date val) {
		//Repository will convert this to the correct number.
		if( match == null ){
			// null implies equality test
			return new SQLValueFilter<T>(filter_type,repository,name,val);
		}
		// This to avoid unecessary conversions in the filter
		return new SQLValueFilter<T>(filter_type,repository,name,match,val);
	}
	public SQLFilter<T> getNullFilter(boolean is_null)
			throws CannotFilterException {
		return new NullFieldFilter<T>(filter_type,repository, name,is_null);
	}
	public SQLFilter<T> getOrderFilter(boolean descending)
			throws CannotFilterException {
		return new FieldOrderFilter<T>(filter_type,repository, name, descending);
	}
	
}