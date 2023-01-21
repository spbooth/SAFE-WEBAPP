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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** convert a numeric {@link SQLExpression} to a {@link DateSQLExpression}
 * 
 * @author spb
 *
 */
public class MysqlDateConverter implements DateSQLExpression, WrappedSQLExpression<Date>{

	public MysqlDateConverter(long res, SQLExpression<? extends Number> val) {
		super();
		this.res = res;
		this.val = val;
	}
	private final long res;
	private final SQLExpression<? extends Number> val;
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("FROM_UNIXTIME(");
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
	public Date makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		Timestamp timestamp;

		Timestamp t = rs.getTimestamp(pos);
		if( t == null ){
			return null;
		}
		return new Date(t.getTime());

	}
	public SQLFilter getRequiredFilter() {
		return val.getRequiredFilter();
	}
	public Class<Date> getTarget() {
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
		MysqlDateConverter other = (MysqlDateConverter) obj;
		if (res != other.res)
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("FROM_UNIXTIME(");
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression#preferSeconds()
	 */
	@Override
	public boolean preferSeconds() {
		return res == 1000L;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.WrappedSQLExpression#getSQLValue()
	 */
	public SQLValue<Date> getSQLValue() {
		return new DateSQLValue(val, res);
	}
	@Override
	public String getFilterTag() {
		return val.getFilterTag();
	}
	

}