// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

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
	public Integer makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getInt(pos);
		} catch (SQLException e) {
			throw new DataFault("Error adding length to database field",e);
		}

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