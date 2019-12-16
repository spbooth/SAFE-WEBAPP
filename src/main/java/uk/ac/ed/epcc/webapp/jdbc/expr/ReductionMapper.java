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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;


/** A {@link ResultMapper} that maps a single SQL reduction to a value of the same type
 * 
 * @author Stephen Booth
 *
 * @param <R> type of result
 */
public class ReductionMapper<R> extends AbstractContexed implements ResultMapper<R> {

	private final Reduction op;
	private final Class<R> target;
	private final SQLExpression<? extends R> exp;
	private final R def;
	private boolean qualify=false;

	/**
	 * 
	 * @param c   {@link AppContext}
	 * @param target type of result
	 * @param op {@link Reduction} operation
	 * @param def default result
	 * @param exp {@link SQLExpression}
	 */
	public ReductionMapper(AppContext c,Class<R> target, Reduction op,R def,SQLExpression<? extends R> exp) {
		super(c);
		this.target=target;
		this.op=op;
		this.exp=exp;
		this.def=def;
	}

	@Override
	public String getModify() {
		return null;
	}

	@Override
	public String getTarget() {
		StringBuilder sb = new StringBuilder();
		SQLExpression expr=null;
		switch(op){
		case SUM:  expr = FuncExpression.apply(conn,SQLFunc.SUM,target,exp);break;
		case MIN:  expr = FuncExpression.apply(conn,SQLFunc.MIN,target,exp);break;
		case MAX:  expr = FuncExpression.apply(conn,SQLFunc.MAX,target,exp);break;
		case AVG:  expr = FuncExpression.apply(conn,SQLFunc.AVG,target,exp);break;
		}
		if( expr == null ){
			throw new ConsistencyError("reduction did not generate expression");
		}
		expr.add(sb, qualify);
		return sb.toString();
	}

	@Override
	public R makeDefault() {
		return def;
	}

	@Override
	public R makeObject(ResultSet rs) throws DataException, SQLException {
		if( target.isAssignableFrom(exp.getTarget())) {
			return (R) exp.makeObject(rs, 1);
		}
		return (R) rs.getObject(1);
	}

	@Override
	public boolean setQualify(boolean qualify) {
		boolean old_q = this.qualify;
		this.qualify=qualify;
		return old_q;
	}
	@Override
	public SQLFilter getRequiredFilter() {
		return exp.getRequiredFilter();
	}

	@Override
	public List<PatternArgument> getTargetParameters(
			List<PatternArgument> list) {
		return exp.getParameters(list);
	}

	@Override
	public List<PatternArgument> getModifyParameters(
			List<PatternArgument> list) {
		return list;
	}

}