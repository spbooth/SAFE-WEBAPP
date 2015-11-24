// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;

@uk.ac.ed.epcc.webapp.Version("$Id: NumberFormatTransform.java,v 1.2 2014/09/15 14:30:15 spb Exp $")
/**
 * Format numerical cells of a Table using a NumberFormat.
 * 
 * @author spb
 * 
 */
public class NumberFormatTransform implements NumberTransform {
	private final NumberFormat nf;
	private final Object use_null;
	public NumberFormatTransform(NumberFormat f) {
		nf = f;
		use_null=0.0;
	}
	public NumberFormatTransform(NumberFormat f,Object use_null) {
		nf = f;
		this.use_null=use_null;
	}

	public Object convert(Object old) {
		if( old == null){
			old = use_null;
		}
		if( old instanceof Number){
			return nf.format((Number) old);
		}
		return old;
	}
	

	

}