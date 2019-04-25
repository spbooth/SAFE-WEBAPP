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
/** This package contains logic for accessing expressions over values from objects/records.

There are 3 basic types:
<ul>
<li>An <b>Accessor</b> that accesses a value from an object entirely in java</li>
<li>A <b>SQLValue</b> that retrieves a value using SQL.</li>
<li>A <b>SQLExpression</b> which is a <code>SQLValue</code> where the java representations corresponds directly to the SQL 
representation and so can be composed to form more complex expressions.
</ul> 
 * 
 * These allow the target clauses of complex queries to be built. The where clauses are handled by
 * the filter classes some of which are also built out of <code>SQLExpression</code>s
 * <p>
 * Normally the <code>SQLValue</code> and <code>SQLExpression</code> interfaces are used to directly query the database.
 * The <code>Accessor</code> interfaces provide the same functionality when records are retrieved as objects
 * and queried individually.
 * @author spb
 *
 */
package uk.ac.ed.epcc.webapp.jdbc.expr;