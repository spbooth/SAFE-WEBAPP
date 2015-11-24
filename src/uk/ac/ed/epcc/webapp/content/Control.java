// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;

@uk.ac.ed.epcc.webapp.Version("$Id: Control.java,v 1.5 2014/09/15 14:30:14 spb Exp $")
public class Control<I> implements UIGenerator{
	public Control(AppContext conn,Field<I> field) {
		this(conn,field,null);
	}
	public Control(AppContext conn,Field<I> field,Object item) {
		this.conn=conn;
		this.field = field;
		this.radio_selector=item;
	}
	public final Field<I> field;
	public final AppContext conn;
	public final Object radio_selector;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addFormInput(conn, field,radio_selector);
		return builder;
	}
}