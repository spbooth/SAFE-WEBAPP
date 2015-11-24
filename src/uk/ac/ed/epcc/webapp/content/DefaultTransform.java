// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

@uk.ac.ed.epcc.webapp.Version("$Id: DefaultTransform.java,v 1.2 2014/09/15 14:30:14 spb Exp $")
public class DefaultTransform implements Transform {
	Object default_entry;

	public DefaultTransform(Object def) {
		default_entry = def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.hpcx.Table.Transform#convert(java.lang.Object)
	 */
	public Object convert(Object old) {
		if (old == null) {
			return default_entry;
		}
		return old;
	}

}