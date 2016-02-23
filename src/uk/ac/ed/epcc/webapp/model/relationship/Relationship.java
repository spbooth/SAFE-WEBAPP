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
package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.AbstractTableRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.AddBooleanFieldTransition;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.CompositeTableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.DropFieldTransition;
import uk.ac.ed.epcc.webapp.jdbc.table.DropTableTransition;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableStructureTransitionTarget;
import uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.TransitionSource;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.RoleSelector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.table.TableStructureLinkManager;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;


/** Link class that encodes a relationship between an AppUser and a
 * domain object and used to implement {@link RelationshipProvider} and {@link RoleSelector} for the domain object.
 *
 * 
 * Possible roles are encoded as boolean fields in the table.
 * Unless overridden in a sub-class. If the table is auto_created the initial set of roles are set in
 * <b>relationship.<em>table</em>.create_roles</b> as a comma separated list.
 * The property <b>reference.<em>table</em>.TargetID</b> must be set to the config tag
 * of the domain object.
 * 
 * 
 * @author spb
 *
 * @param <A>
 * @param <B>
 */


public class Relationship<A extends AppUser,B extends DataObject> extends 
         TableStructureLinkManager<Relationship.Link<A,B>,A,B> implements 
         RelationshipProvider<A, B>, RoleSelector<B>{
    
	// This is the default field sub-classes may use a different value
	private static final String TARGET_ID = "TargetID";
	private static final String PERSON_ID = "PersonID";

	@SuppressWarnings("unchecked")
	public Relationship(AppContext c,String tag){
    	this(c,tag,c.makeObject(DataObjectFactory.class, c.getInitParameter("reference."+tag+"."+TARGET_ID)),TARGET_ID);
    }
	
	/** Extension constructor to allow sub-classes to set factory and field
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected Relationship(AppContext c,String tag, DataObjectFactory<B> b_fac, String field) {
		super(c,tag,c.getService(SessionService.class).getLoginFactory(),PERSON_ID,b_fac,field);
	}
	/** Constructor to allow sub-classes to set factory
	 * 
	 * @param c
	 * @param tag
	 * @param target_fac
	 */
	@SuppressWarnings("unchecked")
	protected Relationship(AppContext c, String tag,DataObjectFactory<B> target_fac){
		this(c,tag,target_fac,TARGET_ID);
	}
    
    @Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,String table,
			IndexedProducer<A> leftFac, String leftField,
			IndexedProducer<B> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table,leftFac, leftField, rightFac,
						rightField);


		for(String r : getDefaultRoles(c, table)){
			spec.setField(r, new BooleanFieldType(false,false));
		}

		return spec;
	}
    /** generate a list of the default roles to create when bootstrapping a table.
     * 
     * 
     * @param c
     * @param table
     * @return
     */
    protected String[] getDefaultRoles(AppContext c, String table){
    	String roles = c.getInitParameter("relationship."+table+".create_roles");
    	if( roles == null || roles.trim().length() == 0 ){
    		return new String[0];
    	}
    	return roles.trim().split("\\s*,\\s*");
    }

	public static class Link<A extends AppUser,B extends DataObject> extends LinkManager.Link<A,B>{

		protected Link(Relationship< A, B> arg0, Record arg1) {
			super(arg0, arg1);
		}

		@Override
		protected void setup() throws DataFault, DataException {
		}
		public boolean hasRole(String role){
			return record.getBooleanProperty(role, false);
		}
		public void setRole(String role, boolean value){
			record.setProperty(role, value);
		}
    	
    }

	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Link<A,B>(this,res);
	}
	public SQLFilter<Link<A,B>> getUserRoleFilter(A user,String role){
		if( res.hasField(role) && user != null){
			SQLAndFilter<Link<A,B>> fil = new SQLAndFilter<Link<A,B>>(getTarget());
			fil.addFilter( new SQLValueFilter<Link<A,B>>(getTarget(),res,role,Boolean.TRUE));
			fil.addFilter(new ReferenceFilter<Link<A,B>,A>(this,PERSON_ID,user));
			return fil;
		}
		return new FalseFilter<Link<A,B>>(getTarget());
	}
	public SQLFilter<Link<A,B>> getTargetRoleFilter(B target,String role){
		if( res.hasField(role) && target != null){
			SQLAndFilter<Link<A,B>> fil = new SQLAndFilter<Link<A,B>>(getTarget());
			fil.addFilter( new SQLValueFilter<Link<A,B>>(getTarget(),res,role,Boolean.TRUE));
			fil.addFilter(new ReferenceFilter<Link<A,B>,B>(this,TARGET_ID,target));
			return fil;
		}
		return new FalseFilter<Link<A,B>>(getTarget());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getTargetFilter(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public SQLFilter<B> getTargetFilter(AppUser user,String role){
		return getRightFilter(getUserRoleFilter((A) user,role));
	}
	
	@SuppressWarnings("unchecked")
	public SQLFilter<A> getUserFilter(B target,String role){
		return getLeftFilter(getTargetRoleFilter(target, role));
	}
    public class RoleInput extends IntegerInput implements DataObjectItemInput<B>,ListInput<Integer,B>{
        private A user;
        private String role;
    	public RoleInput(A user,String role){
    		this.user=user;
    		this.role=role;
    	}
		public B getDataObject() {
			return getItem();
		}

		public B getItem() {
			return getItembyValue(getValue());
		}

		public void setItem(B item) {
			setValue(item.getID());
			
		}

		public B getItembyValue(Integer value) {
			
				return getRightFactory().find(value);
		}

		public Iterator<B> getItems() {
			try {
				return new RightIterator(getLinkIterator(user, null, getUserRoleFilter(user,role),false));
			} catch (DataFault e) {
				getContext().error(e,"Error making iterator");
				return null;
			}
		}
		
		public int getCount(){
			try{
			return (int)Relationship.this.getCount(getUserRoleFilter(user, role));
			}catch(Exception e ){
				getContext().error(e,"Error getting count");
				return 0;
			}
		}

		public String getTagByItem(B item) {
			
			return Integer.toString(item.getID());
		}

		public String getTagByValue(Integer value) {
			return value.toString();
		}
		@Override
		public String getPrettyString(Integer val) {
			String res =  getText(getItembyValue(val));
			if( res == null ){
				res = "Not Selected";
			}
			return res;
		}
		public String getText(B item) {
			if( item== null ){
				return null;
			}
			return item.getIdentifier();
		}
		@Override
		public <R> R accept(InputVisitor<R> vis) throws Exception {
			return vis.visitListInput(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
		 */
		@Override
		public boolean isValid(B item) {
			Link l;
			try {
				l = getLink(user, item);
			} catch (DataException e) {
				return false;
			}
			if( l == null){
				return false;
			}
			return l.hasRole(role);
		}
    	
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getInput(java.lang.String, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@SuppressWarnings("unchecked")
	public DataObjectItemInput<B> getInput(String role, SessionService user) {
		return new RoleInput((A) user.getCurrentPerson(), role);
	}
	

	public class RelationshipTableRegistry extends AbstractTableRegistry{

		public RelationshipTableRegistry(){
			addTableTransition(new TransitionKey<Relationship>(Relationship.class, "DropTable"), new DropTableTransition<Relationship>(getContext()));
			addTableTransition(new TransitionKey<Relationship>(Relationship.class, "DropField"), new DropFieldTransition<Relationship>(res));
			addTableTransition(new TransitionKey<Relationship>(Relationship.class, "AddRole"), new AddBooleanFieldTransition<Relationship>(res));
		}
		public boolean allowTableTransition(TransitionKey name,SessionService operator) {
			return operator.hasRole(SessionService.ADMIN_ROLE);
		}

		public void getTableTransitionSummary(ContentBuilder hb,SessionService operator) {
			hb.addHeading(3,"Roles");
			ExtendedXMLBuilder xml = hb.getText();
			xml.open("ul");
			for(String role : getRelationships()){
				xml.open("li");
				xml.clean(role);
				xml.close();
			}
			xml.close();
			xml.appendParent();
		}
		
	}
	
	protected TableTransitionRegistry makeTableRegistry() {
		return new RelationshipTableRegistry();
	}
	public static void makeTable(AppContext conn,
			String my_table, String peer_table) throws DataFault {
		assert(peer_table != null);
		assert(my_table != null);
		TableSpecification s = new TableSpecification();
		s.setField(PERSON_ID, new IntegerFieldType()); // int ok as factory got from session service
		s.setField(TARGET_ID, new ReferenceFieldType(peer_table));
		try {
			s.new Index("Link", true, PERSON_ID, TARGET_ID);
		} catch (InvalidArgument e) {
			conn.error(e,"Error making index");
			return;
		}
		// best we can do
		DataBaseHandlerService handler = conn.getService(DataBaseHandlerService.class);
		if( handler != null ){
			handler.createTable(my_table, s);
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(A, B, java.lang.String)
	 */
	public boolean hasRole(A user, B target, String role){
		if( ! getLeftFactory().isMine(user) || ! getRightFactory().isMine(target)){
			return false;
		}
		try{
			Link<A,B> link = getLink(user, target);
			if( link != null && link.hasRole(role)){
				return true;
			}
			return false;
		}catch(Exception e){
			getContext().error(e,"Error getting role");
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#setRole(A, B, java.lang.String, boolean)
	 */
	public void setRole(A user, B target, String role, boolean value){
		if( ! getLeftFactory().isMine(user) || ! getRightFactory().isMine(target)){
			throw new ConsistencyError("Factory types do not match");
		}
		if( ! getRelationships().contains(role)){
			getContext().error("Invalid role "+role);
			return;
		}
		try{
			Link<A,B> link = makeLink(user, target);
			link.setRole(role, value);
			link.commit();
		}catch(Exception e){
			getContext().error(e,"Error making role");
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean hasRole(SessionService sess,B target,String role){
		return hasRole((A) sess.getCurrentPerson(),target ,role);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getRoles()
	 */
	public Set<String> getRelationships(){
		Set<String> result = new HashSet<String>();
		for( String s: res.getFields()){
			if( res.getInfo(s).isBoolean()){
				result.add(s);
			}
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager#selectLink(uk.ac.ed.epcc.webapp.model.data.Indexed, uk.ac.ed.epcc.webapp.model.data.Indexed)
	 */
	@Override
	protected Link<A, B> selectLink(A leftEnd, B rightEnd) throws Exception {
		return makeLink(leftEnd, rightEnd);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(java.lang.String, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@SuppressWarnings("unchecked")
	public boolean hasRole(String role, SessionService user) {
		A u = (A) user.getCurrentPerson();
		if( u == null){
			return false;
		}
		try {
			return exists(getUserRoleFilter(u, role));
		} catch (DataException e) {
			getContext().error(e,"Error checking match");
			return false;
		}
	}
	public boolean canCreate(SessionService c){
		// link objects are created from the update form
		return false;
	}

	public DataObjectFactory<B> getTargetFactory() {
		return getRightFactory();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#hasRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String)
	 */
	@Override
	public BaseFilter<B> hasRelationFilter(SessionService<A> sess, String role) {
		return getTargetFilter(sess.getCurrentPerson(), role);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#personInRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public BaseFilter<A> personInRelationFilter(SessionService<A> sess, String role, B target) {
		return getUserFilter(target, role);
	}


}