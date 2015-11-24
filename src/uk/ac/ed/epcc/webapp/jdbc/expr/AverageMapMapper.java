// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.AppContext;
/** A MapMapper where the data field is Numerical data averaged out.
 * 
 * Note that there is no way of combing averages to give an average.
 * so the keys need to generate unique values.
 * 
 * @author adrianj
 *
 * @param <K> key type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AverageMapMapper.java,v 1.4 2014/09/15 14:30:22 spb Exp $")

public class AverageMapMapper<K> extends MapMapper<K, Number> {

	public AverageMapMapper(AppContext c, SQLValue<K> key, String key_name,SQLExpression<? extends Number> val, String value_name) {
		super(c, key, key_name);
		addAverage(val, value_name);
	}

	

}