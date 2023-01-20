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

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** A {@link FilterVisitor} that negates the selection of filter.
 * 
 * Ordering information is not preserved.
 * @author Stephen Booth
 * @param <T> type of filter
 *
 */
public class NegatingFilterVisitor<T extends DataObject> implements FilterVisitor<BaseFilter<T>, T> {

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
	public BaseFilter<T> visitPatternFilter(PatternFilter<T> fil) throws Exception {
		if( fil instanceof NegatingFilter) {
			return ((NegatingFilter) fil).getNested();
		}
		return new SQLNotFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
	 */
	@Override
	public BaseFilter<T> visitSQLCombineFilter(BaseSQLCombineFilter<T> fil) throws Exception {
		FilterCombination c = fil.getFilterCombiner();
		BaseCombineFilter<T> neg;
		if( c == FilterCombination.AND) {
			neg = new SQLOrFilter<>(fac.getTag());
		}else {
			neg = new SQLAndFilter<>(fac.getTag());
		}
		for( BaseFilter<T> f : fil.getSet()) {
			neg.add(f.acceptVisitor(this), false);
		}
		return neg;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
	 */
	@Override
	public BaseFilter<T> visitAndFilter(AndFilter<T> fil) throws Exception {
		if( ! fil.hasAcceptFilters()) {
			try {
				return FilterConverter.convert(fil).acceptVisitor(this);
			}catch(Exception t) {
				getLogger().error("Unexpected error: SQL convert failed", t);
			}
		}
		OrFilter<T> result = new OrFilter<>(fac.getTag(), fac);
		for(BaseFilter f : fil.getSet()) {
			result.add((BaseFilter<? super T>) f.acceptVisitor(this), false);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
	 */
	@Override
	public BaseFilter<T> visitOrFilter(OrFilter<T> fil) throws Exception {
		AndFilter<T> result = new AndFilter<T>(fac.getTag());
		for(BaseFilter f : fil.getSet()) {
			result.add( (BaseFilter) f.acceptVisitor(this), false);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter)
	 */
	@Override
	public BaseFilter<T> visitOrderFilter(SQLOrderFilter<T> fil) throws Exception {
		return fil;
	}
	/** A negating filter for pure accept filters.
	 * 
	 * @author Stephen Booth
	 *
	 * @param <X>
	 */
    private static final class PureNegatingAcceptFilter<X> implements AcceptFilter<X>,NegatingFilter<AcceptFilter<X>>{
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
		public String getTag() {
			return nested.getTag();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean test(X o) {
			return ! nested.test(o);
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((nested == null) ? 0 : nested.hashCode());
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
			PureNegatingAcceptFilter other = (PureNegatingAcceptFilter) obj;
			if (nested == null) {
				if (other.nested != null)
					return false;
			} else if (!nested.equals(other.nested))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PureNegatingAcceptFilter(" + nested + ")";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.NegatingFilter#getNested()
		 */
		@Override
		public AcceptFilter<X> getNested() {
			return nested;
		}
    	
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
	 */
	@Override
	public BaseFilter<T> visitAcceptFilter(AcceptFilter<T> fil) throws Exception {
		if( fil instanceof NegatingFilter) {
			return ((NegatingFilter) fil).getNested();
		}
		// Can be treated as a pure fulter due to visitor contract
		return new PureNegatingAcceptFilter<>(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
	 */
	@Override
	public BaseFilter<T> visitJoinFilter(JoinFilter<T> fil) throws Exception {
		return (BaseFilter<T>) fil;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
	 */
	@Override
	public BaseFilter<T> visitBinaryFilter(BinaryFilter<T> fil) throws Exception {
		return new GenericBinaryFilter<>( ! fil.getBooleanResult());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
	 */
	@Override
	public BaseFilter<T> visitBinaryAcceptFilter(BinaryAcceptFilter<T> fil) throws Exception {
		return visitBinaryFilter(fil);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
	 */
	@Override
	public BaseFilter<T> visitDualFilter(DualFilter<T> fil) throws Exception {
		
		return new DualFilter((SQLFilter)fil.getSQLFilter().acceptVisitor(this), (AcceptFilter)fil.getAcceptFilter().acceptVisitor(this));
	}

	private Logger log;
	public Logger getLogger() {
		if( log == null) {
			log=fac.getContext().getService(LoggerService.class).getLogger(getClass());
		}
		return log;
	}
	/** static helper method to negate filters
	 * 
	 * @param fac {@link DataObjectFactory}
	 * @param input original {@link BaseFilter}
	 * @return negated {@link BaseFilter}
	 * @throws Exception
	 */
	public static <X extends DataObject> BaseFilter<X> negate(DataObjectFactory<X> fac, BaseFilter<X> input) throws Exception{
		NegatingFilterVisitor<X> vis = new NegatingFilterVisitor<>(fac);
		return input.acceptVisitor(vis);
	}
}
