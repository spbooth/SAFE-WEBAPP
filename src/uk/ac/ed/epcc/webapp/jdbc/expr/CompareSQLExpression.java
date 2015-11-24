// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/** A {@link SQLExpression} for the comparison of two values;
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class CompareSQLExpression<C extends Comparable> implements SQLExpression<Boolean> {

	
	public CompareSQLExpression(SQLExpression<C> e1, MatchCondition m,
			SQLExpression<C> e2) {
		super();
		this.a = e1;
		this.m = m;
		this.b = e2;
	}
	public final SQLExpression<C> a;
	public final MatchCondition m;
	public final SQLExpression<C> b;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("(");
		a.add(sb, qualify);
		if( m == null){
			sb.append("=");
		}else{
			sb.append(m.toString());
		}
		b.add(sb,qualify);
		sb.append(")");
		return 1;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(a.toString());
		if( m == null){
			sb.append("==");
		}else{
			sb.append(m.toString());
		}
		sb.append(b.toString());
		sb.append(")");
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list = a.getParameters(list);
		return b.getParameters(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	@Override
	public Boolean makeObject(ResultSet rs, int pos) throws DataException {
		try {
			return rs.getBoolean(pos);
		} catch (SQLException e) {
			throw new DataException("Error making expression",e);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	@Override
	public SQLFilter getRequiredFilter() {
		SQLFilter a_fil = a.getRequiredFilter();
		SQLFilter b_fil = b.getRequiredFilter();
		if( a_fil == null ){
			return b_fil;
		}else{
			if( b_fil == null ){
				return a_fil;
			}
			SQLAndFilter fil = new SQLAndFilter(a_fil.getTarget());
			fil.addFilter(a_fil);
			fil.addFilter(b_fil);
			return fil;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super Boolean> getTarget() {
		return Boolean.class;
	}

	public SQLExpression<C> getA() {
		return a;
	}

	public MatchCondition getMatchCondition() {
		return m;
	}

	public SQLExpression<C> getB() {
		return b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompareSQLExpression other = (CompareSQLExpression) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (m != other.m)
			return false;
		return true;
	}
}
