//| Copyright - The University of Edinburgh 2018                            |
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

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A {@link FilterVisitor} that negates the selection of filter.
 * 
 * Ordering information is not preserved.
 * @author Stephen Booth
 *
 */
public class NegatingFilterVisitor<T extends DataObject> implements FilterVisitor<BaseFilter<? super T>, T> {

	/**
	 * @param fac
	 */
	public NegatingFilterVisitor(DataObjectFactory<T> fac) {
		super();
		this.fac = fac;
	}

	private final DataObjectFactory<T> fac;
 	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
	 */
	@Override
	public BaseFilter<? super T> visitPatternFilter(PatternFilter<? super T> fil) throws Exception {
		return new SQLNotFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public BaseFilter<? super T> visitSQLCombineFilter(BaseSQLCombineFilter<? super T> fil) throws Exception {
		return visitJoinFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public BaseFilter<? super T> visitAndFilter(AndFilter<? super T> fil) throws Exception {
		OrFilter<T> result = new OrFilter<>(fac.getTarget(), fac);
		for(BaseFilter f : fil.getSet()) {
			result.add((BaseFilter<? super T>) f.acceptVisitor(this), false);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public BaseFilter<? super T> visitOrFilter(OrFilter<? super T> fil) throws Exception {
		AndFilter<? super T> result = new AndFilter<>(fil.getTarget());
		for(BaseFilter f : fil.getSet()) {
			result.add( (BaseFilter) f.acceptVisitor(this), false);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter)
	 */
	@Override
	public BaseFilter<? super T> visitOrderFilter(SQLOrderFilter<? super T> fil) throws Exception {
		return fil;
	}
	/** A negating filter for pure accept filters.
	 * 
	 * @author Stephen Booth
	 *
	 * @param <X>
	 */
    private static final class PureNegatingAcceptFilter<X> implements AcceptFilter<X>{
    	/**
		 * @param nested
		 */
		public PureNegatingAcceptFilter(AcceptFilter<X> nested) {
			super();
			this.nested = nested;
		}

		private final AcceptFilter<X> nested;
    	
    
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		@Override
		public Class<? super X> getTarget() {
			return nested.getTarget();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(X o) {
			return ! nested.accept(o);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		@Override
		public <Y> Y acceptVisitor(FilterVisitor<Y, ? extends X> vis) throws Exception {
			return vis.visitAcceptFilter(this);
		}
    	
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public BaseFilter<? super T> visitAcceptFilter(AcceptFilter<? super T> fil) throws Exception {
		// Can be treated as a pure fulter due to visitor contract
		return new PureNegatingAcceptFilter<>(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public BaseFilter<? super T> visitJoinFilter(JoinFilter<? super T> fil) throws Exception {
		return new NotJoinFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public BaseFilter<? super T> visitBinaryFilter(BinaryFilter<? super T> fil) throws Exception {
		return new GenericBinaryFilter<>(fil.getTarget(), ! fil.getBooleanResult());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
	 */
	@Override
	public BaseFilter<? super T> visitBinaryAcceptFilter(BinaryAcceptFilter<? super T> fil) throws Exception {
		return visitBinaryFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public BaseFilter<? super T> visitDualFilter(DualFilter<? super T> fil) throws Exception {
		
		return new DualFilter((SQLFilter)fil.getSQLFilter().acceptVisitor(this), (AcceptFilter)fil.getAcceptFilter().acceptVisitor(this));
	}

}
