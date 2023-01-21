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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;


/** SQLExpression for duration. 

 * 
 * @author spb
 *
 */
public class DurationSQLExpression  implements SQLExpression<Duration> {
  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + (int) (resolution ^ (resolution >>> 32));
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		DurationSQLExpression other = (DurationSQLExpression) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (resolution != other.resolution)
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
private final SQLExpression<? extends Number> start, end;
  private final long resolution;
 
  
  
  /** {@link Duration} {@link SQLExpression}  (value in milliseconds)
   * 
   * @param resolution
   * @param start  start-time in units of resolution milliseconds
   * @param end    end-time in units of resolution  milliseconds
   */
  public DurationSQLExpression(long resolution, SQLExpression<? extends Number> start, SQLExpression<? extends Number> end){
	  this.start=start;
	  this.end=end;
	  this.resolution=resolution;
  }
  public DurationSQLExpression(SQLExpression<? extends Number> start, SQLExpression<? extends Number> end){
	  this(1L,start,end);
  }

@Override
public Class<Duration> getTarget() {
	return Duration.class;
}
@Override
public String toString() {
	return "Duration("+start.toString()+","+end.toString()+")";
}


@Override
public int add(StringBuilder sb, boolean qualify) {
	if( resolution != 1L) {
		sb.append("(");
		sb.append(Long.toString(resolution));
		sb.append("*");
	}
	sb.append("(");
	end.add(sb, qualify);
	sb.append("-");
	start.add(sb, qualify);
	sb.append(")");
	if( resolution != 1L) {
		sb.append(")");
	}
	return 1;
}
@Override
public List<PatternArgument> getParameters(List<PatternArgument> list) {
	list = end.getParameters(list);
	list =  start.getParameters(list);
	return list;
}


@Override
public Duration makeObject(ResultSet rs, int pos) throws DataException, SQLException {
	// expression is always valued in milliseconds
		return new Duration(rs.getLong(pos),1L);
}
@Override
@SuppressWarnings("unchecked")
public SQLFilter getRequiredFilter() {
	SQLFilter a_fil = start.getRequiredFilter();
	SQLFilter b_fil = end.getRequiredFilter();
	if( a_fil == null ){
		return b_fil;
	}else{
		if( b_fil == null ){
			return a_fil;
		}
		SQLAndFilter fil = new SQLAndFilter(getFilterTag());
		fil.addFilter(a_fil);
		fil.addFilter(b_fil);
		return fil;
	}
}
@Override
public String getFilterTag() {
	String t = start.getFilterTag();
	if( t != null) {
		return t;
	}
	return end.getFilterTag();
}


}