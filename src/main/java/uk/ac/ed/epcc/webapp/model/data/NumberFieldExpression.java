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
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;


/** A {@link FieldExpression} for {@link Number} values.
 * 
 * This supports {@link Integer}, {@link Long}, {@Link Double}, {@link Float} and {@Link Duration}
 * values.
 * <p>
 * {@link Duration} objects are stored at millisecond resolution so as to properly implement {@link SQLExpression}
 * to store values at a different resolution use a {@link DurationFieldValue}.
 * @author spb
 *
 * @param <T> type of expression.
 * @param <X> type of hosting {@link DataObject}
 */
public class NumberFieldExpression<T extends Number,X extends DataObject> extends FieldExpression<T,X>{
	protected NumberFieldExpression(Class<T> target,Repository res,String field) {
		super(res, target,field);
	}
	@SuppressWarnings("unchecked")
	public T getValue(Record r) {
		Number n = r.getNumberProperty(name);
		if( n == null ){
			return null;
		}
		if( target.isAssignableFrom(n.getClass())){
			// result is directly assignable.
			return (T) n;
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