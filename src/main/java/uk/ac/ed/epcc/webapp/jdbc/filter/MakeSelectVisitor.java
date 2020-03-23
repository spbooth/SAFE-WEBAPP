//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.Repository;

/** A {@link FilterVisitor} that makes the select clause for a filter
 * 
 * @author spb
 *
 * @param <T> type of filter
 */
public class MakeSelectVisitor<T> implements FilterVisitor<StringBuilder, T>{
	public MakeSelectVisitor(Set<Repository> tables,StringBuilder sb,boolean qualify,boolean require_sql) {
		super();
		this.tables=tables;
		this.sb = sb;
		this.qualify=qualify;
		this.require_sql=require_sql;
	}

	private final StringBuilder sb;
	private final Set<Repository> tables;
	private final boolean qualify;
	private final boolean require_sql;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public StringBuilder visitPatternFilter(PatternFilter<T> fil) throws Exception {
		return fil.addPattern(tables,sb, qualify);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public StringBuilder visitSQLCombineFilter(BaseSQLCombineFilter<T> fil) throws Exception {
		if( fil.useBinary(true)) {
			// we can ignore joins and order
			return visitBinaryFilter(fil);
		}
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public StringBuilder visitAndFilter(AndFilter<T> fil) throws Exception {
		if( fil.useBinary(true)) {
			// we can ignore joins and order
			return visitBinaryFilter(fil);
		}
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public StringBuilder visitOrFilter(OrFilter<T> fil) throws Exception {
		if( fil.nonSQL() ){
			return visitAcceptFilter(fil);
		}
		return fil.getSQLFilter().acceptVisitor(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	@Override
	public StringBuilder visitOrderFilter(SQLOrderFilter<T> fil) throws Exception {
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public StringBuilder visitAcceptFilter(AcceptFilter<T> fil) throws Exception {
		if( require_sql){
			throw new NoSQLFilterException();
		}
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public StringBuilder visitJoinFilter(JoinFilter<T> fil) throws Exception {
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public StringBuilder visitBinaryFilter(BinaryFilter<T> fil) throws Exception {
		sb.append(" ");
		sb.append(Boolean.toString(fil.getBooleanResult()));
		sb.append(" ");
		return sb;
	}
	
	private StringBuilder doTrue(){
		sb.append(" true ");
		return sb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public StringBuilder visitDualFilter(DualFilter<T> fil) throws Exception {
		
		return fil.getSQLFilter().acceptVisitor(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
	 */
	@Override
	public StringBuilder visitBinaryAcceptFilter(BinaryAcceptFilter<T> fil) throws Exception {
		return visitBinaryFilter(fil);
	}
}