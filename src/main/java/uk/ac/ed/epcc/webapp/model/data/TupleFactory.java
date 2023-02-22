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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.ResultSet;
import java.util.*;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** Factory for sets of {@link DataObject}s generated by a join.
 * Each result in the join becomes a tuple. Where a record from one of the contributing
 * tables appears in multiple results from the join it also appears in multiple tuples.
 *  
 * @author spb
 * @param <A> type of {@link DataObject}
 * @param <AF> type of {@link DataObjectFactory}
 * @param <T> type of {@link Tuple}
 *
 */
public class TupleFactory<A extends DataObject, AF extends DataObjectFactory<A>, T extends TupleFactory.Tuple<A>> extends AbstractContexed implements  Owner<T> {
	private final Map<String,AF> factories;
	public TupleFactory(AppContext c,AF ... fac) {
		super(c);
		factories = new LinkedHashMap<>();
		for(AF  f : fac){
			addFactory(f);
		}
	}
	
	public void addFactory(AF fac){
		factories.put(fac.getTag(), fac);
	}
	/**
	 * @return the a_fac
	 */
	public AF getFactory(String tag) {
		return factories.get(tag);
	}
	
	
	
	public static class Tuple<A extends DataObject> extends LinkedHashMap<String, A>{
		
		
	}
	
	public T makeTuple(){
		return (T) new Tuple<A>();
	}
	public void addSource(StringBuilder sb) {
		boolean seen=false;
		for ( AF fac : factories.values()){
			if(seen){
				sb.append(" join ");
			}
			fac.res.addSource(sb, true);
			seen=true;
		}
	}
	public final Set<Repository> getSourceTables(){
		HashSet<Repository> set = new HashSet<>();
		for ( AF fac : factories.values()){
			set.add(fac.res);
		}
		return set;
	}
	public String getDBTag() {
		return factories.values().iterator().next().res.getDBTag();
	}
	/** A combination filter for {@link Tuple}s
	 *  
	 *  Filters on member types can be added
	 * 
	 * @author spb
	 *
	 */
	public class TupleAndFilter extends AndFilter<T>{
	
		public TupleAndFilter(BaseFilter<? super T>... fil) {
			super(null, fil);
		}

	
		public TupleAndFilter() {
			super(null);

		}
		
		public class AddMemberVisitor extends AddFilterVisitor{
			

			public AddMemberVisitor(String tag) {
				super();
				this.tag = tag;
			}

