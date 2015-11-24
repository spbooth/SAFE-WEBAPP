// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.text.DateFormat;
import java.util.Date;

@uk.ac.ed.epcc.webapp.Version("$Id: FormatDateTransform.java,v 1.2 2014/09/15 14:30:14 spb Exp $")
/**
 * Format numerical cells of a Table using a DateFormat.
 * 
 * @author spb
 * 
 */
public class FormatDateTransform implements Transform {
	DateFormat nf;

	public FormatDateTransform(DateFormat f) {
		nf = f;
	}

	public Object convert(Object old) {
		if (old == null) {
			return null;
		}
		if (old instanceof Date) {
			return nf.format((Date) old);
		}
		return old;
	}

}