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
package uk.ac.ed.epcc.webapp.model.log;


import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.inputs.ConstantInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.ClassType;
import uk.ac.ed.epcc.webapp.model.data.CloseableIterator;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.OrphanReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** general log Entry class.
 * 
 * Consists of a series of date-stamped entries of mutable class.
 * Each entry has an operator who added the entry and a integer tag that references an
 * object that implements Indexed and an Owning object
 * 
 * @author spb
 *
 * @param <T> Type of log Entry
 * @param <O> Type of owning object
 */

public abstract class LogFactory<T extends LogFactory.Entry, O extends Indexed>
		extends DataObjectFactory<T> {
	
	public static final EntryKey EDIT_ITEM = new EntryKey("EditItem","Edit the log entry");


	
	public static class EntryKey extends TransitionKey<Entry>{

		public EntryKey(String name, String help) {
			super(Entry.class, name, help);
		}
		
	}
	/**
	 * Filter LogItems by date
	 * 
	 * @author spb
	 * 
	 */
	public class DateFilter extends SQLAndFilter<T> {
		public DateFilter(Date start, Date end) {
			super(LogFactory.this.getTag());
			if (start != null) {
				addFilter(new TimeFilter(DATE, MatchCondition.GE, start));
			}
			if (end != null) {
				addFilter(new TimeFilter(DATE, MatchCondition.LE, end));
			}
		}
	}

	/** Base class for a log entry
	 * 
	 * @author Stephen Booth
	 *
	 * @param <L>
	 * @param <F>
	 * @param <O>
	 */
	public abstract static class Entry<L extends Indexed, F extends LogFactory, O extends DataObject>
			extends DataObject implements Removable {
		protected F item_factory;

		private O my_query;

		private L link_item;

		protected Entry(F fac, Record r) {
			super(r);
			item_factory = fac;
		}
        protected F getLogFactory(){
        	return item_factory;
        }
		public final boolean belongs(O q) {
			return q.getID() == record.getNumberProperty(OWNER_ID).intValue();
		}

		public void buildUpdateForm(Form f, SessionService p) {

			// Make sure this is not keyed by Type because we don't actually
			// want to edit this.

			f.addInput(item_factory.getItemType().getField(), "Item Type",
					new ConstantInput<String>(getType().getName()));

			AppUser operator = getOperator();
			if (operator != null) {
				f.addInput(OPERATOR_ID, "Operator", new ConstantInput<String>(
						operator.getName()));
			}

			DateFormat df = DateFormat.getInstance();
			f.addInput(DATE, "Date/Time", new ConstantInput<Date>(df
					.format(getDate())));

			f.addAction("Update", getAction());
		}
      
		/**
		 * Does the specified person have any access to the item at all
		 * 
		 * @param p
		 * @return boolean
		 */
		public abstract boolean canRead(SessionService p);

		@SuppressWarnings("unchecked")
		public FormAction getAction() {
			return new ItemAction(this);
		}
		
		/** access control on transitions
		 * 
		 * @param serv
		 * @param key
		 * @return true if transition permitted to current user
		 */
		public abstract boolean permit(SessionService serv, TransitionKey<Entry> key);
		/**
		 * Get the date the Item was added to the log
		 * 
		 * @return Date
		 */
		public final Date getDate() {
			return record.getDateProperty(DATE);
		}

		/**
		 * get the link item
		 * 
		 * @return Link Item or null if not set.
		 * @throws Exception
		 */
		public final L getLink() throws Exception {
			if (link_item == null) {
				Number n = getLinkID();
				if (n == null) {
					return null;
				}
				link_item = makeLink(n);
			}
			return link_item;
		}

		/**
		 * get the ID of the data record in the Date table
		 * 
		 * @return Number
		 */
		private final Number getLinkID() {
			return record.getNumberProperty(LINK_ID);
		}

		/**
		 * Get the Person who added this item
		 * 
		 * @return Person or null
		 */
		public final AppUser getOperator() {
				try {
					Number n = record.getNumberProperty(OPERATOR_ID);
					return (AppUser) item_factory.user_factory.find(n);
				} catch (Exception  e) {
				    getContext().error(e,"Error making operator");
				    return null;
				}
		}
	
		/**
		 * get the owning Query
		 * 
		 * @return Query owning this item
		 * @throws DataException
		 */
		@SuppressWarnings("unchecked")
		public final O getOwner() throws DataException {
			if (my_query != null) {
				return my_query;
			}
			return (my_query = (O) item_factory.owner_factory.find(record
					.getNumberProperty(OWNER_ID).intValue()));
		}

		@SuppressWarnings("unchecked")
		public ItemType.ItemValue getType() {
			return (ItemType.ItemValue) record.getProperty(item_factory.getItemType());
		}

		

		/**
		 * actually retreive the link item from the database
		 * 
		 * @param id
		 *            if of link item
		 * @return Link item
		 * @throws Exception 
		 */
		protected abstract L makeLink(Number id) throws  Exception;

		/**
		 * Remove referenced data if appropriate and then remove this object.
		 * @throws Exception
		 */
		@Override
		public final void remove() throws Exception {
			try{
				L data = getLink();
				if (data != null && data instanceof Removable) {
					((Removable) data).remove();
				}
			}catch(DataNotFoundException e){
				// We are removing anyway so carry on
				// Might be something we already removed
				getLogger().error("Link not found",e);
			}
			delete();
		}

		/**
		 * Set the Creation Date for LogItem
		 * 
		 * @param d
		 */
		public final void setDate(Date d) {
			record.setProperty(DATE, d);
		}

		/**
		 * Set the Link item
		 * 
		 * 
		 * @param link_item
		 */
		public final void setLink(L link_item) {
			this.link_item = link_item;
			if( link_item != null){
				setLinkID(link_item.getID());
			}else{
				setLinkID(0);
			}
		}

		/**
		 * set the ID of the link item entry in the data table.
		 * 
		 * @param id
		 */
		protected final void setLinkID(int id) {
			record.setProperty(LINK_ID, id);
		}

		/**
		 * Set the Operator who added this item
		 * 
		 * @param p
		 */
		public final void setOperator(AppUser p) {
			if (p == null) {
				record.remove(OPERATOR_ID);
			} else {
				record.setProperty(OPERATOR_ID, p.getID());
			}
		}

		public final void setOwner(O q) {
			my_query = q;
			int id = q.getID();
			if( id < 0 ){
				try {
					q.commit();
				} catch (DataFault e) {
					getLogger().error("Error forcing owner id",e);
				}
				id= q.getID();
				assert(id > 0);
			}
			record.setProperty(OWNER_ID, id);
		}
		

	}
	
	public static class ItemAction<T extends Entry> extends FormAction {
		T target;

		public ItemAction(T t) {
			target = t;
		}
       public T getTarget(){
    	   return target;
       }
		@Override
		public FormResult action(Form f) throws ActionException {
			target.setContents(f.getContents());
			try {
				target.commit();
			} catch (DataFault e1) {
				throw new ActionException("Error performing update", e1);
			}
			Object owner;
			try {
				owner = target.getOwner();
			} catch (DataException e) {
				throw new ActionException("Error getting owner",e);
			}
			if( owner instanceof Viewable){
			// redirect back to parent query page
			   return ((Viewable) owner).getViewTransition();
			}else{
				return new RedirectResult(LoginServlet.getMainPage(target.getContext()));
			}
		}

	}
	/**
	 * Filter items by owner, type and date range
	 * 
	 * @author spb
	 * 
	 */
	public class ItemDateFilter extends DateFilter {
		public ItemDateFilter(O q, ItemType.ItemValue<T> v, Date s, Date e) {
			super(s,e);
			addFilter(new ReferenceFilter<>(LogFactory.this, OWNER_ID, q));
			addFilter(getItemFilter(v));
		}
		public ItemDateFilter(O q, Set<ItemType.ItemValue<T>> set, Date s, Date e) {
			super(s,e);
			addFilter(new ReferenceFilter<>(LogFactory.this, OWNER_ID, q));
			addFilter(getItemType().getFilter(LogFactory.this, set));
		}
	}
	/**
	 * superclass for the type objects in sub-classes
	 * 
	 * @author spb
	 * 
	 * @param <E>
	 */
	public static class ItemType<E extends Entry> extends
			ClassType<ItemType.ItemValue<E>, E> {
		public static class ItemValue<E extends Entry> extends
		    ClassType.ClassValue<E> {
			protected ItemValue(ItemType<E> parent, String tag, String name,
					Class<? extends E> c) {
				super(parent, tag, name, c);
			}
			protected ItemValue(String tag, String name,
					Class<? extends E> c) {
				super(tag, name, c);
			}
		}

		public ItemType(String field) {
			super(field);
		}
		public ItemType(ItemType<E> parent){
			super(parent);
		}
		public void adopt(ItemValue<E> value){
			register(value);
		}
		
	}
	
	/**
	 * Filter item by type, date range and link reference
	 * 
	 * @author spb
	 * 
	 * @param <L>
	 *            type of link
	 */
	public class TypeFilter<L extends Indexed> extends DateFilter {

		public TypeFilter(ItemType.ItemValue<T> item, L target, Date s, Date e) {
			super(s,e);
			addFilter(getItemType().getFilter(LogFactory.this, item));
			if (target != null) {
				addFilter(new ReferenceFilter<>(LogFactory.this, LINK_ID,
						target));
			}
		}

	}
	public class DateOrderFilter implements SQLOrderFilter<T>{

		@Override
		public String getTag() {
			return LogFactory.this.getTag();
		}

		@Override
		public List<OrderClause> OrderBy() {
			LinkedList<OrderClause> order = new LinkedList<>();
			order.add(res.getOrder(DATE, false));
			order.add(res.getOrder(null, false));
			return order;
		}
		
	}

	protected static final String OWNER_ID = "QueryID";

	protected static final String LINK_ID = "LinkID";

	protected static final String OPERATOR_ID = "OperatorID";

	protected static final String DATE = "Date";

	protected final LogOwner<O> owner_factory;
    protected final AppUserFactory<?> user_factory;
    // This is a per-instance ItemType that can also include ItemValues registered by composite
    protected final ItemType<T> use_type;
	public LogFactory(LogOwner<O> fac, String table,AppUserFactory<?> uf) {
		AppContext ctx = fac.getContext();
		use_type=new ItemType<>(getStaticItemType());
		if( DataObjectFactory.AUTO_CREATE_TABLES_FEATURE.isEnabled(ctx)){
    		if( setContextWithMake(ctx, table,getDefaultTableSpecification(ctx,fac,uf,table))){
    			// table created
    			setupItems(ctx, table);
    		}
    	}else{
    		setContext(ctx, table);
    	}
		owner_factory = fac;
		user_factory = uf;
		use_type.lock();
	}
	/** create LogItem tables that don't have factories.
	 * 
	 * @param c
	 * @param table
	 */
	protected void setupItems(AppContext c,String table){
		Map<String,TableSpecification> prereq=getPrereqTables();
	
		DataBaseHandlerService serv = c.getService(DataBaseHandlerService.class);
		if( serv != null ){
			for(String tab : prereq.keySet()){
				if( ! serv.tableExists(tab)){
					try {
						serv.createTable(tab, prereq.get(tab));
					} catch (DataFault e) {
						c.error(e,"Error making prereq table");
					}
				}
			}
		}
		
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#observeComposite(uk.ac.ed.epcc.webapp.model.data.Composite)
	 */
	@Override
	protected void observeComposite(Composite c) {
		// Allow composites to add to the use_type
		if( c instanceof LogComposite){
			((LogComposite)c).registerItems(use_type);
		}
		
	}
	protected Map<String,TableSpecification> getPrereqTables(){
		return new HashMap<>();
	}
	protected TableSpecification getDefaultTableSpecification(AppContext c,LogOwner<O> fac,AppUserFactory<?>uf,String homeTable){
		TableSpecification spec = new TableSpecification("LogID");
		spec.setField(DATE, new DateFieldType(true, null));
		spec.setField(OWNER_ID, new ReferenceFieldType(fac.getTag()));
		spec.setField(OPERATOR_ID, new ReferenceFieldType(uf.getTag()));
		spec.setField(LINK_ID, new IntegerFieldType());
		try {
			spec.new Index("OwnerKey",false,OWNER_ID,DATE);
		} catch (InvalidArgument e) {
			getLogger().error("Error making key",e);
		}
		return spec;
	}
	
	
	public ReferenceFilter<T, O> getOwnerFilter(O q) {
		return new ReferenceFilter<>(this, OWNER_ID, q);
	}
	/** Find and item only by what it points to.
	 * Only makes sense for a unique target.
	 * 
	 * @param v
	 * @param link
	 * @param allow_null
	 * @return Entry
	 * @throws DataException
	 */
	public  T find(ItemType.ItemValue v, int link, boolean allow_null)
			throws DataException {
		SQLAndFilter<T> fil;
		fil = getItemFilter(v, link);
		return find(fil,allow_null);
		
	}
	/** find an item by owner and value
	 * 
	 * @param q
	 * @param v
	 * @param link
	 * @param allow_null
	 * @return Item
	 * @throws DataException
	 */
	public  T find(O owner,ItemType.ItemValue v, int link, boolean allow_null)
			throws DataException {
		SQLAndFilter<T> fil;
		fil = getItemFilter(owner,v, link);
		return find(fil,allow_null);
		
	}
	/** get a SQL filter to items that point to a particular target.
	 * @param v
	 * @param link
	 * @return
	 */
	protected SQLAndFilter<T> getItemFilter(ItemType.ItemValue v, int link) {
		SQLAndFilter<T> fil;
		fil = getSQLAndFilter();
		fil.addFilter(getItemFilter(v));
		fil.addFilter(new SQLValueFilter<>(res, LINK_ID, link));
		return fil;
	}
	
	/** get a Filter for items that point to a DataObject that match a particular filter
	 * @param v
	 * @param link
	 * @return
	 */
	public <L extends DataObject> AndFilter<T> getItemFilter(DataObjectFactory<L> fac, ItemType.ItemValue v, BaseFilter<L> link_fil) {
		AndFilter<T> fil;
		fil = getAndFilter();
		fil.addFilter(getItemFilter(v));
		fil.addFilter(getRemoteFilter(fac, LINK_ID, link_fil));
		return fil;
	}
	
	protected SQLAndFilter<T> getItemFilter(O q, ItemType.ItemValue v, int link) {
		SQLAndFilter<T> fil = getItemFilter(v,link);
		fil.addFilter(getOwnerFilter(q));
		return fil;
	}
	/** get all owners that reference a particular owner from their log.
	 * 
	 * @param v
	 * @param link
	 * @return
	 * @throws DataException
	 */
	public Set<O> getOwners(ItemType.ItemValue v, int link) throws DataException {
		HashSet<O> owners = new HashSet<>();
		for( T item : getResult(getItemFilter(v, link))){
			owners.add((O) item.getOwner());
		}
		return owners;
	}
	/** Get the final {@link ItemType} to use for this class.
	 * This can include dynamic additions.
	 * 
	 * @return
	 */
	protected final ItemType<T> getItemType(){
		return use_type;
	}
	/** Get the most specific non-dynamic {@link ItemType}
	 * sub-classes override this to generate a statically configured type.
	 * which is then used to create a per-instance version including dynamic additions.
	 * @return
	 */
	protected abstract ItemType<T> getStaticItemType();
	@Override
	protected final T makeBDO(Record res) throws DataFault {
		return getItemType().makeBDO(this, res);
	}
	/** purge all entries for an owner.
	 * 
	 * @param q
	 * @throws DataFault 
	 */
	public final void purge(O q) throws DataFault{
		try(CloseableIterator<T> it = getLog(q)){
			while(it.hasNext()) {

				T i = it.next();
				it.remove();
			}
		}catch(DataFault e) {
			throw e;
		} catch (Exception e) {
			throw new DataFault("Error in close", e);
		}

	}
	/** purge all orphan entries.
	 * Only works if the {@link LogOwner} is a {@link DataObjectFactory}.
	 * @throws DataFault 
	 */
	public final void purgeOrphan() throws DataFault{
		if( owner_factory instanceof DataObjectFactory){
			for (Iterator<T> it =new FilterIterator(new OrphanReferenceFilter(this, OWNER_ID, (DataObjectFactory) owner_factory)); it.hasNext();){
				it.remove();
			}
		}
	}
	
	public CloseableIterator<T> getLog(O q) throws DataFault {
		SQLAndFilter<T> fil = getSQLAndFilter(getOwnerFilter(q),  new DateOrderFilter());
		return new FilterIterator(fil);
	}

	protected final LogOwner<O> getOwnerFactory() {
		return owner_factory;
	}
	
	
	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(null, false));
		return order;
	}
	public class EditItemTransition extends AbstractFormTransition<Entry>{

		@Override
		public void buildForm(Form f, Entry target, AppContext conn)
				throws TransitionException {
			
			target.buildUpdateForm(f, conn.getService(SessionService.class));
			f.setContents(target.getMap());
		}
		
	}
	@SuppressWarnings("unchecked")
	public class TransitionProvider extends SimpleTransitionProvider<Entry,TransitionKey<Entry>> implements LogTransitionProvider{

		
		public TransitionProvider(AppContext c) {
			super(c, (IndexedProducer<Entry>) LogFactory.this,"Log:"+getOwnerFactory().getTag());
			addTransition(EDIT_ITEM, new EditItemTransition());
		}

		@Override
		public boolean allowTransition(AppContext c, Entry target,
				TransitionKey<Entry> key) {
			SessionService serv = c.getService(SessionService.class);
			return target.canRead(serv) && target.permit(serv,key);
		}

		@Override
		public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb,Entry target) {
			return cb;
		}
		
	}

	public LogTransitionProvider getTransitionProvider(){
		return new TransitionProvider(getContext());
	}

	/**
	 * @param v
	 * @return
	 */
	public SQLFilter<T> getItemFilter(ItemType.ItemValue<T> v) {
		return getItemType().getFilter(LogFactory.this, v);
	}
	
	
}