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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.IndexedFieldValue;
/** A join based SQLExpression for accessing remote tables.
 * 
 * @author spb
 *
 * @param <H> home object
 * @param <R> remote object
 * @param <T> target type
 */


public class DerefSQLExpression<H extends DataObject,R extends DataObject,T> implements SQLExpression<T>{
	
	private SQLExpression<T> remote_expression;
	private SQLFilter required_filter;
	@SuppressWarnings("unchecked")
	public DerefSQLExpression(IndexedFieldValue<H,R> a,SQLExpression<T> expr ) throws Exception {
		remote_expression = expr;
		required_filter = a.getSQLFilter(remote_expression.getRequiredFilter());
	}
	public int add(StringBuilder sb, boolean qualify) {
		return remote_expression.add(sb, qualify);
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return remote_expression.getParameters(list);
	}
	
	public T makeObject(ResultSet rs, int pos) throws DataException {
		return remote_expression.makeObject(rs, pos);
	}

	public SQLFilter getRequiredFilter() {
		return required_filter;
		
	}

	public Class<? super T> getTarget() {
		return remote_expression.getTarget();
	}

}