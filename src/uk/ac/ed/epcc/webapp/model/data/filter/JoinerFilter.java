// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** Filter to join two tables.
 * 
 * This performs a left join so records without references are
 * available for selection but it also adds the joining clause
 * to the WHERE condition which would normally suppress these. This is to allow
 * OR combinations of filters to work. The records without references are
 * available to OR branches that do not contain this filter.
 * 
 * We still have a problem with reversed references (remote table points to source)
 * if different remote peers match different branches of an OR then the target will 
 * be matched multiple times so we should consider using a {@link BackJoinFilter} instead for these
 * if we don't actually need the joined fields in the result.
 * 
 * @author spb
 *
 * @param <T> remote table type
 * @param <BDO> target table type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JoinerFilter.java,v 1.5 2014/09/15 14:30:31 spb Exp $")

public final class JoinerFilter<T extends DataObject, BDO extends DataObject> implements SQLFilter<BDO>, JoinFilter<BDO> {
	private final Class<? super BDO> target;
	private final String join_field;
	private final Repository res;
	private final Repository remote_res;
	private final boolean target_references;
	/**
	 * 
	 * @param target type of filter target
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 * @param target_references boolean true if target points to remote. false if remote points to target
	 */
	public JoinerFilter(Class<? super BDO> target, String join_field, Repository res, Repository remote_res, boolean target_references){
		this.target=target;
		this.join_field=join_field;
		this.res=res;
		this.remote_res=remote_res;
		this.target_references=target_references;
	}
		
		
		
		
		
		public String getJoin() {
			StringBuilder join=new StringBuilder();
			// see comments for reason for left join
			join.append(" left join ");
			remote_res.addTable(join, true);
			join.append(" on ");
			addPattern(join, true);
			return join.toString();
		}
		
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			return list;
		}
		
		public StringBuilder addPattern(StringBuilder join, boolean qualify) {
			// this is the clause that matches the tables.
			join.append("(");
			if( target_references){
	     		res.getInfo(join_field).addName(join, true, true);
	         	join.append(" = ");
	         	remote_res.addUniqueName(join, true, true);
	     	}else{
	     		remote_res.getInfo(join_field).addName(join, true, true);
	         	join.append(" = ");
	         	res.addUniqueName(join, true, true);
	     	}
			join.append(")");
			return join;
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
			result = prime * result + (target_references ? 1231 : 1237);
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
			if (target_references != other.target_references)
				return false;
			return true;
		}


		
		public void accept(BDO o) {
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, ? extends BDO> vis)
				throws Exception {
			return vis.visitJoinFilter(this);
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<? super BDO> getTarget() {
			return target;
		}
}