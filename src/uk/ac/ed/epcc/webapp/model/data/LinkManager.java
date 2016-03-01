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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.CompositeInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ConstantInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AbstractAcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultIterator;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/**
 * LinkManager is the base class of Factories that manage Link objects These
 * classes manage many-to-one relationships between model level objects. The <code>LinkManager</code>
 * class calls these the <it>left</it> and <it>right</it> objects. 
 * The actual <code>Link</code> DataObjects are pseudo inner classes of the <code>LinkManager</code>
 * Often the Link objects will remain hidden inside the <code>LinkManager</code> sub-class and only be
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
 * LinkManager can navigate the links in either direction the subclass may
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
	public abstract static class Link<L extends DataObject, R extends DataObject> extends IndexedLinkManager.Link<L,R> {
		
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
				return super.getIdentifier();
			}
		}

		
		
       
	}

    public class LinkMapper implements ResultMapper<T>{
        LinkFilter fil;
        public LinkMapper(LinkFilter f,boolean join_left, boolean join_right){
        	fil=f;
        	this.qualify=join_left || join_right;
        	this.join_left=join_left;
        	this.join_right=join_right;
        }
        boolean qualify=true;
        final boolean join_left;
        final boolean join_right;
        public boolean setQualify(boolean qualify) {
			boolean old = this.qualify;
			this.qualify = qualify;
			return old;
		}
		public T makeObject(ResultSet rs) throws DataFault {
			LinkManager<T,L,R> lm = LinkManager.this;
			T link = lm.makeObject(rs,true);

			if (fil.getLeftTarget() == null) {
				if( join_left ){
					L left =  getLeftFactory().makeObject(rs,qualify);
					link.setLeft(left);
				}
			}else{
				link.setLeft(fil.getLeftTarget());
			}
			if (fil.getRightTarget() == null) {
				if( join_right ){
					R right = getRightFactory().makeObject(rs,qualify);
					link.setRight(right);
				}
			}else{
				link.setRight(fil.getRightTarget());
			}

			return link;
		}
    	
		public String getTarget() {
			StringBuilder target = new StringBuilder();
			res.addTable(target, true);
			target.append(".* ");
			if (fil.getLeftTarget() != null && fil.getRightTarget() != null) {
				return target.toString();
			}
			if (fil.getLeftTarget() == null && join_left) {
				target.append(", ");
				getLeftFactory().res.addTable(target, true);
				target.append(".* ");
			}
			if (fil.getRightTarget() == null && join_right) {
				target.append(", ");
				getRightFactory().res.addTable(target, true);
				target.append(".* ");
			}
			return target.toString();
		}
		public String getModify() {
			// force a defined order as we may be chunking
			return OrderBy(true);
		}
		public T makeDefault() {
			return null;
		}
		public SQLFilter getRequiredFilter() {
			// TODO Should we put reference filters here instead of
			// {@link JoinLinkFilterIterator.addSource}
			return null;
		}
		public List<PatternArgument> getTargetParameters(
				List<PatternArgument> list) {
			return list;
		}
		public List<PatternArgument> getModifyParameters(
				List<PatternArgument> list) {
			return list;
		}
    }

	public class LinkFilterIterator extends ResultIterator<T> {

		public LinkFilterIterator(LinkFilter fil) throws DataFault {
			super(LinkManager.this.getContext(),LinkManager.this.getTarget());
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
	 * Version of FilterIterator that optimises the end object caches by
	 * performing a join on target tables.
	 * 
	 * 
	 * @author spb
	 * 
	 */
	public class JoinLinkFilterIterator extends ResultIterator<T> {
        private final LinkFilter fil;
        private final boolean join_left;
        private final boolean join_right;
		public JoinLinkFilterIterator(LinkFilter fil) throws DataFault {
        	this(fil,fil.getLeftTarget()==null,fil.getRightTarget()==null);
        }
    	public JoinLinkFilterIterator(LinkFilter fil,boolean join_left,boolean join_right) throws DataFault {
			super(LinkManager.this.getContext(),LinkManager.this.getTarget());
			this.fil=fil;
			this.join_left=join_left;
			this.join_right=join_right;
			boolean use_join = join_left || join_right;
			setQualify(use_join);
			setMapper(new LinkMapper(fil,join_left,join_right));
			setVisitor(fil);
			try {
				setup(fil,0,-1);
			} catch (DataException e) {
				throw new DataFault("Error in setup",e);
			}
		}

		public void addSource(StringBuilder source) {
			res.addTable(source, true);
			
		
			if (fil == null ) {
				return;
			}
			
			if (joinLeft()) {
				source.append(" JOIN ");
				getLeftFactory().res.addTable(source, true);
				source.append(" ON ");
				res.getInfo(getLeftField()).addName(source, true, true);
				source.append("=");
				getLeftFactory().res.addUniqueName(source, true, true);
			}
			if (joinRight()) {
				source.append(" JOIN ");
				getRightFactory().res.addTable(source, true);
				source.append(" ON ");
				res.getInfo(getRightField()).addName(source, true, true);
				source.append("=");
				getRightFactory().res.addUniqueName(source, true, true);
			}


		}

		private boolean joinRight() {
			return join_right && res.getDBTag() == getRightFactory().res.getDBTag();
		}

		private boolean joinLeft() {
			return join_left && res.getDBTag() == getLeftFactory().res.getDBTag();
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
				addInput("left",  new ConstantInput<Integer>(left.getIdentifier(),left.getID()));
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
				addInput("right",  new ConstantInput<Integer>(right.getIdentifier(),right.getID()));
			}
		}

		public T getItem() {
			Number num = getValue();
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
				setValue(null);
				return;
			}
			if (v.trim().length() == 0) {
				setValue(null);
				return;
			}
			try {
				Integer i;

				i = new Integer(Integer.parseInt(v.trim()));

				setValue(i);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid integer format");
			}

		}

		public void setItem(T l) {

		
			setValue(new Integer(l.getID()));

		}

		@Override
		public Integer setValue(Integer v) throws TypeError {
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
			throw new TypeError("Unknown type pased to LinkInput");
		}
        public Integer convert(Object v){
        	if( v == null || v instanceof Integer){
        		return (Integer) v;
        	}
        	if( v instanceof Number){
        		return new Integer(((Number)v).intValue());
        	}
        	throw new TypeError("Unknown type pased to LinkInput");
        }
		@Override
		public void validate() throws FieldException {
			super.validate();
			Number left = left_input.getValue();
			Number right = right_input.getValue();
			if (left == null || right == null) {
				// ok if optional
				if (isOptional()) {
					return;
				}
				throw new MissingFieldException();
			}
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

		public T getDataObject() {
			return getItem();
		}

	}
	/** A {@link FilterResult} for link objects.
	 * 
	 * @author spb
	 *
	 */
	public class LinkResult extends AbstractFilterResult<T> implements FilterResult<T>{
		public LinkResult(L left, R right, BaseFilter<? super T> fil) throws DataFault {
			super();
			this.left=left;
			this.right = right;
			this.fil = fil;
			iter=getLinkIterator(left,right, fil);
		}

		private final L left;
		private final R right;
		private final BaseFilter<? super T> fil;
		private Iterator<T> iter;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<T> iterator() {
			try {
				if( iter != null ){
					Iterator<T> result=iter;
					iter=null;
					return result;
				}
				return getLinkIterator(left,right, fil);
			} catch (DataFault e) {
				LinkManager.this.getLogger().error("Error making iterator for LinkResult", e);
				return null;
			}
		}
	}

	/** A {@link FilterResult} for left link objects.
	 * 
	 * @author spb
	 *
	 */
	public class LeftResult extends AbstractFilterResult<L> implements FilterResult<L>{
		public LeftResult(R right, BaseFilter<? super T> fil) throws DataFault {
			super();
			this.right = right;
			this.fil = fil;
			iter=getLeftIterator(right, fil);
		}

		private final R right;
		private final BaseFilter<? super T> fil;
		private Iterator<L> iter;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<L> iterator() {
			try {
				if( iter != null ){
					Iterator<L> result=iter;
					iter=null;
					return result;
				}
				return getLeftIterator(right, fil);
			} catch (DataFault e) {
				LinkManager.this.getLogger().error("Error making iterator for LeftResult", e);
				return null;
			}
		}
	}

	/** A {@link FilterResult} for right link objects.
	 * 
	 * @author spb
	 *
	 */
	public class RightResult extends AbstractFilterResult<R> implements FilterResult<R>{
		public RightResult(L left, BaseFilter<? super T> fil) throws DataFault {
			super();
			this.left = left;
			this.fil = fil;
			iter=getRightIterator(left, fil);
		}

		private final L left;
		private final BaseFilter<? super T> fil;
		private Iterator<R> iter;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<R> iterator() {
			try {
				if( iter != null ){
					Iterator<R> result=iter;
					iter=null;
					return result;
				}
				return getRightIterator(left, fil);
			} catch (DataFault e) {
				LinkManager.this.getLogger().error("Error making iterator for RightResult", e);
				return null;
			}
		}
	}

	/** A wrapper to convert a non SQL filter on the link object into an {@link AcceptFilter}
	 * on the left object
	 * 
	 * @author spb
	 *
	 */
    public class LeftAcceptFilter extends  AbstractAcceptFilter<L>{
    	/**
		 * @param target
		 * @param fil
		 */
		public LeftAcceptFilter( BaseFilter<T> fil) {
			super(getLeftFactory().getTarget());
			this.fil = fil;
		}

		private final BaseFilter<T> fil;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(L o) {
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
    public class RightAcceptFilter extends  AbstractAcceptFilter<R>{
    	/**
		 * @param target
		 * @param fil
		 */
		public RightAcceptFilter( BaseFilter<T> fil) {
			super(getRightFactory().getTarget());
			this.fil = fil;
		}

		private final BaseFilter<T> fil;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean accept(R o) {
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
			getContext().error(e,"Error making index");
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
	@Override
	public Iterator<T> getLinkIterator(L l, R r, BaseFilter<? super T> f) throws DataFault{
		return getLinkIterator(l, r, f, true);
	}
	public Iterator<T> getLinkIterator(L l, R r, BaseFilter<? super T> f, boolean use_join)
			throws DataFault {
		if (use_join) {
			return this.new JoinLinkFilterIterator(new LinkFilter(l, r, f));
		}
		return new LinkFilterIterator(new LinkFilter(l, r, f));
	}
	public long getLinkCount(L l, R r,BaseFilter<? super T> f) throws DataException{
		return getCount(new LinkFilter(l, r, f));
	}
	public DataObjectFactory<R> getRightFactory(){
		return (DataObjectFactory<R>) getRightProducer();
	}
   
	/** get A {@link FilterResult} for link objects
	 * 
	 * @param left  Left {@link DataObject} required null for any
	 * @param right Right {@link DataObject} requeired null for any
	 * @param fil Additional {@link BaseFilter}
	 * @return {@link FilterResult}
	 * @throws DataFault
	 */
	public FilterResult<T> getResult(L left, R right, BaseFilter<? super T> fil) throws DataFault{
		return new LinkResult(left, right, fil);
	}

	@Override
	public Class<? super T> getTarget(){
		return Link.class;
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
		return new DestFilter<L>(fil, getLeftField(), getLeftFactory());
	}
	/** Get a {@link SQLFilter} for the link from a {@link SQLFilter} on the left peer
	 * 
	 * @param fil SQLFilter on left peer
	 * @return SQLFilter on self
	 */
	public SQLFilter<T> getLeftJoinFilter(SQLFilter<? super L> fil){
		return new RemoteFilter<L>(fil, getLeftField(), getLeftFactory());
	}
	/** get a {@link BaseFilter} for the link from a {@link BaseFilter} on the left peer
	 * 
	 * @param fil {@link BaseFilter} on left peer
	 * @return {@link BaseFilter} on self
	 */
	public BaseFilter<T> getLeftRemoteFilter(BaseFilter<? super L> fil){
		return getRemoteFilter(getLeftFactory(), getLeftField(), fil);
	}
	
	public FilterResult<L> getLeftResult(R right, BaseFilter<? super T> fil) throws DataFault{
		return new LeftResult(right, fil);
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
		return new DestFilter<R>(fil, getRightField(), getRightFactory());
	}
	/** Get a {@link SQLFilter} for the link from a {@link SQLFilter} on the right peer
	 * 
	 * @param fil {@link SQLFilter} on right peer
	 * @return {@link SQLFilter} on self
	 */
	public SQLFilter<T> getRightJoinFilter(SQLFilter<? super R> fil){
		return new RemoteFilter<R>(fil, getRightField(), getRightFactory());
	}
	/** get a {@link BaseFilter} for the link from a {@link BaseFilter} on the right peer.
	 * 
	 * @param fil {@link BaseFilter} in right peer
	 * @return {@link BaseFilter} on self
	 */
	public BaseFilter<T> getRightRemoteFilter(BaseFilter<? super R> fil){
		return getRemoteFilter(getRightFactory(), getRightField(), fil);
	}
	public FilterResult<R> getRightResult(L left, BaseFilter<? super T> fil) throws DataFault{
		return new RightResult(left, fil);
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

	@Override
	public Iterator<L> getLeftIterator(R r, BaseFilter<? super T> f)
			throws DataFault {
		// Force join to left only
		return new LeftIterator(new JoinLinkFilterIterator(new LinkFilter(null, r, f),true,false));
	}

	@Override
	public Iterator<R> getRightIterator(L l, BaseFilter<? super T> f)
			throws DataFault {
		// Force join to right only
		return new RightIterator(new JoinLinkFilterIterator(new LinkFilter(l, null, f),false,true));
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
	protected Map<String, Object> getSelectors() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(getLeftField(),new Selector() {

			public Input getInput() {
				return getLeftInput();
			}
		});
		result.put(getRightField(),new Selector() {

			public Input getInput() {
				return getRightInput();
			}
		});
		return result;
	}

	

	
	
	
}