			public final String tag;

			

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
			 */
			@Override
			public Boolean visitAcceptFilter(AcceptFilter fil) throws Exception {
				addAccept(new AcceptFilterConverter(tag, fil));
				return null;
			}

			
		}
		public void addMemberFilter(String tag,BaseFilter<? super A> ... fil) throws Exception{
			if(! factories.containsKey(tag)){
				throw new CannotFilterException("tag "+tag+" not a member");
			}
			AddMemberVisitor vis = new AddMemberVisitor(tag);
			for(BaseFilter<? super A> f: fil){
				f.acceptVisitor(vis);
			}
		}
		
	}
	/** An {@link AcceptFilter} that forwards onto an {@link AcceptFilter} on a member
	 * 
	 * @author spb
	 *
	 */
	public class AcceptFilterConverter implements AcceptFilter<T>{
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((inner == null) ? 0 : inner.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AcceptFilterConverter other = (AcceptFilterConverter) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (inner == null) {
				if (other.inner != null)
					return false;
			} else if (!inner.equals(other.inner))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			return true;
		}
		public AcceptFilterConverter(String tag, AcceptFilter<? super A> inner) {
			this.tag = tag;
			this.inner = inner;
		}
		private final String tag;
		private final AcceptFilter<? super A> inner;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean test(T o) {
			return inner.test(o.get(tag));
		}
		private TupleFactory getOuterType() {
			return TupleFactory.this;
		}
	}
    public class TupleMapper implements ResultMapper<T>{
    	// always qualify
    	private static final boolean qualify=true;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#setQualify(boolean)
		 */
		@Override
		public boolean setQualify(boolean qualify) {
//			boolean old = this.qualify;
//			this.qualify=qualify;
//			return old;
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeObject(java.sql.ResultSet)
		 */
		@Override
		public T makeObject(ResultSet rs) throws DataException {
			T res = makeTuple();
			for(String tag : factories.keySet()){
				res.put(tag, getFactory(tag).makeObject(rs, qualify));
			}
			return res;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#makeDefault()
		 */
		@Override
		public T makeDefault() {
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getTarget()
		 */
		@Override
		public String getTarget() {
			StringBuilder sb = new StringBuilder();
			boolean seen=false;
			for ( AF fac : getMemberFactories()){
				if(seen){
					sb.append(",");
				}
				fac.res.addAlias(sb, true);
				sb.append(".*");
				seen=true;
			}
			return sb.toString();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getTargetParameters(java.util.List)
		 */
		@Override
		public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
			return list;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getModify()
		 */
		@Override
		public String getModify() {
			StringBuilder sb = new StringBuilder();
			sb.append(" ORDER BY ");
			boolean seen=false;
			for ( AF fac : getMemberFactories()){
				if(seen){
					sb.append(",");
				}
				fac.res.addUniqueName(sb, true, true);
				seen=true;
			}
			return sb.toString();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getModifyParameters(java.util.List)
		 */
		@Override
		public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
			return list;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper#getRequiredFilter()
		 */
		@Override
		public SQLFilter getRequiredFilter() {
			return null;
		}
    	
    }
    
    /** Iterator over {@link Tuple}s 
     * Run-time type checking on the filters is disabled so SQL filters of either factory
     * (and join expressions) can be used to filter the Tuple.
     * 
     * @author spb
     *
     */
    public class TupleIterator extends ResultIterator<T>{

    	private final TupleMapper mapper;
    	
    	protected TupleIterator(){
    		super(TupleFactory.this.getContext(), null);
			mapper = new TupleMapper();
			setMapper(mapper);
			setQualify(true);
    	}
		
		public TupleIterator(BaseFilter<T> fil, int start, int max) throws DataFault {
			this();
			try {
				setup(fil, start, max);
			} catch (DataException e) {
				throw new DataFault("Error making iterator", e);
			}
		}

		/**
		 * @param s
		 * @throws DataFault 
		 */
		public TupleIterator(BaseFilter<T> s) throws DataFault {
			this();
			try {
				setup(s, 0, -1);
			} catch (DataException e) {
				throw new DataFault("Error making iterator", e);
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getDBTag()
		 */
		@Override
		protected String getDBTag() {
			return TupleFactory.this.getDBTag();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#addSource(java.lang.StringBuilder)
		 */
		@Override
		protected void addSource(StringBuilder sb) {
			TupleFactory.this.addSource(sb);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getQualify()
		 */
		@Override
		public boolean getQualify() {
			return true;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getSourceTables()
		 */
		@Override
		protected Set<Repository> getSourceTables() {
			return TupleFactory.this.getSourceTables();
		}
    	
    }
    protected class FilterSet extends AbstractFilterResult<T> implements Iterable<T>, FilterResult<T>{
		private BaseFilter<T> f;
        
        int start;
        int max;
        public FilterSet(BaseFilter<T> f) throws DataFault{
        	
        	
			this.f=f;
			
        	start=-1;
        	max=-1;
        }
        public FilterSet(SQLFilter f, int start, int max) throws DataFault{
        	this.f=f;
        	this.start=start;
        	this.max=max;
        }
		
		protected CloseableIterator<T> makeIterator() throws DataFault {
			if( start < 0 ){
				return new TupleIterator(f);
			}else{
				return new TupleIterator(f, start,max);
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.AbstractFilterResult#getLogger()
		 */
		@Override
		protected Logger getLogger() {
			return TupleFactory.this.getLogger();
		}
      
	}
    public FilterResult<T> makeResult(BaseFilter<T> f) throws DataFault{
    	return new FilterSet(f);
    }
    public FilterResult<T> makeResult(SQLFilter<? super T> f,int start,int max) throws DataFault{
    	return new FilterSet(f,start,max);
    }
    protected abstract class AbstractFinder<X> extends FilterFinder<T, X>{
		public AbstractFinder(boolean allow_null) {
			super(TupleFactory.this.getContext(),null,allow_null);
		}
		
		public AbstractFinder() {
			super(TupleFactory.this.getContext(),null);
		}
		@Override
		protected final void addSource(StringBuilder sb) {
			TupleFactory.this.addSource(sb);
		}

		@Override
		protected final String getDBTag() {
			return TupleFactory.this.getDBTag();
		}
		@Override
		protected final Set<Repository> getSourceTables() {
			return TupleFactory.this.getSourceTables();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterReader#getQualify()
		 */
		@Override
		public final boolean getQualify() {
			return true;
		}
    }
    public class FilterCounter extends AbstractFinder{

		public FilterCounter() {
			super();
			CounterMapper m = new CounterMapper();
			m.setQualify(true);
			setQualify(true);
			setMapper(m);
		}
    	
    }
    public class FilterExists extends AbstractFinder{

		public FilterExists() {
			super();
			ExistsMapper m = new ExistsMapper();
			m.setQualify(true);
			setQualify(true);
			setMapper(m);
		}
    	
    }
    public final long getCount(BaseFilter<T> s) throws DataException{
    	try{
			SQLFilter<T> sql_fil = FilterConverter.convert(s);
			FilterCounter counter = new FilterCounter();
			return ((Long)counter.find(sql_fil)).longValue();
		}catch(NoSQLFilterException e){
			// do things the hard way
			long count=0;
			try(CloseableIterator<T> it = new TupleIterator(s)){
				while(it.hasNext()){
					count++;
					it.next();
				}
			} catch (Exception e1) {
				throw new DataException("error closing iterator",e1);
			}
			return count;
		}
    }
    public final boolean exists(BaseFilter<T> s) throws DataException{
    	try{
			SQLFilter<T> sql_fil = FilterConverter.convert(s);
			FilterExists exists = new FilterExists();
			return  (boolean) exists.find(sql_fil);
		}catch(NoSQLFilterException e){
			// do things the hard way
			long count=0;
			try(CloseableIterator<T> it = new TupleIterator(s)){
				if( it.hasNext()) {
					return true;
				}
			} catch (Exception e1) {
				throw new DataException("error closing iterator",e1);
			}
			return false;
		}
    }
	

	/** get the component factories of the tuples
	 * @return
	 */
	public Collection<AF> getMemberFactories() {
		return factories.values();
	}
	
	public boolean hasMemberFactories() {
		return factories != null && ! factories.isEmpty();
	}

	@Override
	public boolean isMine(Object target) {
		if( target == null) {
			return false;
		}
		if( target instanceof Tuple) {
			Tuple<DataObject> t = (Tuple<DataObject>) target;
			if( t.size() != factories.size()) {
				return false; // wrong size of tuple
			}
			for(Map.Entry<String, DataObject> e : t.entrySet()) {
				String key = e.getKey();
				AF fac = factories.get(key);
				if( fac == null || ! fac.isMine(e.getValue())) {
					return false;
				}
			}
		}
		return false;
	}
}
