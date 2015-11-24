// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.NumberOp;
/** A {@link MapMapper} where the data field is Numerical that the maximum is selected from.
 * As the key is a {@link SQLValue} rather than a {@link SQLExpression} we may have multiple SQL rows mapping to the
 * same key so numerical results may have to be combined.
 * 
 * @author adrianj
 *
 * @param <K> key type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MaximumMapMapper.java,v 1.2 2014/09/15 14:30:23 spb Exp $")

public class MaximumMapMapper<K> extends MapMapper<K, Number> {

	public MaximumMapMapper(AppContext c, SQLValue<K> key, String key_name,SQLExpression<? extends Number> val, String value_name) {
		super(c, key, key_name);
		addMax(val, value_name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MapMapper#combine(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Number combine(Number a, Number b) {
		return NumberOp.max(a, b);		
	}
	

}