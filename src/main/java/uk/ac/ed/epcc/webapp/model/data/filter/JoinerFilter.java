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

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;

/** Filter to join two tables.
 * 
 * This performs a left join so records without references are
 * available for selection but the filter visitor in {@link BaseCombineFilter} also adds the joining clause
 * to the WHERE condition which would normally suppress these. This is to allow
 * OR combinations of filters to work. The records without references are
 * available to OR branches that do not contain this filter.
 * 
 * We still have a problem with reversed references (remote table points to source)
 * if different remote peers match different branches of an OR then the target will 
 * be matched multiple times so we should consider using a {@link BackJoinFilter} instead for these
 * if we don't actually need the joined fields in the result.
 * 
 * This filter cannot be used if multiple joins to the same table are required because table aliases are not supported.
 * 
 * @author spb
 *
 * @param <REMOTE> remote table type
 * @param <TARGET> target table type
 */


public class JoinerFilter<TARGET extends DataObject,REMOTE extends DataObject > implements SQLFilter<TARGET>, JoinFilter<TARGET>, LinkClause,Contexed {
	
	protected final String join_field;
	protected final Repository res;
	protected final Repository remote_res;
	
	/**
	 * 
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 */
	public JoinerFilter(String join_field, Repository res, Repository remote_res){
		this.join_field=join_field;
		this.res=res;
		this.remote_res=remote_res;
		
		assert(res.hasField(join_field));
	}
		
		
		
		
		
		





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.filter.LinkClause#addLinkClause(java.lang.StringBuilder)
		 */
		@Override
		public void addLinkClause(StringBuilder join) {
			join.append("(");
			
     		FieldInfo info = res.getInfo(join_field);
			info.addName(join, true, true);
         	join.append(" = ");
         	remote_res.addUniqueName(join, true, true);
     	
         	join.append(")");
		}
	

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((join_field == null) ? 0 : join_field.hashCode());
			result = prime * result
					+ ((remote_res == null) ? 0 : remote_res.hashCode());
			result = prime * result + ((res == null) ? 0 : res.hashCode());
			
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
			JoinerFilter other = (JoinerFilter) obj;
			if (join_field == null) {
				if (other.join_field != null)
					return false;
			} else if (!join_field.equals(other.join_field))
				return false;
			if (remote_res == null) {
				if (other.remote_res != null)
					return false;
			} else if (!remote_res.equals(other.remote_res))
				return false;
			if (res == null) {
				if (other.res != null)
					return false;
			} else if (!res.equals(other.res))
				return false;
			return true;
		}



		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public String getTag() {
			return res.getTag();
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("(");
			
				res.getInfo(join_field).addName(sb, true, false);
				sb.append("=");
				remote_res.addUniqueName(sb, true, false);
			sb.append(")");
			return sb.toString();
		}





		@Override
		public boolean qualifyTables() {
			return true;
		}


        Repository getTargetRes() {
        	return res;
        }
        Repository getRemoteRes() {
        	return remote_res;
        }


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter#addJoin(java.util.Set, java.lang.StringBuilder, java.util.Set)
		 */
		@Override
		public void addJoin(Set<Repository> tables, StringBuilder join_clause, Set<LinkClause> additions) {
			if( tables.contains(remote_res) && tables.contains(res)) {
				additions.add(this);
			}else {
				if( ! tables.contains(res) && ! tables.contains(remote_res)) {
					throw new BadJoinException("Neither end of join already in query");
				}
				//assert( tables.contains(res) || tables.contains(remote_res));
				if( ! tables.contains(remote_res)) {
					// see comments for reason for left join
					join_clause.append(" left join ");
					remote_res.addSource(join_clause, true);
					join_clause.append(" on ");
					addLinkClause(join_clause);
					tables.add(remote_res);
				}
				if( ! tables.contains(res)) {
					// This allows us to use a simple join when using a unique remote reference
					if( ! res.getInfo(join_field).isUnique()) {
						throw new BadJoinException("simple back join to non unique field "+join_field);
					}
					//assert(res.getInfo(join_field).isUnique());
					// see comments for reason for left join
					join_clause.append(" left join ");
					res.addSource(join_clause, true);
					join_clause.append(" on ");
					addLinkClause(join_clause);
					tables.add(res);
				}
			}
			
		}











		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
		 */
		@Override
		public AppContext getContext() {
			return res.getContext();
		}
}