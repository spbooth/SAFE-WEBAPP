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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;



public class SetMapper<O> implements ResultMapper<Set<O>> {
	
    private SQLValue<O> target; 
   
    protected boolean qualify;
    
    public SetMapper(SQLValue<O> target) {
    	this(target, false);
    	
    }
    
    /** Make a TableMapper
     * @param target 
     * @param qualify 
     * 
     */
    public SetMapper(SQLValue<O> target,  boolean qualify) {
    	this.target = target;
    	this.qualify = qualify;
    	
    }
    
	@Override
	public String getTarget() {
		StringBuilder sb = new StringBuilder();
		sb.append("DISTINCT ");
		target.add(sb,qualify);
		return sb.toString();
		
	}
	
	@Override
	public boolean setQualify(boolean qualify) {
		boolean old = this.qualify;
		this.qualify = qualify;
		return old;
		
	}

	@Override
	public Set<O> makeDefault() {
		return new HashSet<>();
		
	}

	@Override
	public Set<O> makeObject(ResultSet rs) throws DataException, SQLException {
		Set<O> set = new HashSet<>();

		do {
			set.add(target.makeObject(rs, 1));

		} while (rs.next());


		return set;

	}

	

	@Override
	public SQLFilter getRequiredFilter() {
		return target.getRequiredFilter();
	}

	@Override
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
		return target.getParameters(list);
	}
	@Override
	public String getModify() {
		//TODO is it always valid to order by an arbitrary SQLValue
		//StringBuilder sb = new StringBuilder();
		//sb.append(" ORDER BY ");
		//target.add(sb, qualify);
		return null;
	}
	@Override
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
		//target.getParameters(list);
		return list;
	}

}