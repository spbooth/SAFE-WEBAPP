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
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;


/** A {@link SQLValue} that returns the first non null result from a set of {@link SQLValue}s
 * 
 * @author spb
 *
 * @param <T> type of SQLValue
 */
public class SQLSelectValue<T> implements GroupingSQLValue<T> {
    private final Class<T> target;
    private final SQLValue<T> accessors[];
    private final int offsets[];
    
    public SQLSelectValue(Class<T> target, SQLValue<T> accessors[]){
    	this.target=target;
    	this.accessors=accessors;
    	offsets=new int[accessors.length];
    }
	
	public Class<T> getTarget() {
		return target;
	}

	
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Select(");
		boolean seen=false;
		for(SQLValue<T> a: accessors){
			if( seen ){
				sb.append(",");
			}
			seen=true;
			sb.append(a.toString());
		}
		sb.append(")");
		return sb.toString();
	}

	public int add(StringBuilder sb, boolean qualify) {
		int count=0;
		for(int i=0;i<accessors.length;i++){
			if( i > 0){
				sb.append(" , ");
			}
			offsets[i]=count;
			count += accessors[i].add(sb, qualify);
		}
		
		return count;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(int i=0;i<accessors.length;i++){
			list = accessors[i].getParameters(list);
		}
		return list;
	}
	
	public T makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		for(int i=0;i<accessors.length;i++){
			T tmp = accessors[i].makeObject(rs, pos+offsets[i]);
			if( tmp != null ){
				if( tmp instanceof IndexedReference){
					if( ! ((IndexedReference)tmp).isNull()){
						return tmp;
					}
				}else{
					return tmp;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public SQLFilter getRequiredFilter() {
		SQLAndFilter required = null;
		for(int i=0;i<accessors.length;i++){
			SQLFilter tmp = accessors[i].getRequiredFilter();
			if( tmp != null){
				if(required==null){
					required=new SQLAndFilter(tmp.getTag());
				}
				required.addFilter(tmp);
			}
		}
		return required;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#addGroup(java.lang.StringBuilder, boolean)
	 */
	@Override
	public int addGroup(StringBuilder sb, boolean qualify) {
		int count=0;
		boolean seen=false;
		for(SQLValue<T> v : accessors) {
			if( v instanceof GroupingSQLValue) {
				StringBuilder tmp = new StringBuilder();
				if( count > 0) {
					tmp.append(" , ");
				}
				int added = ((GroupingSQLValue<T>)v).addGroup(tmp, qualify);
				if( added > 0 ) {
					count += added;
					sb.append(tmp);
				}
			}else {
				throw new ConsistencyError(v.toString()+" not a GroupingSQLValue");
			}
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#getGroupParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		for(SQLValue<T> v : accessors) {
			if( v instanceof GroupingSQLValue) {
				list = ((GroupingSQLValue<T>)v).getGroupParameters(list);
			}else {
				list = v.getParameters(list);
			}
		}
		return list;
	}

	@Override
	public boolean checkContentsCanGroup() {
		for(SQLValue<T> v : accessors) {
			if( ! (v instanceof GroupingSQLValue)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getFilterTag() {
		for(SQLValue<T> v : accessors) {
			String t = v.getFilterTag();
			if( t != null) {
				return t;
			}
		}
		return null;
	}

}