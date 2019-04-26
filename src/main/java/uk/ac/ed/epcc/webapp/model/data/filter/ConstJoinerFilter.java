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

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to a specific row of a remote table.
 * 
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public final class ConstJoinerFilter<T extends DataObject, BDO extends DataObject> implements SQLFilter<BDO>, JoinFilter<BDO> , LinkClause{
	public ConstJoinerFilter(Class<BDO> target, int id, Repository remote_res) {
		super();
		this.target = target;
		this.id=id;
		this.remote_res = remote_res;
	}





	private final Class<BDO> target;
	private final int id;
	private final Repository remote_res;
		
		
		
		

		
		public void accept(BDO o) {
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, BDO> vis)
				throws Exception {
			return vis.visitJoinFilter(this);
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<BDO> getTarget() {
			return  target;
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
}