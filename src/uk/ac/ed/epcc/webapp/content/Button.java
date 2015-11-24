// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Wrapper object allowing us to add buttons as {@link Table} content
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Button.java,v 1.2 2014/09/15 14:30:14 spb Exp $")

public class Button implements XMLGenerator,UIGenerator,Contexed {

	private final String text;
	private final FormResult result;
	private final AppContext conn;
	public Button(AppContext conn,String text,FormResult result){
		this.conn=conn;
		this.text=text;
		this.result=result;
	}
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ContentBuilder){
			addContent((ContentBuilder)builder);
		}else{
			builder.clean(text);
		}
		return builder;
	}
	public AppContext getContext() {
		return conn;
	}
	public String toString(){
		// makes text tables work sensibly
		return text;
	}
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addButton(conn, text, result);
		return builder;
	}
}