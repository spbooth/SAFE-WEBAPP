//| Copyright - The University of Edinburgh 2016                            |
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

/** An interface for objects that can produce a {@link UIGenerator} to add
 * themselves to a {@link ContentBuilder}.
 * 
 * This serves the same purpose as the {@link UIGenerator} interface but is better suited
 * to cases where the content logic is added by composition.
 * 
 * 
 * @see UIGenerator
 * @author spb
 *
 */
public interface UIProvider {

	public UIGenerator getUIGenerator();
}
