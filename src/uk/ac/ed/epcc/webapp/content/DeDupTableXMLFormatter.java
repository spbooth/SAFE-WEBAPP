//| Copyright - The University of Edinburgh 2018                            |
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
import java.util.ArrayList;

/** A {@link TableXMLFormatter} that merges duplicate entries in a column.
 * @author Stephen Booth
 *
 */
public class DeDupTableXMLFormatter<C,R> extends TableXMLFormatter<C,R> {

	private ArrayList<R> row_keys;
	/**
	 * @param builder
	 * @param nf
	 * @param style
	 */
	public DeDupTableXMLFormatter(SimpleXMLBuilder builder, NumberFormat nf, String style) {
		super(builder, nf, style);
	}

	/**
	 * @param builder
	 * @param nf
	 */
	public DeDupTableXMLFormatter(SimpleXMLBuilder builder, NumberFormat nf) {
		super(builder, nf);
	}

	@Override
	public void addBody(Table<C, R> t) {
		row_keys = new ArrayList<>();  // need random access to rows.
		for(R row : t.getRows()) {
			row_keys.add(row);
		}
		super.addBody(t);
		row_keys.clear();
		row_keys=null;
	}

	@Override
	protected void addTd(Table<C, R> t, R row_key, int row, int col, C key, Object n, String dc, int cols, int rows) {
		if( row > 0 ) {
			Object prev = t.get(key, row_keys.get(row-1));
			if( n != null && prev != null && n.equals(prev)) {
				hb.clean("\t\t");
				return;  //supress duplicate
			}
		}
		for( int i=row+1; i< t.nRows();i++) {
			Object next= t.get(key, row_keys.get(i));
			if( n != null && next != null && n.equals(next)) {
				rows++;
			}else {
				break;
			}
		}
		super.addTd(t, row_key, row, col, key, n, dc, cols, rows);
	}

}
