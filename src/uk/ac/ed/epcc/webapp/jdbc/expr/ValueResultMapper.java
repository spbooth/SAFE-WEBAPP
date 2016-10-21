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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** A resultMapper that returns a single value specified by a {@link SQLValue}
 * 
 * @author spb
 *
 * @param <O>
 */


public class ValueResultMapper<O> implements ResultMapper<O> {
    private SQLValue<O> expr;
    private boolean qualify=false;
    public ValueResultMapper(SQLValue<O> e){
    	assert(e != null);
    	expr=e;
    }
	public String getModify() {
		return null;
	}

	public String getTarget(){
		StringBuilder sb = new StringBuilder();
		expr.add(sb, qualify);
		return sb.toString();
	}

	public O makeDefault() {
		return null;
	}

	public O makeObject(ResultSet rs) throws DataException {
		O res =  expr.makeObject(rs, 1);
		return res;
	}

	public boolean setQualify(boolean qualify) {
		boolean old=qualify;
		this.qualify=qualify;
		return old;
	}
	public SQLFilter getRequiredFilter() {
		return expr.getRequiredFilter();
	}
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
		return expr.getParameters(list);
	}
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
		return list;
	}

}