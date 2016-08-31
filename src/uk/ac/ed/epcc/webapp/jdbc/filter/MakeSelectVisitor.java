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

/** A {@link FilterVisitor} that makes the select clause for a filter
 * 
 * @author spb
 *
 * @param <T> type of filter
 */
public class MakeSelectVisitor<T> implements FilterVisitor<StringBuilder, T>{
	public MakeSelectVisitor(StringBuilder sb,boolean qualify,boolean require_sql) {
		super();
		this.sb = sb;
		this.qualify=qualify;
		this.require_sql=require_sql;
	}

	private final StringBuilder sb;
	private final boolean qualify;
	private final boolean require_sql;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public StringBuilder visitPatternFilter(PatternFilter<? super T> fil) throws Exception {
		return fil.addPattern(sb, qualify);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public StringBuilder visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) throws Exception {
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public StringBuilder visitAndFilter(AndFilter<? super T> fil) throws Exception {
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public StringBuilder visitOrFilter(OrFilter<? super T> fil) throws Exception {
		if( fil.nonSQL() ){
			visitAcceptFilter(fil);
		}
		return fil.getSQLFilter().acceptVisitor(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	@Override
	public StringBuilder visitOrderFilter(OrderFilter<? super T> fil) throws Exception {
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public StringBuilder visitAcceptFilter(AcceptFilter<? super T> fil) throws Exception {
		if( require_sql){
			throw new NoSQLFilterException();
		}
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public StringBuilder visitJoinFilter(JoinFilter<? super T> fil) throws Exception {
		return doTrue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public StringBuilder visitBinaryFilter(BinaryFilter<? super T> fil) throws Exception {
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
	public StringBuilder visitDualFilter(DualFilter<? super T> fil) throws Exception {
		
		return fil.getSQLFilter().acceptVisitor(this);
	}
}