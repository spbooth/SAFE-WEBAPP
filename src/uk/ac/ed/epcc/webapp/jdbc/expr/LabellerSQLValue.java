// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Labeller;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;

/**
 * @author spb
 * @param <T> type of nested SQLValue
 * @param <R> type of returned object
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: LabellerSQLValue.java,v 1.5 2014/09/15 14:30:23 spb Exp $")
public class LabellerSQLValue<T,R> implements SQLValue<R> {

	public LabellerSQLValue(AppContext c,Labeller<T,R> labeller, SQLValue<T> nested) {
		super();
		this.conn=c;
		this.labeller = labeller;
		this.nested = nested;
	}
	private final AppContext conn;
	private final Labeller<T,R> labeller;
	private final SQLValue<T> nested;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super R> getTarget() {
		return labeller.getTarget();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#add(java.lang.StringBuilder, boolean)
	 */
	public int add(StringBuilder sb, boolean qualify) {
		return nested.add(sb,qualify);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return nested.getParameters(list);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#makeObject(java.sql.ResultSet, int)
	 */
	public R makeObject(ResultSet rs, int pos) throws DataException {
		return labeller.getLabel(conn, nested.makeObject(rs, pos));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue#getRequiredFilter()
	 */
	public SQLFilter getRequiredFilter() {
		return nested.getRequiredFilter();
	}
	@Override
	public String toString() {
		return labeller.getClass().getSimpleName()+"("+nested.toString()+")";
	}

}
