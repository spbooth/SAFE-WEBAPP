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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterConverter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterFinder;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultIterator;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
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
public class TupleFactory<A extends DataObject, AF extends DataObjectFactory<A>, T extends TupleFactory.Tuple<A>> implements Contexed, Targetted<T> {
	private final Map<String,AF> factories;
	private final  AppContext conn;
	public TupleFactory(AppContext c,AF ... fac) {
		super();
		this.conn=c;
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
	public AppContext getContext(){
		return conn;
	}
	public Logger getLogger(){
		return getContext().getService(LoggerService.class).getLogger(getClass());
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
			super(TupleFactory.this.getTarget(), fil);
		}

		/**
		 * @param target
		 */
		public TupleAndFilter() {
			super(TupleFactory.this.getTarget());

		}
		
		public class AddMemberVisitor extends AbstractAddFilterVisitor<A>{
			

			public AddMemberVisitor(String tag) {
				super();
				this.tag = tag;
			}

			public final String tag;

			

			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
			 */
			@Override
			public Boolean visitAcceptFilter(AcceptFilter<? super A> fil) throws Exception {
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
	public class AcceptFilterConverter extends AbstractAcceptFilter<T>{
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
			super(TupleFactory.this.getTarget());
			this.tag = tag;
			this.inner = inner;
		}
		private final String tag;
		private final AcceptFilter<? super A> inner;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(T o) {
			return inner.accept(o.get(tag));
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
    		super(TupleFactory.this.getContext(), TupleFactory.this.getTarget());
			mapper = new TupleMapper();
			setMapper(mapper);
			setQualify(true);
    	}
		/**
		 * @param c
		 * @param target
		 * @throws DataFault 
		 */
		public TupleIterator(BaseFilter<? super T> fil, int start, int max) throws DataFault {
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
    	
    }
    protected class FilterSet extends AbstractFilterResult<T> implements Iterable<T>, FilterResult<T>{
		private BaseFilter<T> f;
        // create the first iterator in the constructor
        // to give it a chance to throw any exceptions.
        
        private Iterator<T> iter=null;
        int start;
        int max;
        public FilterSet(BaseFilter<T> f) throws DataFault{
        	
        	
			this.f=f;
			
        	start=-1;
        	max=-1;
        	iter = makeIterator();
        }
        public FilterSet(SQLFilter f, int start, int max) throws DataFault{
        	this.f=f;
        	this.start=start;
        	this.max=max;
        	iter = makeIterator();
        }
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#iterator()
		 */
		public Iterator<T> iterator() {
			if( iter != null){
				Iterator<T> temp = iter;
				iter=null;
				return temp;
			}
			try{
				return makeIterator();
			}catch(DataFault e){
				TupleFactory.this.getLogger().error("Error making iterator for FilterSet",e);
				return null;
			}
		}
		protected Iterator<T> makeIterator() throws DataFault {
			if( start < 0 ){
				return new TupleIterator(f);
			}else{
				return new TupleIterator(f, start,max);
			}
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
			super(TupleFactory.this.getContext(),Tuple.class,allow_null);
		}
		
		public AbstractFinder() {
			super(TupleFactory.this.getContext(),Tuple.class);
		}
		@Override
		protected final void addSource(StringBuilder sb) {
			TupleFactory.this.addSource(sb);
		}

		@Override
		protected final String getDBTag() {
			return TupleFactory.this.getDBTag();
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
    public final long getCount(BaseFilter<T> s) throws DataException{
    	try{
			SQLFilter<T> sql_fil = FilterConverter.convert(s);
			FilterCounter counter = new FilterCounter();
			return ((Long)counter.find(sql_fil)).longValue();
		}catch(NoSQLFilterException e){
			// do things the hard way
			long count=0;
			Iterator<T> it = new TupleIterator(s);
			while(it.hasNext()){
				count++;
				it.next();
			}
			return count;
		}
    }

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super T> getTarget() {
		return Tuple.class;
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
}
