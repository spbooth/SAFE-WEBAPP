//| Copyright - The University of Edinburgh 2011                            |
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


/** Interface for extended XML content (including HTML).
 * 
 * 
 * 
 * @author spb
 *
 */


public interface ExtendedXMLBuilder extends SimpleXMLBuilder{

	/** Insert a non-breaking space.
	 * 
	 */
	public abstract void nbs();

	/** Insert line break.
	 * 
	 */
	public default void br() {
		open("br");
		close();
	}
	
	/** Convenience method to add an additional class attribute using
	 * the HTML convention that multiple classes are represented 
	 * as one attribute with values separated by spaces.
	 * 
	 * It is only legal to call this after a
	 * call to open and before close or clean.
	 * 
	 * @param s CharSequence attribute value or null for no value
	 * @return reference to self
	 */
	public abstract SimpleXMLBuilder addClass(CharSequence s);
	
}