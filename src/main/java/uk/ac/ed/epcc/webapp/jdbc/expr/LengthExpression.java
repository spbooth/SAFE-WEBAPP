//| Copyright - The University of Edinburgh 2014                            |
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
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** SQLExpression to generate the length of String {@link SQLExpression}.
 * 
 * Actually the LENGTH function is not standard SQL but its fairly common.
 * 
 * @author spb
 *
 */
public class LengthExpression  implements SQLExpression<Integer> {
	private SQLExpression<String> a;
    public LengthExpression(SQLExpression<String> a){
    	this.a=a;
    }
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("LENGTH(");
		a.add(sb,qualify);
		sb.append(")");
		return 1;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return a.getParameters(list);
	}
	public Integer makeObject(ResultSet rs, int pos) throws DataException, SQLException {
			return rs.getInt(pos);
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
    	sb.append("LENGTH(");
    	a.add(sb, true);
    	sb.append(")");
    	return sb.toString();
    }
	public SQLFilter getRequiredFilter() {
		return a.getRequiredFilter();
	}
	public Class<Integer> getTarget() {
		return Integer.class;
	}
}