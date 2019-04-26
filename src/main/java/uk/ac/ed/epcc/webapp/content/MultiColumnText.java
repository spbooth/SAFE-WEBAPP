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

/** Simple implementation of {@link MultiColumn}.
 * @author Stephen Booth
 *
 */
public class MultiColumnText implements MultiColumn {
	/**
	 * @param cols
	 * @param display_class
	 * @param text
	 */
	public MultiColumnText(int cols, String display_class, String text) {
		super();
		this.cols = cols;
		this.display_class = display_class;
		this.text = text;
	}

	private final int cols;
	private final String display_class;
	private final String text;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.MultiColumn#getDisplayClass()
	 */
	@Override
	public String getDisplayClass() {
		return display_class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.MultiColumn#getColumns()
	 */
	@Override
	public int getColumns() {
		return cols;
	}
	
	public String toString() {
		return text;
	}

}
