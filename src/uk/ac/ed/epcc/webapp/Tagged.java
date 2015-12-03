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
package uk.ac.ed.epcc.webapp;
/** Interface for {@link Contexed} objects that can be queried for their
 * configuration tag. Usually this is the database table.
 * If an object implements this interface then a call to {@link AppContext#getClassFromName}
 * should either fail or resolve to the tagged class.
 * 
 * @author spb
 *
 */
public interface Tagged extends Contexed {
	/** get the construction tag
	 * 
	 * @return String tag
	 */
	public String getTag();
}