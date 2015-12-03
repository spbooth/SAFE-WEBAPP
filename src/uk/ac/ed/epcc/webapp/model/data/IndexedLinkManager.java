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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultIterator;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.iterator.DecoratingIterator;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;
import uk.ac.ed.epcc.webapp.model.history.HistoryHandler;
import uk.ac.ed.epcc.webapp.model.history.LinkHistoryHandler;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/**
 * IndexedLinkManager is the base class of Factories that manage Link objects These
 * classes manage many-to-one relationships between model level objects. The <code>IndexedLinkManager</code>
 * class calls these the <it>left</it> and <it>right</it> objects. 
 * This call can be used to link any objects that implement {@link Indexed}. If both ends of the
 * link are {@link DataObject} classes then the {@link LinkManager} sub-class should be used as this
 * provides additional features.
 * 
 * 
 * The actual <code>Link</code> DataObjects are pseudo inner classes of the <code>IndexedLinkManager</code>
 * Often the Link objects will remain hidden inside the <code>IndexedLinkManager</code> sub-class and only be
 * manipulated via an external interface. If we want to track the history of a Link object needs a status
 * field that marks if the link is valid. This way the same object is used to record the link between two peer objects
 * even if the link is broken and remade several times. <code>Link</code> can be selected based on the objects at the
 * end on the link. e.g.
<code>
<pre>
    // get a link
    Link l = manager.getLink(left_peer,right_peer);  // null if link does not exist
    
    // or
    Link l2 = manager.makeLink(left_peer,right_peer);  // make link if it does not already exist
</pre>
</code>
 * <p>
 * IndexedLinkManager can navigate the links in either direction the subclass may
 * choose only to expose one direction of navigation. Internally this is implemented using the
 * <code>LinkFilter</code> and <code>LinkFilterIterator</code> classes which select a set a <code>Link</code> objects
 * based on the value of one or other end.
<code>
<pre>
   // links joining left_peer to something
   Iterator&lt;Link&gt; it = manager.getLinkIterator(left_peer,null,filter);

   // or go straight to the referenced object
   Iterator&lt;Right&gt;  right_it = manager.getRightIterator(left_peer,null,filter);
</pre>
</code>
 * <p>
 * The Link class can cache references to the objects it points to, these cached values are retrieved using
 * the <code>getLeft()</code> and <code>getRight()</code> methods. 
 * <p>
 * 
 * @author spb
 * @param <T> Type of link object
 * @param <L> type of left end 
 * @param <R> type of right end
 * 
 */
public abstract class IndexedLinkManager<T extends IndexedLinkManager.Link<L,R>,L extends Indexed,R extends Indexed> extends DataObjectFactory<T> {

	/**
	 * Decorator to turn Iterators over Links into Left objects
	 * 
	 * @author spb
	 * 
	 */


	public class LeftIterator extends DecoratingIterator<L,T> {

		public LeftIterator(Iterator<? extends T> i) {
			super(i);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.data.DecoratingIterator#next()
		 */
		@Override
		public L next() {
			T o = nextInput();
			try {
				L result = o.getLeft();
				assert(result != null);
				o.release();
				return result;
			} catch (DataException e) {
				o.getContext().error(e, "Error getting left object");
				return null;
			}
		}

	}

	/**
	 * Link is an object representing an entry in a linkage table. Link objects
	 * are not primary model objects themselves but represent many to one
	 * relationships between model objects. To a first approximation Link
	 * objects only contain references to the 2 model objects on the ends of the
	 * links In many cases we will also want to add status and Date fields
	 * <p>
	 * 
	 * Link objects are pseudo inner classes.
	 * We make the manage reference explicit to avoid Link being generic parameterised by itself.
	 * <p>
	 * The appropriate LinkManager Link should
	 * always be subclasses to improve type safety. The <code>getLeft()</code>/<code>getRight()</code> methods are
	 * made protected to force us to add sensibly named accessors in sub-classes
	 * 
	 * @author spb
	 * @param <L> Left end type
	 * @param <R> Right end type
	 * 
	 */
	public abstract static class Link<L extends Indexed, R extends Indexed> extends DataObject {
		private L left = null;
		private R right = null;
		
