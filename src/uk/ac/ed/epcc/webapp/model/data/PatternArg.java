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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;

/** Class encoding the arguments for a PatternFilter
 * @author spb
 *
 */


public class PatternArg implements PatternArgument{
	String field;
	Object arg;
	Repository res;
	boolean can_log=true;
	public PatternArg(Repository res, String field, Object arg){
		this.field = field;
		this.arg = arg;
		this.res=res;
	}
	public void addArg(PreparedStatement stmt,int pos) throws SQLException{
		if(res == null){
			stmt.setObject(pos, arg);
			return;
		}
		res.setObject(stmt, pos, field, res.convert(field, arg));
		
	}
	public boolean canLog(){
		return can_log;
	}
	public void setLog(boolean log){
		can_log=false;
	}
	/** get a field name with the same type as this argument
	 * or null for default conversion.
	 * @return String or null
	 */
	public String getField(){ return field; }
	/** get the object to apply to this parameter
	 * 
	 * @return Object
	 */
	public Object getArg(){ return arg; }
}