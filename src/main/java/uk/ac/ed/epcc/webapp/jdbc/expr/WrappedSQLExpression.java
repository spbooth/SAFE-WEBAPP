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
package uk.ac.ed.epcc.webapp.jdbc.expr;

/** A {@link SQLExpression} which can be implemented more directly as a {@link SQLValue}
 * for example type conversion performed in SQL
 * 
 * When these are just being evaluated at the top level of the query a SQLValue can be substituted that
 * performs some of the expression in jave. 
 * 
 * @author Stephen Booth
 *
 */
public interface WrappedSQLExpression<T> extends SQLExpression<T>{

	/** get an equivalent (but preferable) {@link SQLValue}
	 * 
	 * @return
	 */
	public SQLValue<T> getSQLValue();
}
