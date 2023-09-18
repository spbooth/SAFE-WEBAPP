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

import java.sql.ResultSet;
import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.filter.JoinerFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/**
 * LinkManager is the base class of Factories that manage Link objects These
 * classes manage many-to-one relationships between model level objects. The <code>LinkManager</code>
 * class calls these the <i>left</i> and <i>right</i> objects. 
 * The actual <code>Link</code> DataObjects are pseudo inner classes of the <code>LinkManager</code>
 * Often the Link objects will remain hidden inside the <code>LinkManager</code> sub-class and only be
 * manipulated via an external interface. If we want to track the history of a Link object needs a status
 * field that marks if the link is valid. This way the same object is used to record the link between two peer objects
 * even if the link is broken and remade several times. <code>Link</code> can be selected based on the objects at the
 * end on the link. e.g.
<pre>
<code>
    // get a link
    Link l = manager.getLink(left_peer,right_peer);  // null if link does not exist
    
    // or
    Link l2 = manager.makeLink(left_peer,right_peer);  // make link if it does not already exist
</code>
</pre>
 * <p>
 * LinkManager can navigate the links in either direction the subclass may
 * choose only to expose one direction of navigation. Internally this is implemented using the
 * {@link IndexedLinkManager.LinkFilter} or {@link IndexedLinkManager.SQLLinkFilter} classes which select a set a <code>Link</code> objects
 * based on the value of one or other end.
<pre>
<code>
   // links joining left_peer to something
   Iterator&lt;Link&gt; it = manager.getLinkIterator(left_peer,null,filter);

   // or go straight to the referenced object
   Iterator&lt;Right&gt;  right_it = manager.getRightIterator(left_peer,null,filter);
</code>
</pre>
 * <p>
 * The Link class can cache references to the objects it points to, these cached values are retrieved using
 * the <code>getLeft()</code> and <code>getRight()</code> methods. 
 * </p>
 * <p>
 * LinkManager provides a single point in the code to optionally apply SQL join
 * optimisations. A SQL join can be used to initialise the cache fields in the
 * Link object when it is created.
 * 
 * @author spb
 * @param <T> Type of link object
 * @param <L> type of left end 
 * @param <R> type of right end
 * 
 */


public abstract class LinkManager<T extends LinkManager.Link<L,R>,L extends DataObject,R extends DataObject> extends IndexedLinkManager<T,L,R> {

	public static final Feature USE_JOIN = new Feature("linkmanager.use_join",true,"Use joins to pre-fetch link ends");

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
	 * </p>
	 * <p>
	 * The appropriate LinkManager Link should
	 * always be subclasses to improve type safety. The <code>getLeft()</code>/<code>getRight()</code> methods are
	 * made protected to force us to add sensibly named accessors in sub-classes
	 * </p>
	 * @author spb
	 * @param <L> Left end type
	 * @param <R> Right end type
	 * 
	 */
	public abstract static class Link<L extends DataObject, R extends DataObject> extends IndexedLinkManager.Link<L,R>  implements UIGenerator{
		
