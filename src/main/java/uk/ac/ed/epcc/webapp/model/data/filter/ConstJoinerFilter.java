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

import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to a specific row of a remote table.
 * There are no linking fields between the two tables. It just adds in a single
 * row or the remote table to all rows of the result set.
 * 
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public final class ConstJoinerFilter<T extends DataObject, BDO extends DataObject> implements SQLFilter<BDO>, JoinFilter<BDO> , LinkClause{
	/**
	 * 
	 * @param target_tag  tag of target table
	 * @param id
	 * @param remote_res
	 */
	public ConstJoinerFilter(String target_tag, int id, Repository remote_res) {
		super();
		this.target_tag = target_tag;
		this.id=id;
		this.remote_res = remote_res;
	}





	private final String target_tag;
	private final int id;
	private final Repository remote_res;
		
		


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
	@Override
		public String getTag() {
			return  target_tag;
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MultiTableFilter#qualifyTables()
		 */
		@Override
		public boolean qualifyTables() {
			return true;
		}


		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter#addJoin(java.util.Set, java.lang.StringBuilder, java.util.Set)
		 */
		@Override
		public void addJoin(Set<Repository> tables, StringBuilder join, Set<LinkClause> additions) {
			if( tables.contains(remote_res)) {
				additions.add(this);
			}else {
				join.append(" join ");
				remote_res.addSource(join, true);
				join.append(" on ");
				
				join.append(" ");
				addLinkClause(join);
			}
			
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.filter.LinkClause#addLinkClause(java.lang.StringBuilder)
		 */
		@Override
		public void addLinkClause(StringBuilder join) {
			remote_res.addUniqueName(join, true, true);
			join.append("=");
			join.append(Integer.toString(id));
			
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			result = prime * result + ((remote_res == null) ? 0 : remote_res.hashCode());
			result = prime * result + ((target_tag == null) ? 0 : target_tag.hashCode());
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
			ConstJoinerFilter other = (ConstJoinerFilter) obj;
			if (id != other.id)
				return false;
			if (remote_res == null) {
				if (other.remote_res != null)
					return false;
			} else if (!remote_res.equals(other.remote_res))
				return false;
			if (target_tag == null) {
				if (other.target_tag != null)
					return false;
			} else if (!target_tag.equals(other.target_tag))
				return false;
			return true;
		}





		
}