// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
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
@uk.ac.ed.epcc.webapp.Version("$Id: OrphanFilter.java,v 1.1 2015/02/10 11:09:11 spb Exp $")

public class OrphanFilter<T extends DataObject, BDO extends DataObject> extends FilterSelect<T> implements SQLFilter<BDO>, PatternFilter<BDO> {
	private final Class<? super BDO> target;
	private final String join_field;
	private final Repository res;
	private final Repository remote_res;
	/**
	 * 
	 * @param join_field String reference field
	 * @param res        Repository of target
	 * @param remote_res Repository of remote
	 */
	public OrphanFilter( Class<? super BDO> target,String join_field, Repository res, Repository remote_res){
		this.target=target;
		this.join_field=join_field;
		this.res=res;
		this.remote_res=remote_res;
	}
		
		
		
		
		
		@SuppressWarnings("unchecked")
		
		public final List<PatternArgument> getParameters(List<PatternArgument> list) {
			return list;
		}
		
		public final StringBuilder addPattern(StringBuilder sb, boolean qualify) {
			// this is the clause that matches the tables.
			sb.append("NOT EXISTS( SELECT 1 FROM ");
			remote_res.addTable(sb, true);
			
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
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public final <X> X acceptVisitor(FilterVisitor<X, ? extends BDO> vis)
				throws Exception {
			return vis.visitPatternFilter(this);
		}





		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public final Class<? super BDO> getTarget() {
			return target;
		}
}