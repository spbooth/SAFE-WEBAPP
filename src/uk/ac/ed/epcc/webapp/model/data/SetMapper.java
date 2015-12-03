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
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;



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
    
	public String getTarget() {
		StringBuilder sb = new StringBuilder();
		sb.append("DISTINCT ");
		target.add(sb,qualify);
		return sb.toString();
		
	}
	
	public boolean setQualify(boolean qualify) {
		boolean old = this.qualify;
		this.qualify = qualify;
		return old;
		
	}

	public Set<O> makeDefault() {
		return new HashSet<O>();
		
	}

	public Set<O> makeObject(ResultSet rs) throws DataException {
		Set<O> set = new HashSet<O>();
		try {
			do {
				set.add(target.makeObject(rs, 1));
				
			} while (rs.next());
			
		} catch (SQLException ex) {
			throw new DataFault("Error making object",ex);
		}		
		return set;
		
	}

	public String getModify() {
		return "";
	}

	public SQLFilter getRequiredFilter() {
		return target.getRequiredFilter();
	}

	public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
		return target.getParameters(list);
	}

	public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
		return list;
	}

}