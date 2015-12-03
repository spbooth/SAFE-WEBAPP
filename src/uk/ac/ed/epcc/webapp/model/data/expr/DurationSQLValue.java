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
import java.util.Date;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Duration;



public class DurationSQLValue implements SQLValue<Duration> {
  private final SQLValue<Date> start, end;
  int offset;
  public DurationSQLValue(SQLValue<Date> start, SQLValue<Date> end){
	  this.start=start;
	  this.end=end;
  }

public Class<? super Duration> getTarget() {
	return Duration.class;
}
@Override
public String toString() {
	return "Duration("+start.toString()+","+end.toString()+")";
}

public int add(StringBuilder sb, boolean qualify) {
	offset = start.add(sb, qualify);
	sb.append(" , ");
	return offset + end.add(sb, qualify);
}
public List<PatternArgument> getParameters(List<PatternArgument> list) {
	list = start.getParameters(list);
	return end.getParameters(list);
}


public Duration makeObject(ResultSet rs, int pos) throws DataException {
	
	return new Duration(start.makeObject(rs, pos), end.makeObject(rs, pos+offset));
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