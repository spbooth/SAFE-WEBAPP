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

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MultiTableFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to select from a table based on the contents of
 * a table that points to it.
 * 
 * To avoid expansion of the results when multiple peers point
 * to the same target we use a dependent sub-query. The remote fields will not be
 * available in the result and will have to be evaluated by an accessor or method
 * but the selection expression is in the WHERE clause and should
 * combine ok with AND/OR.
 * 
 * Dependent sub-queries have the reputation of being slow however
 * we need this to be safe for reduction operations where duplicate results
 * would give the wrong answer.
 * 
 * It we need the remote fields in the result it would be better to query on the remote table
 * and use a normal join back to the target table.
 * 
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */


public final class BackJoinFilter<T extends DataObject, BDO extends DataObject> extends FilterSelect<T> implements SQLFilter<BDO>, PatternFilter<BDO>, MultiTableFilter {
	private final Class<? super BDO> target;
	// Note this is a filter on the remote that points back to the target
	private final JoinerFilter<BDO, T> link;
	private final SQLFilter<? super T> fil;
	private final Repository remote_res;
	/**
	 * 
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 * @param fil 
	 */
	public BackJoinFilter( Class<? super BDO> target,String join_field, Repository res, Repository remote_res, SQLFilter<? super T> fil){
		this.target=target;
		this.link = new JoinerFilter<BDO,T>((Class<? super T>) (fil != null ? fil.getTarget(): DataObject.class), join_field, remote_res, res);
		this.remote_res=remote_res;
		this.fil=fil;
	}
		
		
		
		
		
		@SuppressWarnings("unchecked")
		
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			return getFilterArguments(fil, list);
		}
		
		public StringBuilder addPattern(StringBuilder sb, boolean qualify) {
			// this is the clause that matches the tables.
			sb.append("EXISTS( SELECT 1 FROM ");
			remote_res.addSource(sb, true);
			if( fil != null && fil instanceof JoinFilter){
				final String join = ((JoinFilter)fil).getJoin();
				if( join != null ){
					sb.append(" ");
					sb.append(join);
				}
			}
			sb.append(" WHERE ");
	     	link.addLinkClause(sb);
	        if( fil != null ){
	        	sb.append(" AND ");
	        	makeWhere(fil, sb, true);
	        }
			sb.append(")");
			return sb;
		}


	


		
		public void accept(BDO o) {
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, ? extends BDO> vis)
				throws Exception {
			return vis.visitPatternFilter(this);
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<? super BDO> getTarget() {
			return target;
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("(");
			sb.append(link.toString());
			if( fil != null ) {
				sb.append(" remote_filter=");
				sb.append(fil.toString());
			}
			sb.append(")");
			return sb.toString();
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MultiTableFilter#qualifyTables()
		 */
		@Override
		public boolean qualifyTables() {
			return true;
		}
}