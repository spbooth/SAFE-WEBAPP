package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

public class PostgresqlDateConverter implements DateSQLExpression{

	public PostgresqlDateConverter(long res, SQLExpression<? extends Number> val) {
		super();
		this.res = res;
		this.val = val;
	}
	private final long res;
	private final SQLExpression<? extends Number> val;
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("to_timestamp(");
		if( res != 1000L){
			sb.append("(");
			val.add(sb,qualify);
			if( res != 1L){
			  sb.append("*");
			  sb.append(res);
			}
			sb.append(")/1000");
		}else{
			val.add(sb,qualify);
		}
		sb.append(")");
		return 1;
	}
	
	public SQLExpression<? extends Number> getMillis() {
		if( res == 1L ){
			return val;
		}else{
		
			return BinaryExpression.create(null,val,
					  Operator.MUL,
					  new ConstExpression(Long.class,res));
		
		}

	}
	public SQLExpression<? extends Number> getSeconds() {
		if( res == 1000L ){
			return val;
		}else{
		
			return BinaryExpression.create(null,val,
					  Operator.MUL,
					  new ConstExpression(Long.class,res/1000L));
		
		}

	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return val.getParameters(list);
	}
	public Date makeObject(ResultSet rs, int pos) throws DataException {
		Timestamp timestamp;
		try {
			return new Date(rs.getTimestamp(pos).getTime());
		} catch (SQLException e) {
			throw new DataException("Fault getting timestamp",e);
		}
		
	}
	public SQLFilter getRequiredFilter() {
		return val.getRequiredFilter();
	}
	public Class<? super Date> getTarget() {
		return Date.class;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (res ^ (res >>> 32));
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		PostgresqlDateConverter other = (PostgresqlDateConverter) obj;
		if (res != other.res)
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("to_timestamp(");
		if( res != 1000L){
			sb.append("(");
			sb.append(val.toString());
			if( res != 1L){
			  sb.append("*");
			  sb.append(res);
			}
			sb.append(")/1000");
		}else{
			sb.append(val.toString());
		}
		sb.append(")");
		return sb.toString();
	}
	

}
