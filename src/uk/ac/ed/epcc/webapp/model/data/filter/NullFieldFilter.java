// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.model.data.Repository;

/** filter to select on null/non-null fields 
 * 
 * @author spb
 * @param <T> Type of filter
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NullFieldFilter.java,v 1.7 2014/09/15 14:30:31 spb Exp $")

public class NullFieldFilter<T> implements PatternFilter<T>, SQLFilter<T>{
	private final Class<? super T> target;
    private final boolean match_null;
    private final String field;
    private final Repository res;
    public NullFieldFilter(Class<? super T> target,Repository res,String field, boolean match_null){
    	this.target=target;
    	this.field=field;
    	this.match_null=match_null;
    	this.res = res;
    }
	public StringBuilder  addPattern(StringBuilder target,boolean qualify) {
		if( res != null ){
			res.getInfo(field).addName(target, qualify, true);
		}else{
			//TODO note this assumes mysql quoting if repository not present
			target.append("`");
			target.append(field);
			target.append("`");
		}
		if(match_null){
			target.append(" IS NULL ");
		}else{
			target.append(" IS NOT NULL ");
		}
		return target;
	}

	public void accept(T o) {
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (match_null ? 1231 : 1237);
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
		NullFieldFilter other = (NullFieldFilter) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (match_null != other.match_null)
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}
	
}