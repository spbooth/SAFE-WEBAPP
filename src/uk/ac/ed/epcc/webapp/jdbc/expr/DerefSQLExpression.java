// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.IndexedFieldValue;
/** A join based SQLExpression for accessing remote tables.
 * 
 * @author spb
 *
 * @param <H> home object
 * @param <R> remote object
 * @param <T> target type
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DerefSQLExpression.java,v 1.3 2015/11/11 09:28:03 spb Exp $")

public class DerefSQLExpression<H extends DataObject,R extends DataObject,T> implements SQLExpression<T>{
	
	private SQLExpression<T> remote_expression;
	private SQLFilter required_filter;
	@SuppressWarnings("unchecked")
	public DerefSQLExpression(IndexedFieldValue<H,R> a,SQLExpression<T> expr ) throws Exception {
		remote_expression = expr;
		required_filter = a.getSQLFilter(remote_expression.getRequiredFilter());
	}
	public int add(StringBuilder sb, boolean qualify) {
		return remote_expression.add(sb, qualify);
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return remote_expression.getParameters(list);
	}
	
	public T makeObject(ResultSet rs, int pos) throws DataException {
		return remote_expression.makeObject(rs, pos);
	}

	public SQLFilter getRequiredFilter() {
		return required_filter;
		
	}

	public Class<? super T> getTarget() {
		return remote_expression.getTarget();
	}

}