// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.NumberOp;
/** A MapMapper where the data field is Numerical data combined by summation.
 * As the key is a SQLValue rather than a SQLExpression we may have multiple SQL rows mapping to the
 * same key so numerical results may have to be combined.
 * 
 * @author spb
 *
 * @param <K> key type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SumMapMapper.java,v 1.2 2014/09/15 14:30:24 spb Exp $")

public class SumMapMapper<K> extends MapMapper<K, Number> {

	public SumMapMapper(AppContext c, SQLValue<K> key, String key_name,SQLExpression<? extends Number> val, String value_name) {
		super(c, key, key_name);
		addSum(val, value_name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MapMapper#combine(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Number combine(Number a, Number b) {
		return NumberOp.add(a,b);
		
	}
	

}