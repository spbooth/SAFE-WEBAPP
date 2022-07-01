//| Copyright - The University of Edinburgh 2019                            |
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

/** An alternative mechanism for formatting tables 
 * optimised for streaming directly to a {@link ExtendedXMLBuilder}
 * its less functional than a {@link Table} but does not need to keep
 * all the table contents in memory.
 * 
 * @author Stephen Booth
 *
 */
public class StreamingTable {
	private final SimpleXMLBuilder sb;
	private String cols[];
	private boolean open=false;
	private boolean append=false;

	public static StreamingTable getInstance(ContentBuilder cb, String ... headings) {
		if( cb instanceof SimpleXMLBuilder) {
			return new StreamingTable((SimpleXMLBuilder)cb, headings);
		}
		StreamingTable result = new StreamingTable(cb.getText(), headings);
		result.append=true;
		return result;
	}
	/**
	 * 
	 * @param sb  {@link ExtendedXMLBuilder} to add too.
	 * @param headings  table column headings
	 */
	public StreamingTable(SimpleXMLBuilder sb, String ... headings) {
		this.sb=sb;
		this.cols=headings;
	}
	
	/** Add a row
	 * 
	 * @param data
	 */
	public void addRow(Object ... data) {
		if( ! open ) {
			open=true;
			sb.open("table");
			sb.open("thead");
			sb.open("tr");
			for(String h : cols) {
				sb.open("th");
				sb.addObject(h);
				sb.close();
			}
			sb.close(); // tr
			sb.close(); // thead
			sb.open("tbody");
		}
		sb.open("tr");
		for(Object o :data) {
			sb.open("td");
			if( o != null ) {
				sb.addObject(o);
			}
			sb.close();
		}
		sb.close();
	}
	
	/** finish the table
	 * @return true if table had data.
	 */
	public boolean close() {
		if( open ) {
			sb.close(); //tbody
			sb.close(); //table
		}
		if( append) {
			sb.appendParent();
		}
		return open;
	}

}
