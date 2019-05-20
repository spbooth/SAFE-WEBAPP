//| Copyright - The University of Edinburgh 2013                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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
			((ContentBuilder )builder).addTable(conn,nf, t);
		}else{
			TableXMLFormatter f = new TableXMLFormatter(builder, nf);
			f.add(t);
		}
		return builder;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		TextTableFormatter f = new TextTableFormatter(nf, t);
		StringBuilder sb = new StringBuilder();
		f.add(sb);
		return sb.toString();
	}

}