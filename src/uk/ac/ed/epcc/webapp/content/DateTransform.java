// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.text.DateFormat;
import java.util.Date;

import uk.ac.ed.epcc.webapp.Version;
@uk.ac.ed.epcc.webapp.Version("$Id: DateTransform.java,v 1.3 2014/09/15 14:30:14 spb Exp $")


public class DateTransform implements Transform{
    DateFormat df;
    public DateTransform(DateFormat df){
    	this.df=df;
    }
	public Object convert(Object old) {
		if( old != null &&  old instanceof Date){
			return df.format((Date)old);
		}
		return old;
	}

}