        private IndexedLinkManager<?,L,R> manager;
		protected Link(IndexedLinkManager<?,L,R> man,Repository.Record res) {
			super(res);
			manager=man;
		}
		/** allow sub-classes to get the LinkManager
		 * don't make the return type generic its much easier for  sub-classes to implement getFactory() methods
		* that cast correctly than have a generic signature that can be used use-fully
		 * type checking is preserved by getting the constructor right 
		 * @return
		 */
        protected IndexedLinkManager getIndexedLinkManager(){
        	return manager;
        }
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#commit()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void post_commit(boolean changed) throws DataFault {
		
			if (changed && manager.useAutoHistory() ){

				LinkHistoryHandler handler = (LinkHistoryHandler)manager.getHistoryHandler();
				if( handler != null && handler.isValid()){
					try {
						// cast to avoid generic problems as we don't know the real link type here
						((LinkHistoryHandler) manager.getHistoryHandler()).update(this);
					} catch (Exception e) {
						getContext().error(e, "Error updating Link History");
					}
				}
			}
		}

		@Override
		public void release() {
			left = null;
			right = null;
			manager=null;
			super.release();
		}

	

		

		/**
		 * get the Left end object
		 * 
		 * @return DataObject
		 * @throws DataException
		 */
		protected final L getLeft() throws DataException {
			if (left == null) {
				left = manager.getLeftProducer().find(getLeftID().intValue());
			}
			return left;
		}

		protected final Number getLeftID() {
			return (Number) record.getRequiredProperty(manager.left_field);
		}

		/**
		 * get the Right end object
		 * 
		 * @return DataObject
		 * @throws DataException
		 */
		protected final R getRight() throws DataException {
			if (right == null) {
				right =  manager.getRightProducer().find(getRightID().intValue());
			}
			return right;
		}

		protected final Number getRightID() {
			return (Number) record.getRequiredProperty(manager.right_field);
		}
       
		public final boolean isLeftPeer(L o){
        	
        	boolean match =( o.getID() == getLeftID().intValue() &&
        					 manager.isLeft(o));
        	if( match && left == null){
        		left =  o;
        	}
        	return match;
        }
        public final boolean isRightPeer(R o){
        	
        	boolean match =( o.getID() == getRightID().intValue() &&
        					 manager.isRight(o));
        	if( match && right == null){
        		right =  o;
        	}
        	return match;
        }
		/**
		 * pass in a copy of the Left end object to initialise the cache.
		 * 	 Note that this method will do <em>NOTHING</em> if the argument is not the 
		 * current Left peer.
		 * @param o
		 *            DataObject
		 */
		protected final void setLeft(L o) {
			if (o == null || left != null) {
				return;
			}
			if (!manager.isLeft(o)) {
				throw new ClassCastException(
						"Illegal type passed to LinkManager");
			}
			// must be explicit getProperty as we are testing for uninitialised
			// field
			// and getLeftID uses getRequiredProperty
			Number left_id = record.getNumberProperty(manager.left_field);
			if (left_id == null) {
				// Uninitialised object
				record.setProperty(manager.left_field, o.getID());
				left = o;
			} else {
				if ( o.getID() == left_id.intValue()) {
					left = o;
				}else{
					throw new ConsistencyError("Incorrect peer value in LinkManager");
				}
			}
		}

		/**
		 * pass in a copy of the Right end object to initialise the cache.
		 * Note that this method will do <em>NOTHING</em> if the argument is not the 
		 * current Right peer.
		 * @param o
		 *            DataObject
		 */
		protected final void setRight(R o) {
			if (o == null || right != null) {
				return;
			}
			if (!manager.isRight(o)) {
				throw new ClassCastException(
						"Illegal type passed to LinkManager");
			}

			// must be explicit getProperty as we are testing for uninitialised
			// field
			// and getRightID uses getRequiredProperty
			Number right_id = record.getNumberProperty(manager.right_field);
			if (right_id == null) {
				// Uninitialised object
				record.setProperty(manager.right_field, o.getID());
				right = o;
			} else {
				if (o.getID() == right_id.intValue()) {
					right = o;
				}else{
					throw new ConsistencyError("Incorrect peer value in LinkManager");
				}
			}
		}

