// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link XMLGenerator} that formats a table.
  	 * If the passed builder is a {@link ContentBuilder} it will use
  	 * the {@link ContentBuilder#addTable(AppContext, Table)} method.
	 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: TableXMLGenerator.java,v 1.2 2014/09/15 14:30:15 spb Exp $")
public class TableXMLGenerator implements XMLGenerator {
	private final AppContext conn;
    private final Table t;
    private final NumberFormat nf;
  
	public TableXMLGenerator(AppContext conn,NumberFormat nf,Table t) {
		this.conn=conn;
		this.nf=nf;
		this.t=t;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ContentBuilder){
			((ContentBuilder )builder).addTable(conn, t);
		}else{
			TableXMLFormatter f = new TableXMLFormatter(builder, nf);
			f.add(t);
		}
		return builder;
	}

}
