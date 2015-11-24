// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.util.Collection;

import uk.ac.ed.epcc.webapp.forms.Identified;

/** Class to turn Collection into a HTML list.
 * There is a static method to perform one-off formatting.
 * or an object instance can be wrapped round the collection to
 * create a {@link XMLGenerator}
 * 
 * A {@link ContentBuilder} will be able to add a collection directly via {@link ContentBuilder#addList(Collection)}
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ListGenerator.java,v 1.3 2014/09/15 14:30:14 spb Exp $")
public class ListGenerator<X> implements XMLGenerator {

	private final Collection<X> data;
	/**
	 * 
	 */
	public ListGenerator(Collection<X> c) {
		this.data=c;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		addList(builder,data);
		return builder;
	}

	public static <X> void addList(SimpleXMLBuilder builder, Collection<X> c){
		builder.open("ul");
		for(X dat : c){
			builder.open("li");
			addData(builder, dat);
			builder.close();
		}
		builder.close();
	}
	public static <X> void addData(SimpleXMLBuilder builder, X data){
		if( data instanceof XMLGenerator){
			((XMLGenerator)data).addContent(builder);
			return;
		}
		if( data instanceof Identified){
			builder.clean(((Identified)data).getIdentifier());
			return;
		}
		builder.clean(data.toString());
	}
}