		/**
		 * extension point for Link subclasses this method is called when new
		 * records are created to initialise subclass fields to sensible default
		 * values
		 * 
		 * @throws Exception
		 * 
		 */
		protected abstract void setup() throws Exception;
	}

	/**
	 * Filter for link objects This class handles filtering by the end points
	 * and can be extended by composition for example to select objects with a
	 * particular status. This class also initialises the cache links for the
	 * objects it knows about.
	 * 
	 * @author spb
	 * 
	 */
	protected final class LinkFilter extends AndFilter<T> implements ResultVisitor<T>{
		private L left_target = null;

		private R right_target = null;


		/**
		 * create a LinkFilter
		 * 
		 * @param l
		 *            required Left DataObject null for any
		 * @param r
		 *            requires Right DataObject null for any
		 * @param f
		 *            extension Filter
		 */
		public LinkFilter(L l, R r, BaseFilter<? super T> f) {
			super(IndexedLinkManager.this.getTarget());
			left_target = l;
			right_target = r;
			if (l != null && !isLeft(l)) {
				throw new ClassCastException("Wrong Left Class in filter");
			}
			if (r != null && !isRight(r)) {
				throw new ClassCastException("Wrong Right Class in filter");
			}
			addFilter(f);
			if( l != null ){
			  addFilter(new ReferenceFilter<T,L>(IndexedLinkManager.this,left_field,l));
			}
			if( r != null ){
				  addFilter(new ReferenceFilter<T,R>(IndexedLinkManager.this,right_field,r));
			}
		}

		protected L getLeftTarget(){
			return left_target;
		}
		protected R getRightTarget(){
			return right_target;
		}
        public void visit(T l) {
				// Set cache values where we can. This happened in the LinkMapper if
				// we are using LinkFilterIterator but we have to do it here if we are using
				// a standard LinkFilter
				l.setLeft(left_target);
				l.setRight(right_target);
		}
	}
   

	/**
	 * Filter for link objects This class handles filtering by the end points
	 * and can be extended by composition for example to select objects with a
	 * particular status. This class also initialises the cache links for the
	 * objects it knows about.
	 * 
	 * @author spb
	 * 
	 */
	protected final class SQLLinkFilter extends SQLAndFilter<T> implements ResultVisitor<T>{
		private L left_target = null;

		private R right_target = null;


		/**
		 * create a LinkFilter
		 * 
		 * @param l
		 *            required Left DataObject null for any
		 * @param r
		 *            requires Right DataObject null for any
		 * @param f
		 *            extension Filter
		 */
		public SQLLinkFilter(L l, R r, SQLFilter<? super T> f) {
			super(IndexedLinkManager.this.getTarget());
			left_target = l;
			right_target = r;
			if (l != null && !isLeft(l)) {
				throw new ClassCastException("Wrong Left Class in filter");
			}
			if (r != null && !isRight(r)) {
				throw new ClassCastException("Wrong Right Class in filter");
			}
			addFilter(f);
			if( l != null ){
				addFilter(new ReferenceFilter<T,L>(IndexedLinkManager.this,left_field,l));
			}
			if( r != null ){
				addFilter(new ReferenceFilter<T,R>(IndexedLinkManager.this,right_field,r));
			}
		}

		protected L getLeftTarget(){
			return left_target;
		}
		protected R getRightTarget(){
			return right_target;
		}
        public void visit(T l) {
				// Set cache values where we can. This happened in the LinkMapper if
				// we are using LinkFilterIterator but we have to do it here if we are using
				// a standard LinkFilter
				l.setLeft(left_target);
				l.setRight(right_target);
		}
	}
   
	public class LinkFilterIterator extends ResultIterator<T> {

		public LinkFilterIterator(LinkFilter fil) throws DataFault {
			super(IndexedLinkManager.this.getContext(),IndexedLinkManager.this.getTarget());
			setMapper(new FilterAdapter());
			setVisitor(fil);
	    	try {
				setup(fil,0,-1);
			} catch (DataException e) {
				throw new DataFault("Error in setup", e);
			}
		}

