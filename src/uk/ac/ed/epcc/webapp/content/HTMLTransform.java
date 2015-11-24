// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.Version;


/** A Table.Formatter. If the SimpleXMLBuilder is really a
 * HTMLBuilder then convert whitespaces into non breaking spaces.
 * 
 * 
 * @author spb
 * @param <C> 
 * @param <R> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: HTMLTransform.java,v 1.2 2014/09/15 14:30:14 spb Exp $")

public class HTMLTransform <C,R> extends Object implements Table.Formatter<C,R> {

	public Object convert(Object old) {
		return old;
	}

	

	public Object convert(Table<C, R> t, C col, R row, Object raw) {
		if( raw instanceof String){
			return new HtmlSpaceGenerator((String)raw);
		}
		return raw;
	}
}