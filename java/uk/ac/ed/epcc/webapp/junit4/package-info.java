//| Copyright - The University of Edinburgh 2015                            |
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
/** Support package for webapp Junit4 test framework.
 * 
 * Where Junit3 could be extended by extending TestCase in Junit4
 * the model is to extends the Runner classes.
 * 
 * We would introduce the following extensions.
 * <ul>
 * <li>Allow database fixtures to be loaded for a test
 * <li>Allow interface tests to be inherited.
 * </ul>
 * 
 * Note that database fixture annotations are per test. A global fixture can e set in
 * a @before method using {@link uk.ac.ed.epcc.webapp.model.data.XMLDataUtils}.
 * though note these will be loaded independently of the annotation and <em>after</em> them.
 * 
 * 
 * 
 * @author spb
 *
 */
package uk.ac.ed.epcc.webapp.junit4;