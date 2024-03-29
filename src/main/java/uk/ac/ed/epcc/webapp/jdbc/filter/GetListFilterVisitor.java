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

import java.util.List;

/** a {@link FilterVisitor} that extracts any {@link PatternArgument}s needed by the filter.
 * 
 * This has to correspond to the arguments needed by the {@link MakeSelectVisitor}.
 * 
 * @author spb
 *
 * @param <T> type of filter.
 */
public class GetListFilterVisitor<T> implements FilterVisitor<List<PatternArgument>, T>{
	public GetListFilterVisitor(List<PatternArgument> list,boolean require_sql) {
		super();
		this.list = list;
		this.require_sql = require_sql;
	}

	private final List<PatternArgument> list;
	private final boolean require_sql;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public List<PatternArgument> visitPatternFilter(PatternFilter<T> fil) throws Exception {
		return fil.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public List<PatternArgument> visitSQLCombineFilter(BaseSQLCombineFilter<T> fil) throws Exception {
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public List<PatternArgument> visitAndFilter(AndFilter<T> fil) throws Exception {
		return visitPatternFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public List<PatternArgument> visitOrFilter(OrFilter<T> fil) throws Exception {
		// We can only use the SQL version here if there
		// are no joins because joins would have required corresponding
		// changes elsewhere in the query.
		// 
		
		return visitAcceptFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
	 */
	@Override
	public List<PatternArgument> visitOrderFilter(SQLOrderFilter<T> fil) throws Exception {
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public List<PatternArgument> visitAcceptFilter(AcceptFilter<T> fil) throws Exception {
		if( require_sql){
			throw new NoSQLFilterException();
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public List<PatternArgument> visitJoinFilter(JoinFilter<T> fil) throws Exception {
		return list;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public List<PatternArgument> visitBinaryFilter(BinaryFilter<T> fil) throws Exception {
		return list;
	}
	
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public List<PatternArgument> visitDualFilter(DualFilter<T> fil) throws Exception {
		return fil.getSQLFilter().acceptVisitor(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
	 */
	@Override
	public List<PatternArgument> visitBinaryAcceptFilter(BinaryAcceptFilter<T> fil) throws Exception {
		return visitBinaryFilter(fil);
	}

	
}