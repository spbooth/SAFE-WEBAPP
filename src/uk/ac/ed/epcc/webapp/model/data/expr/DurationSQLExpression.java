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
import uk.ac.ed.epcc.webapp.jdbc.expr.DateSQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


/** SQLExpression for duration. Only works where the
 * start and end dates can be Millisecond SQLExpressions
 * 
 * @author spb
 *
 */
public class DurationSQLExpression  implements SQLExpression<Duration> {
  private final SQLExpression<? extends Number> start, end;
  int offset;
  public DurationSQLExpression(SQLExpression<? extends Number> start, SQLExpression<? extends Number> end){
	  this.start=start;
	  this.end=end;
  }
  public DurationSQLExpression(DateSQLExpression start, DateSQLExpression end){
	  this(start.getMillis(),end.getMillis());
  }

public Class<? super Duration> getTarget() {
	return Duration.class;
}
@Override
public String toString() {
	return "Duration("+start.toString()+","+end.toString()+")";
}

public int add(StringBuilder sb, boolean qualify) {
	sb.append("(");
	end.add(sb, qualify);
	sb.append("-");
	start.add(sb, qualify);
	sb.append(")");
	return 1;
}
public List<PatternArgument> getParameters(List<PatternArgument> list) {
	list = end.getParameters(list);
	return start.getParameters(list);
}


public Duration makeObject(ResultSet rs, int pos) throws DataException {
	
	try {
		return new Duration(rs.getLong(pos),1L);
	} catch (SQLException e) {
		throw new DataFault("Error making duration expression",e);
	}
}
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
		SQLAndFilter fil = new SQLAndFilter(null);
		fil.addFilter(a_fil);
		fil.addFilter(b_fil);
		return fil;
	}
}

}