// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link Transform} that wraps a {@link Labeller}
 * 
 * Because a {@link FormatProvider} can return a null {@link Labeller}
 * this class handles null labellers as well to reduce the need to check the return values.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: LabellerTransform.java,v 1.7 2014/09/30 16:08:09 spb Exp $")
public class LabellerTransform implements Transform{

	public LabellerTransform(AppContext conn, Labeller labeller) {
		super();
		this.conn = conn;
		this.labeller = labeller;
	}

	private final AppContext conn;
	private final Labeller labeller;

	
	@SuppressWarnings("unchecked")
	public Object convert(Object old) {
		if( labeller != null && labeller.accepts(old)){
			// Transforms are type tollerant so
			// check labeller can handle type.
			return labeller.getLabel(conn, old);
		}
		return old;
	}

}
