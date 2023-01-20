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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.PatternArg;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Simple SQLFilter to match based on the value of a field.
 * 
 *  The filter is constructed with a Repository so the field name
 *  can be qualified. It is legal but to pass null (the filter will select all records) but not recommended
 *  as this will cause problems if filters are combined via a join.
 * 
 * @author spb
 * @param <T> Type of object selected
 *
 */


public class SQLValueFilter<T> implements SQLFilter<T>, PatternFilter<T> {
	 private final Object peer;
     private final String field;
     private final Repository parent;
     private final String match;
     
     public SQLValueFilter(Repository res,String field,Object peer){
        this(res,field,peer,false);
     }
     public SQLValueFilter(Repository res,String field,Object peer, boolean negate ){
    	this(res,field,negate ? "!=" : "= ",peer);
     }
     public SQLValueFilter(Repository res, String field,MatchCondition cond, Object peer){
    	 this(res,field,cond.match(),peer);
     }
     private SQLValueFilter(Repository res,String field,String match,Object peer){
    	 parent=res;
    	 this.field=field;
    	 this.peer=peer;
    	 this.match=match;
    	 assert(parent.hasField(field));
     }
	public final List<PatternArgument> getParameters(List<PatternArgument> list){
		if( peer != null ){
		  list.add(new PatternArg(parent,field,peer));
		}
		return list;
	}

	public final StringBuilder addPattern(Set<Repository> tables,StringBuilder result,boolean qualify) {
		if( peer == null ){
			result.append("true"); // default to true
			return result;
		}
		
		// Search form may have a null repository
		if(  parent != null  ){
			FieldInfo info = parent.getInfo(field);
			if( info != null ){
				
				info.addName(result, qualify, true);
			}else{
				//if( parent.getUniqueIdName().equals(field)){
					
				//	parent.addUniqueName(result, qualify, true);
				//}else{
					throw new ConsistencyError("Field "+field+" not recognised ");
				//}
			}
		}else{
			result.append("`"+field+"`");
		}
		result.append(match);
		result.append("?");
		return result;

	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((match == null) ? 0 : match.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((peer == null) ? 0 : peer.hashCode());
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
		SQLValueFilter other = (SQLValueFilter) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (match == null) {
			if (other.match != null)
				return false;
		} else if (!match.equals(other.match))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (peer == null) {
			if (other.peer != null)
				return false;
		} else if (!peer.equals(other.peer))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public final String getTag() {
		return parent.getTag();
	}
	/** Make sure sub-classes don't try to imlement OrderFilter as well
	 * 
	 */
	public final void OrderBy(){
		
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SQLValueFilter(");
		if( parent != null ) {
			parent.getInfo(field).addName(sb, true, false);
		}else {
			sb.append(field);
		}
		sb.append(match);
		sb.append(peer.toString());
		sb.append(")");
		return sb.toString();
	}
}