		protected Link(LinkManager<?,L,R> man,Repository.Record res) {
			super(man,res);
		}
		/** allow sub-classes to get the LinkManager
		 * don't make the return type generic its much easier for  sub-classes to implement getFactory() methods
		* that cast correctly than have a generic signature that can be used use-fully
		 * type checking is preserved by getting the constructor right 
		 * @return
		 */
        protected LinkManager getLinkManager(){
        	return (LinkManager) getIndexedLinkManager();
        }
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#getIdentifier()
		 */
		@Override
		public String getIdentifier(int max_length) {
			try {
				if( max_length > 7){
					max_length -= 5;
					max_length = max_length/2;
				}
				return "(" + getLeft().getIdentifier(max_length) + ")-("
						+ getRight().getIdentifier(max_length) + ")";
			} catch (DataException e) {
				return super.getIdentifier(max_length);
			}
		}
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			try {


				builder.addObject(getLeft());
				builder.addObject("-");
				builder.addObject(getRight());
			}catch(Exception e){
				getLogger().error("Error adding content", e);
				builder.addObject(getIdentifier());
			}
			return builder;
		}		
       
	}

	/** A {@link ResultMapper} that handles setting the links from joins.
	 * 
	 * @author spb
	 *
	 */
    public class JoinLinkMapper implements ResultMapper<T>{
        public JoinLinkMapper(boolean join_left, boolean join_right){
        	this.qualify=join_left || join_right;
        	this.join_left=join_left;
        	this.join_right=join_right;
        }
        boolean qualify=true;
        final boolean join_left;
        final boolean join_right;
        @Override
		public boolean setQualify(boolean qualify) {
			boolean old = this.qualify;
			this.qualify = qualify;
			return old;
		}
        @Override
		public T makeObject(ResultSet rs) throws DataException {
        	LinkManager<T,L,R> lm = LinkManager.this;
        	T link = lm.makeObject(rs,true);


        	if( join_left ){
        		try{
        			L left =  getLeftFactory().makeObject(rs,qualify);
        			link.setCachedLeft(left);
        		}catch(DataNotFoundException e){
        			// reference link is bad
        			getLogger().error("Bad left link value "+link.getIdentifier());
        		}
        	}

        	if( join_right ){
        		try{
        			R right = getRightFactory().makeObject(rs,qualify);
        			link.setCachedRight(right);
        		}catch(DataNotFoundException e){
        			// reference link is bad
        			getLogger().error("Bad right link value "+link.getIdentifier());
        		}
        	}

        	return link;
		}
    	
		@Override
		public String getTarget() {
			StringBuilder target = new StringBuilder();
			res.addAlias(target, true);
			target.append(".* ");
			if ( ! (join_left || join_right)) {
				return target.toString();
			}
			if (join_left) {
				target.append(", ");
				getLeftFactory().res.addAlias(target, true);
				target.append(".* ");
			}
			if (join_right) {
				target.append(", ");
				getRightFactory().res.addAlias(target, true);
				target.append(".* ");
			}
			return target.toString();
		}
		@Override
		public String getModify() {
			// force a defined order as we may be chunking
			return OrderBy(true);
		}
		@Override
		public T makeDefault() {
			return null;
		}
		@Override
		public SQLFilter getRequiredFilter() {
			if( ! (join_left || join_right)){
				return null;
			}
			// Use JoinerFilters so that if additional joins are added explicitly
			// The join clauses will be identical and not duplicated.
			SQLAndFilter<T> fil = LinkManager.this.getSQLAndFilter();
			if( join_left ){
				fil.addFilter(new JoinerFilter<T,L>( getLeftField(), res, getLeftFactory().res));
			}
			if( join_right ){
				fil.addFilter(new JoinerFilter<T,R>( getRightField(), res, getRightFactory().res));
			}
			return fil;
		}
		@Override
		public List<PatternArgument> getTargetParameters(
				List<PatternArgument> list) {
			return list;
		}
		@Override
		public List<PatternArgument> getModifyParameters(
				List<PatternArgument> list) {
			return list;
		}
    }

	
	/**
	 * Version of FilterIterator that optimises the end object caches by
	 * performing a join on target tables.
	 * 
	 * 
	 * @author spb
	 * 
	 */
	public class JoinLinkFilterIterator extends ResultIterator<T> {
       
        private final boolean join_left;
        private final boolean join_right;
		public JoinLinkFilterIterator(LinkProvider<T,L,R> fil) throws DataFault {
        	this(fil,fil.getLeftTarget()==null,fil.getRightTarget()==null);
        }
    	public JoinLinkFilterIterator(BaseFilter<T> fil,boolean join_left,boolean join_right) throws DataFault {
			super(LinkManager.this.getContext(),LinkManager.this.getTag());
			this.join_left=join_left;
			this.join_right=join_right;
			boolean use_join = join_left || join_right;
			setQualify(use_join);
			setMapper(new JoinLinkMapper(join_left,join_right));
			if( fil instanceof ResultVisitor){
				setVisitor((ResultVisitor<T>)fil);
			}
			
			try {
				setup(fil,0,-1);
			} catch (DataException e) {
				throw new DataFault("Error in setup",e);
			}
			
		}
		@Override
		public final void addSource(StringBuilder source) {
			// Note the join tables are added as filters
			res.addSource(source, true);	
		}
		@Override
		protected final Set<Repository> getSourceTables() {
			// note the join tables are added as filters
			HashSet<Repository> set = new HashSet<>();
			set.add(res);
			return set;
		}
		@Override
		protected String getDBTag() {
			return res.getDBTag();
		}
	}

	/**
	 * An input for Link objects implemented as a CompositeInput
	 * 
	 * @author spb
	 * 
	 */
	public class LinkInput extends CompositeInput<Integer> implements DataObjectItemInput<T> {
		Input<Integer> left_input;

		Input<Integer> right_input;

		L fix_left = null;

		R fix_right = null;

		T l;

		public LinkInput() {
			super();
			addInput("left",  (left_input =  getLeftInput()));
			addInput("right",  (right_input = getRightInput()));
		}

		/**
		 * set a forcing value on the left side of the selector
		 * 
		 * @param left
		 */
		public void fixLeft(L left) {
			fix_left = left;
			if (left == null) {
				// removing the fix
				addInput("left",  left_input);
			} else {
				// override the input
				addInput("left",  new ConstantInput<>(left.getIdentifier(),left.getID()));
			}
		}

		/**
		 * set a forcing value on the right side of the selector
		 * 
		 * @param right
		 */
		public void fixRight(R right) {
			fix_right = right;
			if (right == null) {
				// removing the fix
				addInput("right",  right_input);
			} else {
				// override the input
				addInput("right",  new ConstantInput<>(right.getIdentifier(),right.getID()));
			}
		}

		@Override
		public T getItembyValue(Integer num) {
			if (num == null) {
				// must be optional
				return null;
			}
			try {
				return find(num.intValue());
			} catch (DataException e) {
				return null;
			}

		}

		@Override
		public String getString(Integer value) {
			if (value == null) {
				return null;
			}
			return value.toString();

		}
       
		@Override
		public Integer getValue() {
			if (l == null) {
				return null;
			}
			return new Integer(l.getID());
		}

		public void parse(String v) throws ParseException {
			if (v == null) {
				setNull();
				return;
			}
			if (v.trim().length() == 0) {
				setNull();
				return;
			}
			try {
				Integer i;

				i = new Integer(Integer.parseInt(v.trim()));

				setNull();
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid integer format");
			}

		}

		@Override
		public Integer getValueByItem(T l) {
			return l.getID();
		}

		@Override
		public void setNull() {
			super.setNull();
			l=null;
		}
		@Override
		public Integer setValue(Integer v) throws TypeException {
			if( v == null ){
				Integer old = getValue();
				l = null;
				return old;
			}
			if( v instanceof Number){
			int id=((Number)v).intValue();
			Integer old = getValue();
			try {
					l =  find(id);
					left_input.setValue(l.getLeftID().intValue());
					right_input.setValue(l.getRightID().intValue());
					// reset the fixed values to maintain consistency
					if (fix_left != null) {
						fixLeft(l.getLeft());
					}
					if (fix_right != null) {
						fixRight(l.getRight());
					}
			} catch (DataException e) {
				l = null;
			}
			return old;
			}
			throw new TypeException("Unknown type pased to LinkInput");
		}
        @Override
		public Integer convert(Object v) throws TypeException{
        	if( v == null || v instanceof Integer){
        		return (Integer) v;
        	}
        	if( v instanceof Number){
        		return new Integer(((Number)v).intValue());
        	}
        	throw new TypeException("Unknown type pased to LinkInput");
        }
		@Override
		public void validateInner() throws FieldException {
			super.validateInner();
			Number left = left_input.getValue();
			Number right = right_input.getValue();
			
			try {
				l = selectLink(getLeftFactory().find(left), getRightFactory().find(right));
			} catch (Exception e) {
				getLogger().error("error in LinkInput");
				throw new ValidateException("Selected item does not exist",e);
			}
			if (l == null) {
				throw new ValidateException("Selected item does not exist");
			}
		}

		@Override
		public boolean isEmpty() {
			Number left = left_input.getValue();
			Number right = right_input.getValue();
			if (left == null || right == null) {
				return true;
			}
			return super.isEmpty();
		}

		public T getDataObject() {
			return getItem();
		}

	}
	/** A {@link FilterResult} for link objects.
	 * 
	 * This generates a iterator to pre-populate the end-links if known.
	 * 
	 * @see DataObjectFactory.FilterSet
	 * @author spb
	 *
	 */
	public class LinkResult extends AbstractFilterResult<T> implements FilterResult<T>{
		public LinkResult(L left, R right, BaseFilter<T> fil) throws DataFault {
			super();
			if( left != null && ! isLeft(left)){
				throw new ClassCastException("Invalid object passed as left peer");
			}
			if( right != null && ! isRight(right)){
				throw new ClassCastException("Invalid object passed as right peer");
			}
			this.left=left;
			this.right = right;
			this.fil = fil;
		}

		private final L left;
		private final R right;
		private final BaseFilter<T> fil;
		
		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.AbstractFilterResult#getLogger()
		 */
		@Override
		protected Logger getLogger() {
			return LinkManager.this.getLogger();
		}
		@Override
		public boolean isEmpty() throws  DataFault{
			try {
				return !exists(fil);
			}catch(DataFault df) {
				throw df;
			} catch (DataException e) {
				throw new DataFault("Error in isEmpty",e);
			}
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.AbstractFilterResult#makeIterator()
		 */
		@Override
		protected CloseableIterator<T> makeIterator() throws DataFault {
			
			return getLinkIterator(left, right, fil);
		}		
	}



	/** A wrapper to convert a non SQL filter on the link object into an {@link AcceptFilter}
	 * on the left object
	 * 
	 * @author spb
	 *
	 */
    public class LeftAcceptFilter implements AcceptFilter<L>{
    	/**
		 * @param fil nested {@link BaseFilter} on link object
		 */
		public LeftAcceptFilter( BaseFilter<T> fil) {
			this.fil = fil;
		}

		private final BaseFilter<T> fil;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean test(L o) {
			try {
				return exists(new LinkFilter(o, null, fil));
			} catch (DataException e) {
				getLogger().error("Error in filter", e);
				return false;
			}
		}
    }
    /** A wrapper to convert a non SQL filter on the link object into an {@link AcceptFilter}
	 * on the right object
	 * 
	 * @author spb
	 *
	 */
    public class RightAcceptFilter implements AcceptFilter<R>{
    	/**
		 * @param fil
		 */
		public RightAcceptFilter( BaseFilter<T> fil) {
			this.fil = fil;
		}

		private final BaseFilter<T> fil;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean test(R o) {
			try {
				return exists(new LinkFilter(null, o,fil));
			} catch (DataException e) {
				getLogger().error("Error in filter", e);
				return false;
			}
		}
    }

	protected LinkManager(AppContext c, String table,DataObjectFactory<L> left_fac,
			String left_field, DataObjectFactory<R> right_fac, String right_field) {
		super(c,table,left_fac,left_field,right_fac,right_field);

	}
	protected LinkManager() {
		super();
	}

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,String table,
			IndexedProducer<L> leftFac, String leftField,
			IndexedProducer<R> rightFac, String rightField) {
		TableSpecification s = super.getDefaultTableSpecification(c, table, leftFac, leftField, rightFac, rightField);
		s.setField(leftField, new ReferenceFieldType(false,((DataObjectFactory<L>)leftFac).getTag()));
		s.setField(rightField, new ReferenceFieldType(false,((DataObjectFactory<R>)rightFac).getTag()));
		try {
			// Might double as an index on leftField
			// parent makes link index
			//s.new Index("Link", true, leftField, rightField);
			
			// May only need the right key but lets be safe.
			s.new Index("LeftKey",false,leftField);
			s.new Index("RightKey",false,rightField);
		} catch (InvalidArgument e) {
			getLogger().error("Error making index",e);
		}
		return s;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getInput()
	 */
	@Override
	public DataObjectItemInput<T> getInput() {
		return new LinkInput();
	}
	public DataObjectFactory<L> getLeftFactory(){
		return (DataObjectFactory<L>) getLeftProducer() ;
	}
   
   
	
	

	

	/**
	 * create an Iterator over Link objects
	 * 
	 * @param l
	 *            Left DataObject required null for any
	 * @param r
	 *            Right DataObject required null for any
	 * @param f
	 *            extension Filter
	 * @return Iterator over Link
	 * @throws DataFault 
	 * @throws DataFault
	 */
	
	private CloseableIterator<T> getLinkIterator(L l, R r, BaseFilter<T> f) throws DataFault{
		return getLinkIterator(l, r, f, USE_JOIN.isEnabled(getContext()));
	}
	public CloseableIterator<T> getLinkIterator(L l, R r, BaseFilter<T> f, boolean use_join)
			throws DataFault {
		if (use_join) {
			return this.new JoinLinkFilterIterator(new LinkFilter(l, r, f));
		}
		// This will still set the known links as LinkFilter is a ResultVisitor
		return new FilterIterator(getFilter(l, r, f));
	}
	public long getLinkCount(L l, R r,BaseFilter<T> f) throws DataException{
		return getCount(getFilter(l, r, f));
	}
	public T find(L l, R r,BaseFilter<T> f) throws DataException{
		return find(getFilter(l, r, f));
	}
	public T find(L l, R r,BaseFilter<T> f, boolean allow_null) throws DataException{
		return find(getFilter(l, r, f),allow_null);
	}
	public DataObjectFactory<R> getRightFactory(){
		return (DataObjectFactory<R>) getRightProducer();
	}
   
	/** get A {@link FilterResult} for link objects
	 * 
	 * @param left  Left {@link DataObject} required null for any
	 * @param right Right {@link DataObject} required null for any
	 * @param fil Additional {@link BaseFilter}
	 * @return {@link FilterResult}
	 * @throws DataFault
	 */
	@Override
	public FilterResult<T> getFilterResult(L left, R right, BaseFilter<T> fil) throws DataFault{
		return new LinkResult(left, right, fil);
	}
	
	public BaseFilter<T> getFilter(L left, R right, BaseFilter<T> fil) {
		return new LinkFilter(left, right, fil);
	}

	
	/** Get a filter for the left peer from a filter on the link
	 * 
	 * @param fil SQLFilter on self
	 * @return SQLFilter on left peer
	 */
	public BaseFilter<L> getLeftFilter(BaseFilter<T> fil){
		return convertToDestinationFilter(getLeftFactory(), getLeftField(), fil);
	}
	/** Get a filter for the left peer from a filter on the link
	 * 
	 * @param fil SQLFilter on self
	 * @return SQLFilter on left peer
	 */
	public SQLFilter<L> getLeftFilter(SQLFilter<T> fil){
		return getDestFilter(fil, getLeftField(), getLeftFactory());
	}
	/** Get a {@link SQLFilter} for the link from a {@link SQLFilter} on the left peer
	 * 
	 * @param fil SQLFilter on left peer
	 * @return SQLFilter on self
	 */
	public SQLFilter<T> getLeftJoinFilter(SQLFilter<L> fil){
		return getRemoteSQLFilter(getLeftFactory(), getLeftField(), fil);
	}
	/** get a {@link BaseFilter} for the link from a {@link BaseFilter} on the left peer
	 * 
	 * @param fil {@link BaseFilter} on left peer
	 * @return {@link BaseFilter} on self
	 */
	public BaseFilter<T> getLeftRemoteFilter(BaseFilter<L> fil){
		return getRemoteFilter(getLeftFactory(), getLeftField(), fil);
	}
	/** Get a filter for the right peer from a filter on the link.
	 * 
	 * 
	 * @param fil SQLFilter on self
	 * @return SQLFilter on left peer
	 */
	public BaseFilter<R> getRightFilter(BaseFilter<T> fil){
		return convertToDestinationFilter(getRightFactory(), getRightField(), fil);
	}
	/** Get a {@link SQLFilter} for the right peer from a {@link SQLFilter} on the link.
	 * 
	 * 
	 * @param fil SQLFilter on self
	 * @return SQLFilter on left peer
	 */
	public SQLFilter<R> getRightFilter(SQLFilter<T> fil){
		return getDestFilter(fil, getRightField(), getRightFactory());
	}
	/** Get a {@link SQLFilter} for the link from a {@link SQLFilter} on the right peer
	 * 
	 * @param fil {@link SQLFilter} on right peer
	 * @return {@link SQLFilter} on self
	 */
	public SQLFilter<T> getRightJoinFilter(SQLFilter<R> fil){
		return getRemoteSQLFilter(getRightFactory(), getRightField(), fil);
	}
	/** get a {@link BaseFilter} for the link from a {@link BaseFilter} on the right peer.
	 * 
	 * @param fil {@link BaseFilter} in right peer
	 * @return {@link BaseFilter} on self
	 */
	public BaseFilter<T> getRightRemoteFilter(BaseFilter<R> fil){
		return getRemoteFilter(getRightFactory(), getRightField(), fil);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager#isLeft(java.lang.Object)
	 */
	@Override
	protected boolean isLeft(Object o) {
		return getLeftFactory().isMine(o);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager#isRight(java.lang.Object)
	 */
	@Override
	protected boolean isRight(Object o) {
		return getRightFactory().isMine(o);
	}

	
	/**
	 * @return
	 */
	protected DataObjectItemInput<L> getLeftInput() {
		return getLeftFactory().getInput();
	}

	/**
	 * @return
	 */
	protected DataObjectItemInput<R> getRightInput() {
		return getRightFactory().getInput();
	}

	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String, Selector> result = super.getSelectors();
		result.put(getLeftField(),new Selector() {

			@Override
			public Input getInput() {
				return getLeftInput();
			}
		});
		result.put(getRightField(),new Selector() {

			@Override
			public Input getInput() {
				return getRightInput();
			}
		});
		return result;
	}

	

	
	
	
}