		@Override
		protected void addSource(StringBuilder sb) {
			res.addTable(sb, true);
			
		}

		@Override
		protected String getDBTag() {
			return res.getDBTag();
		}
	}
	
	

	/**
	 * Decorator to turn Iterators over Links into Left objects
	 * 
	 * @author spb
	 * 
	 */
	public  class RightIterator extends DecoratingIterator<R,T> {

		public RightIterator(Iterator<? extends T> i) {
			super(i);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.data.DecoratingIterator#next()
		 */
		@Override
		public R next() {
			T o =  nextInput();
			try {
				R result = o.getRight();
				assert(result != null);
				o.release();
				return result;
			} catch (DataException e) {
				o.getContext().error(e, "Error getting right object");
				return null;
			}
		}

	}

	/**
	 * 
	 */
	private HistoryHandler<T> link_history;

	

	/**
	 * Field name for the left size of the link.
	 * 
	 */
	private String left_field="LeftID";

	/**
	 * Field name for the right size of the link.
	 * 
	 */
	private String right_field="RightID";

	protected IndexedLinkManager(AppContext c, String table,IndexedProducer<L> left_fac,
			String left_field, IndexedProducer<R> right_fac, String right_field) {
		SetContext(c, table, left_fac, left_field, right_fac, right_field);
	}
	protected IndexedLinkManager(){
		
	}
	protected void SetContext(AppContext c, String table,IndexedProducer<L> left_fac,
				String left_field, IndexedProducer<R> right_fac, String right_field) {	
		if( DataObjectFactory.AUTO_CREATE_TABLES_FEATURE.isEnabled(c)){
			setContextWithMake(c, table,getFinalTableSpecification(c,table,left_fac,left_field,right_fac,right_field));
		}else{
			setContext(c, table,false);
		}
		
		this.left_field = left_field;
		res.addTypeProducer(new IndexedTypeProducer<L,IndexedProducer<L>>(c,left_field, left_fac));
		this.right_field = right_field;
		res.addTypeProducer(new IndexedTypeProducer<R,IndexedProducer<R>>(c,right_field, right_fac));
	}

