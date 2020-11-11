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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
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


public final class BackJoinFilter<T extends DataObject, BDO extends DataObject> extends FilterSelect<T> implements SQLFilter<BDO>, PatternFilter<BDO>, MultiTableFilter,Contexed {
	private final Class<BDO> target;
	// Note this is a filter on the remote that points back to the target
	private final JoinerFilter<BDO, T> link;
	private final SQLFilter<T> fil;
	
	/**
	 * 
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 * @param fil 
	 */
	public BackJoinFilter( Class<BDO> target,String join_field, Repository res, Repository remote_res, SQLFilter<T> fil){
		this.target=target;
		this.link = new JoinerFilter<>((Class<T>) (fil != null ? fil.getTarget(): DataObject.class), join_field, remote_res, res);
		this.fil=fil;
	}
		
	
		
		
		
		
	/**
	 * @param target
	 * @param link
	 * @param fil
	 */
	public BackJoinFilter(Class<BDO> target, JoinerFilter<BDO, T> link, SQLFilter<T> fil) {
		super();
		this.target = target;
		this.link = link;
		this.fil = fil;
	}






		@Override
		@SuppressWarnings("unchecked")
		
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			return getFilterArguments(fil, list);
		}
		
		@Override
		public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb, boolean qualify) {
			Set<Repository> inner_tables = new HashSet<>(tables);
			// this is the clause that matches the tables.
			sb.append("EXISTS( SELECT 1 FROM ");
			link.getTargetRes().addSource(sb, true);
			inner_tables.add(link.getTargetRes());
			
			Set<LinkClause> additions = new LinkedHashSet<>();
			additions.add(link);
			if( fil != null && fil instanceof JoinFilter){
				((JoinFilter)fil).addJoin(inner_tables, sb, additions);
			}
			sb.append(" WHERE ");
			for(LinkClause l : additions) {
				link.addLinkClause(sb);
				sb.append(" AND ");
			}
	        if( fil != null ){
	        	sb.append("("); // might be an OR combinations
	        	makeWhere(inner_tables,fil, sb, true);
	        	sb.append(")");
	        }else {
	        	sb.append("true");
	        }
			sb.append(")");
			return sb;
		}


	


		
		@Override
		public void accept(BDO o) {
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		@Override
		public <X> X acceptVisitor(FilterVisitor<X, BDO> vis)
				throws Exception {
			return vis.visitBackJoinFilter(this);
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		@Override
		public Class<BDO> getTarget() {
			return target;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			//sb.append("@");
			//sb.append(getTarget().getSimpleName());
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





		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fil == null) ? 0 : fil.hashCode());
			result = prime * result + ((link == null) ? 0 : link.hashCode());
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
			BackJoinFilter other = (BackJoinFilter) obj;
			if (fil == null) {
				if (other.fil != null)
					return false;
			} else if (!fil.equals(other.fil))
				return false;
			if (link == null) {
				if (other.link != null)
					return false;
			} else if (!link.equals(other.link))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}





		public JoinerFilter<BDO, T> getLink() {
			return link;
		}





		public SQLFilter<T> getFil() {
			return fil;
		}






		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
		 */
		@Override
		public AppContext getContext() {
			return link.getContext();
		}
}