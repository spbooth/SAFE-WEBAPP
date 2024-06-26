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
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
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
	public abstract static class Link<L extends Indexed, R extends Indexed> extends DataObject implements Owned{
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
        @Override
		public DataObjectFactory getFactory() {
        	return getIndexedLinkManager();
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
						getLogger().error("Error updating Link History", e);
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
		 * current Left peer or the reference is not yet set.
		 * @param o
		 *            DataObject
		 */
		protected final void setCachedLeft(L o) {
			if (o == null || left != null) {
				return;
			}
			if (!manager.isLeft(o)) {
				throw new ClassCastException(
						"Illegal type passed to LinkManager "+o.getClass().getCanonicalName());
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
		 * current Right peer or the reference is not yet set.
		 * @param o
		 *            DataObject
		 */
		protected final void setCachedRight(R o) {
			if (o == null || right != null) {
				return;
			}
			if (!manager.isRight(o)) {
				throw new ClassCastException(
						"Illegal type passed to LinkManager "+o.getClass().getCanonicalName());
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
		protected final void changeLeft(L left) {
			record.setProperty(manager.left_field, left.getID());
			setCachedLeft(left);
		}
		protected final void changeRight(R right) {
			record.setProperty(manager.right_field, right.getID());
			setCachedRight(right);
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
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#pre_commit(boolean)
		 */
		@Override
		protected void pre_commit(boolean dirty) throws DataFault {
			assert(getLeftID().intValue() > 0);
			assert(getRightID().intValue() > 0);
		}
	}

	protected interface LinkProvider<T extends IndexedLinkManager.Link<L,R>,L extends Indexed,R extends Indexed> extends BaseFilter<T>, ResultVisitor<T>{
		public L getLeftTarget();
		public R getRightTarget();
	}
	/**
	 * Filter for link objects This class handles filtering by the end points
	 * and can be extended by composition for example to select objects with a
	 * particular status. This class also initialises the cache links for the
	 * objects it knows about if its the top level filter.
	 * 
	 * @author spb
	 * 
	 */
	protected final class LinkFilter extends DataObjectAndFilter<IndexedLinkManager<T,L,R>,T> implements LinkProvider<T,L,R>{
		private L left_target = null;

		private R right_target = null;


		/**
		 * create a LinkFilter
		 * 
		 * @param l
		 *            required Left Indexed null for any
		 * @param r
		 *            requires Right Indexed null for any
		 * @param f
		 *            extension Filter
		 */
		public LinkFilter(L l, R r, BaseFilter<T> f) {
			super(IndexedLinkManager.this);
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
			  addFilter(new ReferenceFilter<>(IndexedLinkManager.this,left_field,l));
			}
			if( r != null ){
				  addFilter(new ReferenceFilter<>(IndexedLinkManager.this,right_field,r));
			}
		}

		@Override
		public L getLeftTarget(){
			return left_target;
		}
		@Override
		public R getRightTarget(){
			return right_target;
		}
        @Override
		public void visit(T l) {
				// Set cache values where we can. This happened in the LinkMapper if
				// we are using LinkFilterIterator but we have to do it here if we are using
				// a standard LinkFilter
				l.setCachedLeft(left_target);
				l.setCachedRight(right_target);
		}
	}
   

	/**
	 * Filter for link objects This class handles filtering by the end points
	 * and can be extended by composition for example to select objects with a
	 * particular status. This class also initialises the cache links for the
	 * objects it knows about if its the top level filter.
	 * 
	 * @author spb
	 * 
	 */
	protected final class SQLLinkFilter extends DataObjectSQLAndFilter<IndexedLinkManager<T,L,R>,T> implements LinkProvider<T,L,R>{
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
			super(IndexedLinkManager.this);
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
				addFilter(new ReferenceFilter<>(IndexedLinkManager.this,left_field,l));
			}
			if( r != null ){
				addFilter(new ReferenceFilter<>(IndexedLinkManager.this,right_field,r));
			}
		}

		@Override
		public L getLeftTarget(){
			return left_target;
		}
		@Override
		public R getRightTarget(){
			return right_target;
		}
        @Override
		public void visit(T l) {
				// Set cache values where we can. This happened in the LinkMapper if
				// we are using LinkFilterIterator but we have to do it here if we are using
				// a standard LinkFilter
				l.setCachedLeft(left_target);
				l.setCachedRight(right_target);
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
		setContext(c, table, left_fac, left_field, right_fac, right_field);
	}
	protected IndexedLinkManager(){
		
	}
	protected void setContext(AppContext c, String table,IndexedProducer<L> left_fac,
				String left_field, IndexedProducer<R> right_fac, String right_field) {	
		// Composites may wan to query these
		this.left_field = left_field;
		this.right_field = right_field;
		if( DataObjectFactory.AUTO_CREATE_TABLES_FEATURE.isEnabled(c)){
			setComposites(c, table);
			setContextWithMake(c, table,()->getFinalTableSpecification(c,table,left_fac,left_field,right_fac,right_field));
		}else{
			setContext(c, table,false);
		}
		registerTypeProducer(new IndexedTypeProducer<>(c,left_field, left_fac));
		registerTypeProducer(new IndexedTypeProducer<>(c,right_field, right_fac));
	}

	// make final to stop people overriding the wrong method.
	@Override
	public final TableSpecification getDefaultTableSpecification(AppContext c,String table){
		return super.getDefaultTableSpecification(c, table);
	}
	protected TableSpecification getDefaultTableSpecification(AppContext c,String table,
			IndexedProducer<L> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		TableSpecification s = new TableSpecification();
		if( leftFac instanceof DataObjectFactory){
			s.setField(leftField, ((DataObjectFactory)leftFac).getReferenceFieldType(false,true));
		}else{
			s.setField(leftField, new IntegerFieldType(false,null));
		}
		if( rightFac instanceof DataObjectFactory){
			s.setField(rightField, ((DataObjectFactory)rightFac).getReferenceFieldType(false,true));
		}else{
			s.setField(rightField, new IntegerFieldType(false,null));
		}
		try {
			s.new Index("Link", true, leftField, rightField);
		} catch (InvalidArgument e) {
			getLogger().error("Error making index",e);
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
	@Override
	public final TableSpecification getTableSpecification() {
		return getFinalTableSpecification(getContext(), getTag(), getLeftProducer(), getLeftField(), getRightProducer(), getRightField());
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
    	return new ReferenceFilter<>(this,left_field,l);
    }
	
	/** Get a Set of Left  objects
	 * 
	 * @param r Right {@link Indexed} required null for any
	 * @param f extension Filter
	 * @return Set of Left objects
	 * @throws DataException 
	 */
	public Set<L> getLeftSet( R r ,BaseFilter<T> f) throws DataException{
		Set<L> res= new LinkedHashSet<>();
		return addLeftSet(res, r, f);
	}
	/** Add to a set of Left Objects
	 * 
	 * @param res {@link Set} to add too
	 * @param r Right {@link Indexed} required null for any
	 * @param f extension Filter
	 * @return Set of Left objects
	 * @throws DataException 
	 */
	public Set<L> addLeftSet(Set<L> res, R r ,BaseFilter<T> f) throws DataException{	
	
		try(FilterResult<T> filterResult = getFilterResult(null, r, f)){
			for(T link : filterResult){
				res.add(link.getLeft());
			}
			return res;
		}
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
			SQLAndFilter<T> fil = getSQLAndFilter(
					new ReferenceFilter<>(this, left_field, left_end),
					new ReferenceFilter<>(this, right_field, right_end));
			T l = find(fil,true);
			
			if( l != null ){
			  l.setCachedLeft(left_end);
			  l.setCachedRight(right_end);
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
	 * create a {@link FilterResult} of Link objects
	 * 
	 * @param l
	 *            Left {@link Indexed} required null for any
	 * @param r
	 *            Right {@link Indexed} required null for any
	 * @param fil
	 *            extension Filter
	 * @return FilterResult over Link
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
    	return new ReferenceFilter<>(this,right_field,r);
    }
    
	
	/** Get a Set of Right  objects
	 * @param l Left DataObject required null for any
	 * @param f extension Filter
	 * @return Set of Right objects
	 * @throws DataException 
	 */
	public Set<R> getRightSet(L l ,BaseFilter<T> f) throws DataException{
		Set<R> res= new LinkedHashSet<>();
		return addRightSet(res, l, f);
	}
	/** Add to a set of Right objects
	 * 	
	 * @param res {@link Set} to add too
	 * @param l Left DataObject required null for any
	 * @param f extension Filter
	 * @return Set of Right objects
	 * @throws DataException 
	 */
	public Set<R> addRightSet(Set<R> res,L l ,BaseFilter<T> f) throws DataException{
		try(FilterResult<T> filterResult = getFilterResult(l, null, f)){
			for(T link : filterResult){
				res.add(link.getRight());
			}
			return res;
		}
	}


	protected boolean isLeft(Object o){
		if( o == null) {
			return false;
		}
		if( ! (o instanceof Indexed)) {
			return false;
		}
		return true;
	}
	protected boolean isRight(Object o){
		if( o == null) {
			return false;
		}
		if( ! (o instanceof Indexed)) {
			return false;
		}
		return true;
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
		for(LinkComposite l : getComposites(LinkComposite.class)){
			l.modifyHistoryTable(spec);
		}
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
	
	protected final T makeLink(L left_end, R right_end)
			throws Exception {
		return makeLink(left_end,right_end,true);
	}
	synchronized protected final T makeLink(L left_end, R right_end, boolean commit)
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
			l.setCachedLeft(left_end);
			l.setCachedRight(right_end);
			l.setup();
			if( commit) {
				l.commit();
			}
		}
		return l;
	}

	

	/**
	 * method to call when from selector code normally the same as getLink but
	 * may need to be overridden to automatically create objects (beware of
	 * unwanted side effects in this case). Ususally want to Override
	 * {@link #canCreate(uk.ac.ed.epcc.webapp.session.SessionService)} in that case.
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
			Set<String> supress = new HashSet<>();
			Set<String> xtra = super.getSupress();
			if( xtra != null){
				supress.addAll(xtra);
			}
			supress.add(left_field);
			supress.add(right_field);
			return supress;
		}
		@Override
		public FormResult getResult(T dat, Form f) {
			// TODO Auto-generated method stub
			// pass the link ends explicitly so MEssageServlet can link to them
			try {
				return new MessageResult("link_updated",getTypeName(),dat.getLeft(),dat.getRight());
			} catch (DataException e) {
				getLogger().error("Error getting update link result", e);
				return new MessageResult("object_updated", getTypeName(),dat.getIdentifier());
			}
		}
	}
	
	@Override
	public FormUpdate<T> getFormUpdate(AppContext c) {
		return new LinkUpdater();
	}
	
	public class LinkCreator extends Creator<T>{

		

		public LinkCreator() {
			super(IndexedLinkManager.this);
		}
		@Override
		public FormResult getResult(T dat, Form f) {
			// TODO Auto-generated method stub
			// pass the link ends explicitly so MEssageServlet can link to them
			try {
				return new MessageResult("link_created",getTypeName(),dat.getLeft(),dat.getRight());
			} catch (DataException e) {
				getLogger().error("Error getting update link result", e);
				return new MessageResult("object_created", getTypeName(),dat.getIdentifier());
			}
		}
	}
	
	@Override
	public FormCreator getFormCreator(AppContext c) {
		return new LinkCreator();
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
			HistoryHandler<T> h = getHistoryHandler();
			if( h != null ) {
				h.update(val);
			}
		} catch (Exception e) {
		    getLogger().error("Error updating history",e);
		}
	}

	
}