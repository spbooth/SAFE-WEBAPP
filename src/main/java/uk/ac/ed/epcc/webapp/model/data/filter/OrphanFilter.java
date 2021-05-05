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

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.MultiTableFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to select from a table where a local reference field  fails to 
 * resolve
 * 
 
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public class OrphanFilter<T extends DataObject, BDO extends DataObject> extends FilterSelect<T> implements SQLFilter<BDO>, PatternFilter<BDO>,MultiTableFilter {
	private final Class<BDO> target;
	private final String join_field;
	private final Repository res;
	private final Repository remote_res;
	/**
	 * 
	 * @param target type of target
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 */
	public OrphanFilter( Class<BDO> target,String join_field, Repository res, Repository remote_res){
		this.target=target;
		this.join_field=join_field;
		this.res=res;
		this.remote_res=remote_res;
	}
		
		
	
		
		public final List<PatternArgument> getParameters(List<PatternArgument> list) {
			return list;
		}
		
		public final StringBuilder addPattern(Set<Repository> tables,StringBuilder sb, boolean qualify) {
			assert( ! tables.contains(remote_res));
			// this is the clause that matches the tables.
			sb.append("NOT EXISTS( SELECT 1 FROM ");
			
			remote_res.addSource(sb, true);
			sb.append(" WHERE ");
	     	res.getInfo(join_field).addName(sb, true, true);
	        sb.append(" = ");
	        remote_res.addUniqueName(sb, true, true);

			sb.append(")");
			return sb;
		}


	


		
		public final void accept(BDO o) {
		}





		





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public final Class<BDO> getTarget() {
			return target;
		}




		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MultiTableFilter#qualifyTables()
		 */
		@Override
		public boolean qualifyTables() {

			return true;
		}




		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((join_field == null) ? 0 : join_field.hashCode());
			result = prime * result + ((remote_res == null) ? 0 : remote_res.hashCode());
			result = prime * result + ((res == null) ? 0 : res.hashCode());
			result = prime * result + ((target == null) ? 0 : target.hashCode());
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
			OrphanFilter other = (OrphanFilter) obj;
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
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}



}