// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
@uk.ac.ed.epcc.webapp.Version("$Id: NumberFieldExpression.java,v 1.19 2014/09/15 14:30:29 spb Exp $")

/** A {@link FieldExpression} for {@link Number} values.
 * 
 * This supports {@link Integer}, {@link Long}, {@Link Double}, {@link Float} and {@Link Duration}
 * values.
 * <p>
 * {@link Duration} objects are stored at millisecond resolution so as to properly implement {@link SQLExpression}
 * to store values at a different resolution use a {@link DurationFieldValue}.
 * @author spb
 *
 * @param <T>
 * @param <X>
 */
public class NumberFieldExpression<T extends Number,X extends DataObject> extends FieldExpression<T,X>{
	protected NumberFieldExpression(Class<? super X> filter_type,Class<T> target,Repository res,String field) {
		super(filter_type,res, target,field);
	}
	@SuppressWarnings("unchecked")
	public T getValue(Record r) {
		Number n = r.getNumberProperty(name);
		if( n == null ){
			return null;
		}
		// we can't use the record.getIntProperty calls as these return default
		// values on null
		if( target == Double.class){
		
			return (T) Double.valueOf(n.doubleValue());
		}else if( target == Long.class){
			return (T) Long.valueOf(n.longValue());
		}else if( target == Float.class){
			return (T) Float.valueOf(n.floatValue());
		}else if( target == Integer.class){
			return (T) Integer.valueOf(n.intValue());
		}else if( target == Duration.class){
			// The Number value of a Duration is millisecond resolution
			// so this is how its going to be stored in the database
			return (T) new Duration(n.longValue(),1L);
		}
		return (T) n;
	}
	public void setValue(Record r, T value) {
		r.setProperty(name, value);
	}
}