	// make final to stop people overriding the wrong method.
	protected final TableSpecification getDefaultTableSpecification(AppContext c,String table){
		return super.getDefaultTableSpecification(c, table);
	}
	protected TableSpecification getDefaultTableSpecification(AppContext c,String table,
			IndexedProducer<L> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		TableSpecification s = new TableSpecification();
		if( leftFac instanceof DataObjectFactory){
			s.setField(leftField, ((DataObjectFactory)leftFac).getReferenceFieldType());
		}else{
			s.setField(leftField, new IntegerFieldType());
		}
		if( rightFac instanceof DataObjectFactory){
			s.setField(rightField, ((DataObjectFactory)rightFac).getReferenceFieldType());
		}else{
			s.setField(rightField, new IntegerFieldType());
		}
		try {
			s.new Index("Link", true, leftField, rightField);
		} catch (InvalidArgument e) {
			getContext().error(e,"Error making index");
		}
		return s;
	}
	public final TableSpecification getFinalTableSpecification(AppContext c,String table,
			IndexedProducer<L> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {

		TableSpecification spec = getDefaultTableSpecification(c, table, leftFac, leftField, rightFac, rightField);
		if( spec != null ){
			for(TableStructureContributer<T> comp : getTableStructureContributers()){
				spec = comp.modifyDefaultTableSpecification(spec, table);
			}
		}
		return spec;
	}
	public final  HistoryHandler<T> getHistoryHandler() {

		if (link_history == null) {
			link_history =  makeHistoryHandler();
		}
		return link_history;
	}
	/** Set the {@link HistoryHandler} explicitly.
	 * Normally this is created using {@link #makeHistoryHandler()}
	 * but this allows us to set the reference when a handler is created
	 * directly.
	 * 
	 * @param handler
	 */
    public final void setHistoryHandler(HistoryHandler<T> handler){
    	link_history=handler;
    }
	
	@SuppressWarnings("unchecked")
	public IndexedProducer<L> getLeftProducer(){
		return ((IndexedTypeProducer<L,IndexedProducer<L>>)res.getInfo(left_field).getTypeProducer()).getProducer();
	}
    public String getLeftField(){
    	return left_field;
    }
    public SQLFilter<T> getLeftFilter(L l){
    	return new ReferenceFilter<T,L>(this,left_field,l);
    }
	/**
	 * create an Iterator over Left objects
	 * 
	 
	 * @param r
	 *            Right {@link Indexed} required null for any
	 * @param f
	 *            extension Filter
	 * @return Iterator over Left Objects
	 * @throws DataFault
	 */
	public Iterator<L> getLeftIterator( R r, BaseFilter<? super T> f)
			throws DataFault {
		return new LeftIterator(getLinkIterator(null, r, f));
	}
	/** Get a Set of Left  objects
	 * 
	 * @param r Right {@link Indexed} required null for any
	 * @param f extension Filter
	 * @return Set of Left objects
	 * @throws DataFault
	 */
	public Set<L> getLeftSet( R r ,BaseFilter<? super T> f) throws DataFault{
		Set<L> res= new LinkedHashSet<L>();
		for (Iterator<L> it = getLeftIterator(r, f); it.hasNext();){
			res.add(it.next());
		}
		return res;
	}

	/**
	 * get the Link object connecting the specified end objects
	 * 
	 * @param left_end
	 *            Left {@link Indexed}
	 * @param right_end
	 *            Right {@link Indexed}
	 * @return Link or null if link does not exist.
	 * @throws DataException
	 */

	public final T getLink(L left_end, R right_end)
			throws DataException {
		if( left_end == null || right_end == null){
			return null;
		}
		AppContext conn = getContext();
		String tag =getTag()+"-getLink";
		TimerService timer = conn.getService(TimerService.class);
		if( timer != null ){
		   timer.startTimer(tag);
		}
		try {
			SQLAndFilter<T> fil = new SQLAndFilter<T>(getTarget());
			fil.addFilter(new ReferenceFilter<T, L>(this, left_field, left_end));
			fil.addFilter(new ReferenceFilter<T, R>(this, right_field, right_end));
			T l = find(fil,true);
			
			if( l != null ){
			  l.setLeft(left_end);
			  l.setRight(right_end);
			}
			return l;
		} catch (MultipleResultException e) {
			
			throw new ConsistencyError("multiple Link entries for same pairing");
		}finally{
			if( timer != null ){
			  timer.stopTimer(tag);
			}
		}
	}

	

	/**
	 * create an Iterator over Link objects
	 * 
	 * @param l
	 *            Left {@link Indexed} required null for any
	 * @param r
	 *            Right {@link Indexed} required null for any
	 * @param f
	 *            extension Filter
	 * @return Iterator over Link
	 * @throws DataFault 
	 * @throws DataFault
	 */
	public Iterator<T> getLinkIterator(L l, R r, BaseFilter<? super T> f) throws DataFault{
		return new LinkFilterIterator(new LinkFilter(l, r, f));
	}
	/**
	 * create a {@link FilterResult} of Link objects
	 * 
	 * @param l
	 *            Left {@link Indexed} required null for any
	 * @param r
	 *            Right {@link Indexed} required null for any
	 * @param fil
	 *            extension Filter
	 * @return Iterator over Link
	 * @throws DataFault 
	 * @throws DataFault
	 */
	public FilterResult<T> getFilterResult(L l, R r, BaseFilter<T> fil) throws DataFault{
		return new FilterSet(new LinkFilter(l, r, fil));
	}
	
	@SuppressWarnings("unchecked")
	public IndexedProducer<R> getRightProducer(){
		return ((IndexedTypeProducer<R,IndexedProducer<R>>)res.getInfo(right_field).getTypeProducer()).getProducer();
	}
    public String getRightField(){
    	return right_field;
    }
    public SQLFilter<T> getRightFilter(R r){
    	return new ReferenceFilter<T,R>(this,right_field,r);
    }
    
	/**
	 * create an Iterator over Right objects
	 * 
	 * @param l
	 *            Left DataObject required null for any
	 * @param f
	 *            extension Filter
	 * @return Iterator over Right Objects
	 * @throws DataFault
	 */
	public Iterator<R> getRightIterator(L l,  BaseFilter<? super T> f)
			throws DataFault {
		return new RightIterator(getLinkIterator(l, null, f));
	}
	/** Get a Set of Right  objects
	 * 
	 * 	 * @param l Left DataObject required null for any
	 * @param f extension Filter
	 * @return Set of Right objects
	 * @throws DataFault
	 */
	public Set<R> getRightSet(L l ,BaseFilter<? super T> f) throws DataFault{
		Set<R> res= new LinkedHashSet<R>();
		for (Iterator<R> it = getRightIterator(l,f); it.hasNext();){
			res.add(it.next());
		}
		return res;
	}


	protected boolean isLeft(Object o){
		return o != null && getLeftProducer().getTarget().isAssignableFrom(o.getClass());
	}
	protected boolean isRight(Object o){
		return o != null && getRightProducer().getTarget().isAssignableFrom(o.getClass());
	}
	/**
	 * create a new HistoryFactory suitable for the Link objects produced by
	 * this class. This method should be re-implemented by any subclass that
	 * wishes to implement History tracking. Note that HistoryFactories contain
	 * a reference to their Peer Factory so it will be cleaner to pass this
	 * object into the constructor of the HistoryFactory
	 * 
	 * @return LinkHistoryManager
	 */
	protected HistoryHandler<T> makeHistoryHandler() {
		return null;
	}
	/** Add Tracking fields to the history table default specification
	 * 
	 * @param spec
	 */
	public void modifyHistoryTable(TableSpecification spec){
		
	}
	/**
	 * Return a link creating one and calling setup if it does not exist. These
	 * methods are protected in case the superclass needs to add additional
	 * checks or setup as well.
	 * 
	 * @param left_end
	 *            Left {@link Indexed}
	 * @param right_end
	 *            Right {@link Indexed}
	 * @return Link
	 * @throws DataException
	 */
	synchronized protected final T makeLink(L left_end, R right_end)
			throws Exception {
		if( left_end == null || right_end == null){
			throw new IllegalArgumentException("Null arguments to makeLink");
		}
		// Method is synchronized to mitigate a potential race condition when running without
		// database transactions. Still not guaranteed to prevent these but
		// might save some cases.
		T l = getLink(left_end, right_end);
		if (l == null) {
			l =  makeBDO();
			l.setLeft(left_end);
			l.setRight(right_end);
			l.setup();
			l.commit();
		}
		return l;
	}

	

	/**
	 * method to call when from selector code normally the same as getLink but
	 * may need to be overridden to automatically create objects (beware of
	 * unwanted side effects in this case). Ususally want to Override
	 * {@link #canCreate(uk.ac.ed.epcc.webapp.SessionService)} in that case.
	 * 
	 * @param left_end
	 * @param right_end
	 * @return Link
	 * @throws DataException
	 */
	protected T selectLink(L left_end, R right_end)
			throws Exception {
		return getLink(left_end, right_end);
	}
	public class LinkUpdater extends Updater<T>{

		protected LinkUpdater() {
			super( IndexedLinkManager.this);
			
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getSupress()
		 */
		@Override
		protected Set<String> getSupress() {
			// supress link fields as set by the update
			// don't do this in the factory as we may want 
			// a create form
			Set<String> supress = new HashSet<String>();
			Set<String> xtra = super.getSupress();
			if( xtra != null){
				supress.addAll(xtra);
			}
			supress.add(left_field);
			supress.add(right_field);
			return supress;
		}
	}
	@Override
	public FormUpdate<T> getFormUpdate(AppContext c) {
		return new LinkUpdater();
	}
	
	
	
	/**
	 * Do we automatically update the history as part of commit. 
	 * 
	 * @return boolean
	 */
	protected boolean useAutoHistory() {
		return false;
	}
	protected void updateHistory(T val){
		try {
			getHistoryHandler().update(val);
		} catch (Exception e) {
		    getContext().error(e,"Error updating history");
		}
	}

	@Override
	public Class<? super T> getTarget(){
		return Link.class;
	}
}