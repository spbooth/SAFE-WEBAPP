// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.model.data.FieldValue;

/** A combination of {@link Accessor} and {@link SQLValue}. 
 * In general the two behaviours are distinct though related.
 * This interface is for leaf level entities (database fields) where we
 * always need to support both behaviours so it is sensible to implement them
 * in the same object. 
 * @see FieldValue
 * @author spb
 *
 * @param <T> type of result value
 * @param <R> target object
 */
public interface SQLAccessor<T,R> extends Accessor<T,R>, SQLValue<T> {

}