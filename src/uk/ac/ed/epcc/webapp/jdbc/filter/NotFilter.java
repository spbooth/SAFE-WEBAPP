// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.List;


/** Invert the sense of a {@link SQLFilter}
 * @author spb
 * @param <Q>  type of filter
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NotFilter.java,v 1.5 2014/09/15 14:30:25 spb Exp $")
public class NotFilter<Q> implements SQLFilter<Q>,PatternFilter<Q>{
	//TODO convert this to use the {@link SQLFilterVisitor}
	private final PatternFilter<Q> nested;
	
	public NotFilter(SQLFilter<Q> fil){
		this.nested=(PatternFilter<Q>) fil;
	}
	
	public PatternFilter<Q> getNested(){
		return nested;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X,? extends Q> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
	 */
	public void accept(Q o) {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return nested.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#addPattern(java.lang.StringBuilder, boolean)
	 */
	public StringBuilder addPattern(StringBuilder sb, boolean qualify) {
		sb.append("NOT(");
		nested.addPattern(sb, qualify);
		sb.append(")");
		return sb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super Q> getTarget() {
		return nested.getTarget();
	}

}
