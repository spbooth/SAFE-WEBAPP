// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.List;


@uk.ac.ed.epcc.webapp.Version("$Id: FalseFilter.java,v 1.5 2014/09/15 14:30:25 spb Exp $")

/** A {@link SQLFilter} that never selects anything.
 * 
 * @author spb
 *
 * @param <T>
 */
public class FalseFilter<T> implements PatternFilter<T>, SQLFilter<T> {

	public FalseFilter(Class<? super T> target) {
		super();
		this.target = target;
	}

	private final Class<? super T> target;
	public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
		sb.append(" 1 != 1 ");
		return sb;
	}

	public void accept(T o) {

	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	public <X> X acceptVisitor(FilterVisitor<X,? extends T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}
}