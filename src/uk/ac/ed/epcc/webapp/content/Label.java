// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;

@uk.ac.ed.epcc.webapp.Version("$Id: Label.java,v 1.3 2014/09/15 14:30:14 spb Exp $")
public class Label<I> implements UIGenerator{
	public Label(AppContext conn,Field<I> field) {
		this.conn=conn;
		this.field = field;
	}
	public final Field<I> field;
	public final AppContext conn;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addFormLabel(conn, field);
		return builder;
	}
}