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
package uk.ac.ed.epcc.webapp.session;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
import uk.ac.ed.epcc.webapp.model.data.RemoteAccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.RelationshipProvider;
import uk.ac.ed.epcc.webapp.session.perms.ExplainPermissionVisitor;
import uk.ac.ed.epcc.webapp.session.perms.PermParser;
import uk.ac.ed.epcc.webapp.session.perms.PermissionClause;
import uk.ac.ed.epcc.webapp.session.perms.PersonInRelationshipRoleFilterPermissionVisitor;
import uk.ac.ed.epcc.webapp.session.perms.RelationshipRoleFilterPermissionVisitor;
import uk.ac.ed.epcc.webapp.session.perms.SessionRelationshipRoleFilterPermissionVisitor;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;
/** Abstract base implementation of {@link SessionService}
 * <p>
 * A config parameter of the form <b>use_role.<i>role-name</i></b> defines a role-name mapping
 * the value of the parameter is the actual role queried. A comma separated list of sufficient roles 
 * may also be specified. Multiple roles separated by + must all be present. If both command and + are present then
 * the + combination binds more tightly.
 * <p>
 * A role of the form <b><i>tag</i>%<i>rel[@name]</i></b> is possessed by a user if that user
 * has relationship (see below) <i>rel</i> against one of the records from factory constructed using <i>tag</i>.
 * If the optional name-filter  <i>name</i> is specified it must be one of the records that match that filter.
 * <p>
 * A role starting with <i>@</i> denotes a named filter on the {@link AppUser} that must match a
 * person for them to have the role.
 * <p>
 * The {@link AppUserFactory} or its {@link Composite}s can provide roles by implementing
 * {@link StateRoleProvider}. 
 * <p>
 * Relationships are configured via the {@link ConfigService} by setting:
 * <b>use_relationship.<em>factory-tag</em>.<em>relationship</em></b>
 * If this is a comma separated list it implies an OR of the component parts.
 * within this AND combinations can be specified as + separated terms. If both OR and AND combinations exist 
 * the AND operator binds more tightly.
 * <p>
 * The factory (or its {@link Composite}s) can implement {@link AccessRoleProvider} to provide relationships.
 * <p>
 * Roles of the form <i>field</i><b>-></b><i>remote_relationship</i> denotes a remote filter
 * joined via the reference field <i>field</i> A person has these relationships with the target object
 * if they have the <i>remote_relationship</i> on the object the target references. The remote relationship must be unqualified.
 * <p>
 * Relationship names containing a period are qualified names the qualifier can be:
 * <ul>
 * <li> <b>global</b> the relationship is a global role not a relationship.</li>
 * <li> <b>boolean</b> Use a boolean filter so all/none relationships match.</li>
 * <li> <b>feature</b> Use a boolean filter generated by a feature switch.</li>
 * <li> <em>factory-tag</em> un-modified relationship from factory or a named filter from
 * a {@link NamedFilterWrapper} wrapping the factory. Named filters resolve true/false depending
 * on whether any targets exist that match the filter.
 * <li> The tag of a {@link RelationshipProvider} for the target.</li>
 * <li> The tag of a {@link AccessRoleProvider}</li>
 * </ul> 
 * 
 * When generating a filter for all people with a relationship against any target named filters can
 * only check that some target matches the filter. Complex relationships involving these in AND combination
 * with other clauses may be less restrictive than desired in this case.
 * 
 * 
 * @author spb
 * @see NamedFilterWrapper
 * @see RemoteAccessRoleProvider
 * @param <A>
 */
@PreRequisiteService(ConfigService.class)
public abstract class AbstractSessionService<A extends AppUser> extends AbstractContexed implements SessionService<A>{
	/** Property prefix to allow role name aliasing.
	 * The property use_role.<i>name</i> defines a role-name mapping. 
	 * 
	 * 
	 */
	public static final String USE_ROLE_PREFIX = "use_role.";
	public static final Feature TOGGLE_ROLES_FEATURE = new Feature("toggle_roles",true,"allow some roles to toggle on/off");
	public static final Feature CACHE_RELATIONSHIP_FEATURE = new Feature("cache_relationships",true,"cache relationship test results in the session");
	public static final Feature APPLY_DEFAULT_PERSON_RELATIONSHIP_FILTER = new Feature("relationships.apply_default_person_filter",true,"Apply the default person relationship filter when generating a filter on person");
	public static final Feature APPLY_DEFAULT_TARGET_RELATIONSHIP_FILTER = new Feature("relationships.apply_default_target_filter",true,"Apply the default target relationship filter when generating a filter");
	public static final Feature ALLOW_UNKNOWN_RELATIONSHIP_IN_OR_FEATURE = new Feature("relationship.allow_unknown_in_or",true,"Only skip the bad branches of a mis defined OR relationship");
	public static final String ROLE_LIST_CONFIG = "role_list";
	 
	private Map<String,Boolean> toggle_map=null;
	
	private A person=null;
    private AppUserFactory<A> fac=null;
    /** Map of roles that this user can assume.
	 * roles can either be always on or can be toggled on and off.
	 */
	private Map<String,Boolean> role_map;
	
	public static final String ROLE_PERSON_ID = "PersonID";
	public static final String ROLE_FIELD = "Role";
	public static final String ROLE_TABLE = "role_table";
	
	public static final String person_tag = "SESSION_PersonID";
	private static final String toggle_map_tag = "SESSION_toggle_map";
	private static final String role_map_tag = "SESSION_role_map";
	
	public static final String auth_time_tag = "SESSION_auth_time";
	public static final String auth_type_tag = "SESSION_auth_type";
	private boolean apply_toggle=true;
	private final PermParser<A> perm_parser = new PermParser<A>(this);
	/** A keying object representing a relationship.
	 * 
	 * @author spb
	 *
	 */
	static class RelationshipTag implements Serializable{
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			result = prime * result + ((role == null) ? 0 : role.hashCode());
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
			RelationshipTag other = (RelationshipTag) obj;
			if (id != other.id)
				return false;
			if (role == null) {
				if (other.role != null)
					return false;
			} else if (!role.equals(other.role))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			return true;
		}
		public RelationshipTag(String tag, int id, String role) {
			super();
			this.tag = tag;
			this.id = id;
			this.role = role;
		}
		public final String tag;
		public final int id;
		public final String role;
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "RelationshipTag [tag=" + tag + ", id=" + id + ", role=" + role + "]"+hashCode();
		}
	}
	private Map<RelationshipTag,Boolean> relationship_map=null;
	// map of roles to filters.
	private Map<String,BaseFilter> roles = new HashMap<>();
	public AbstractSessionService(AppContext c) {
		super(c);
	}

	/** clear all cached relationships
	 * 
	 */
	@Override
	public void flushRelationships(){
		relationship_map=null;
		roles.clear();
	}
	public static void setupRoleTable(AppContext ctx){
		DataBaseHandlerService dbh = ctx.getService(DataBaseHandlerService.class);
		if(dbh != null &&  ! dbh.tableExists(SimpleSessionService.ROLE_TABLE)){
			TableSpecification s = new TableSpecification();
			s.setField(SimpleSessionService.ROLE_PERSON_ID, new IntegerFieldType(false, null));
			s.setField(SimpleSessionService.ROLE_FIELD, new StringFieldType(false, null,32));
			try {
				dbh.createTable(SimpleSessionService.ROLE_TABLE, s);
			} catch (DataFault e) {
				ctx.getService(LoggerService.class).getLogger(AbstractSessionService.class).error("Failed to make role_table",e);
			}
		}
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public AppUserFactory<A> getLoginFactory() {
		if( fac != null ){
			return fac;
		}
		try{
			AppContext c = getContext();
			Logger log =c.getService(LoggerService.class).getLogger(getClass());
			String table=getLoginTable();
			log.debug("login-table="+table);
			Class<? extends AppUserFactory> clazz;
			if( table == null ){
				// If the login factory has a hardwired table then we specify the class
				// name as follows
				log.debug("looking for login-factory should be "+getContext().getInitParameter("class.login-factory","unset"));
				clazz = c.getPropertyClass(getDefaultFactoryClass(), null,"login-factory");
			}else{
				// look up the class for this table. We ignore any login-factory
				// specification to make sure that we cant ever have two different
				// classes for the same repository. 
				log.debug("lookup for "+table);
				clazz = c.getPropertyClass(getDefaultFactoryClass(),table);
			}
			
			if( clazz == null ){
				error("No class found for login factory");
				throw new ConsistencyError("No class found for login factory");
			}
			if( table != null ){
				fac= c.makeParamObject(clazz,c,table);
			}else{
				fac= c.makeObject(clazz);
			}
			if( fac == null){
				error("Null login factory class="+clazz.getCanonicalName());
				throw new ConsistencyError("Null login factory "+clazz.getCanonicalName());
			}
			return fac;
		}catch(ConsistencyError ce){
			throw ce;
		}catch(Exception e){
			error(e,"Error making login factory");
			throw new ConsistencyError("Bad login factory or bad database connection",e);
		}
	}

	/**
	 * @return
	 */
	protected String getLoginTable() {
		return getContext().getInitParameter("login-table");
	}

	protected Class<? extends AppUserFactory> getDefaultFactoryClass(){
		return AppUserFactory.class;
	}
	/** get the current State of a role toggle or null if not a toggle role
	 * 
	 * @param role
	 * @return Boolean or null
	 */
	@Override
	public final Boolean getToggle(String role){
		if( ! apply_toggle ) {
			return null;
		}
		if( toggle_map == null ){
			setupToggleMap();
			if( toggle_map == null){
				return null;
			}
		}
		Boolean v = toggle_map.get(role);
		return v;
	}
	/** Set the toggle state of a role
	 * 
	 * @param name String role to set
	 * @param value boolean value to set
	 */
	@Override
	public void setToggle(String name, boolean value) {
		flushRelationships();
		if( toggle_map == null ){
			setupToggleMap();
		}
		if( toggle_map != null && toggle_map.containsKey(name)){
			toggle_map.put(name,Boolean.valueOf(value));
			saveMap(); // re-add to session to trigger sync
		}else{
			error("setToggle for non toggle role "+name);
		}
	}
	/** Toggle the sate of a role
	 * return the new value of the toggle or null if its not a togglable role
	 * 
	 * @param name String role to toggle
	 * @return Boolean or null
	 */
	@Override
	public Boolean toggleRole(String name){
		if( toggle_map == null ){
			setupToggleMap();
		}
		Boolean v = toggle_map.get(name);
		if( v != null ){
			v = Boolean.valueOf(! v);
		   toggle_map.put(name, v);
		   saveMap(); // re-add to session to trigger sync
		}else{
			error("toggleRole called for not toggle role "+name);
		}
		return v;
	}
	/** Create the initial map of toggle role statuses.
	 * These are roles that a user can switch between.
	 * 
	 * List is defined in the property <b>toggle_roles</b>.
	 * Initial value defaults to off but can be changed by setting the boolean parameter
	 * <b>toggle_roles.initial_value.<em>role</em></b>
	 * 
	 * @return Map<String,Boolean>
	 */
	public Map<String,Boolean> makeToggleMap(){
		HashMap<String,Boolean> map = new HashMap<>();

		
		String additions = getContext().getInitParameter("toggle_roles",SessionService.ADMIN_ROLE);
		if( additions != null ){
			for(String s : additions.split(",")){
				map.put(s,getContext().getBooleanParameter("toggle_roles.initial_value."+s, false));
			}
		}
		return  map;
	}
	

	public Map<String,Boolean> getToggleMap(){
		if( toggle_map == null ){
			setupToggleMap();
		}
		return new HashMap<>(toggle_map);
	}



	@SuppressWarnings("unchecked")
	private void setupToggleMap() {
		if(TOGGLE_ROLES_FEATURE.isEnabled(getContext()) ){
			toggle_map = (Map<String, Boolean>) getAttribute(toggle_map_tag);
			if( toggle_map == null ){
				toggle_map = makeToggleMap();
				saveMap();
			}
		}
	}

	private void saveMap() {
		setAttribute(toggle_map_tag, toggle_map);
	}
	
	
	/**
	 * Has this person a particular SAF role this is used for adding special
	 * permissions to SAF users
	 * 
	 * @param name of role to be tested
	 * @return true if person has role.
	 * 
	 */
	@Override
	public final boolean hasRole(String name){
		return hasRole(null,name);
	}
	
	/** underlying role check. We want to skip recursive definitions
	 * so we include a skip list
	 * 
	 * @param skip set of roles already checked
	 * @param name name of role to check
	 * @return
	 */
	private final boolean hasRole(Set<String> skip,String name){
		if( skip == null){
			skip = new HashSet<>();
		}
		skip.add(name);
		// role name aliasing
		
		// shortcutTest must bypass
		// all caching.
		// canHaveRole must not do alias expansion, we need to do it at this level
		// so we can check toggle values on values in aliases
		boolean result=false;
		if( shortcutTestRole(name) || canHaveRole(name)){ // check queried role first
			result=true;
		}else{
			String alt_list = mapRoleName(name);
			if( alt_list != null && ! alt_list.isEmpty()){

				for(String r : alt_list.split("\\s*,\\s*")){
					if( ! skip.contains(r)){
						if( hasRole(skip,r)){
							result=true;
							break;
						}
					}
				}
			}
		}
		skip.remove(name);
		if( ! result ){
			return false;
		}

		
		// check the toggle value if this returns null then we have a
		// a non toggling role
		Boolean toggle = getToggle(name);
		if( toggle != null ){
			// only match was a toggle
			return toggle.booleanValue();
		}
		
		return true;
		
	}

	/** map role name to a comma separated list of alternative roles to check.
	 * 
	 * Note the original name should always be checked explicitly first
	 * with the alternatives only checked if that role is not found
	 * 
	 * @param name
	 * @return role to use
	 */
	@Override
	public String mapRoleName(String name) {
		return getContext().getInitParameter(USE_ROLE_PREFIX+name, name).trim();
	}
	
	
	
	/** Get the set of toggle roles this user is capable of assuming
	 * 
	 * @return Set<String>
	 */
	@Override
	public Set<String> getToggleRoles(){
		if( toggle_map == null ){
			setupToggleMap();
		}
		// have predictable order
		Set<String> set = new TreeSet<>();
		if( toggle_map != null ){
		for(String s : toggle_map.keySet()){
			if( canHaveRole(s)){
				set.add(s);
			}
		}
		}
		return set;
	}
	/** Checks if this session can have the role (ignoring toggle values).
	 * No name mapping is applied. (to allow toggle roles to work correctly)
	 * 
	 * @param role
	 * @return  true if role set/permitted
	 */
	public final boolean canHaveRole(String role){
		// final so that sub-classes see the role cache.
		if( role == null ){
			return false;
		}
		if (role_map == null) {
			role_map = setupRoleMap();
		}
		Boolean answer = role_map.get(role);
		if (answer == null) {
			if( haveCurrentUser() ){
				answer = testRole(role);
				if( answer != null && role_map != null) {
					role_map.put(role, answer);
				}else {
					return false;
				}
			}else{
				return false;
			}
			
		}
		return answer.booleanValue();
		
	}
	
	

	/** perform a non-cached role-check. 
	 * This is called every time a role is checked. If it returns true the role is allowed
	 * for that call only. If it returns false the cache and the testRole function are queried.
	 * This is to allow per request roles e.g. ones tied to a particular url.
	 * 
	 * @param role
	 * @return
	 */
	protected boolean shortcutTestRole(String role){
		return false;
	}


	@SuppressWarnings("unchecked")
	private HashMap<String, Boolean> setupRoleMap() {
		HashMap<String,Boolean> result = (HashMap<String, Boolean>) getAttribute(role_map_tag);
		if( result != null ){
			return result;
		}
		result = new HashMap<>();
		setAttribute(role_map_tag, result);
		return result;
	}
	/** underlying check for role membership.
	 * Sub-classes can override this. 
	 * No role mapping is applied. (to allow toggle roles to work correctly)
	 * 
	 * @param role
	 * @return
	 */
	protected  Boolean testRole(String role){
	    return canHaveRole(null,getCurrentPerson(), role,false);
	}

	/** clears all record of the current person.
	 * 
	 * The toggle-map is not cleared. It will have no effect if a new person
	 * does not have the role but it allows toggle state to be retained acSross a SU.
	 * 
	 */
	@Override
	public void clearCurrentPerson() {
		flushRelationships();
		clearRoleMap();
		person=null;
		removeAttribute(person_tag);
	}
	
	@Override
	public void logOut(){
		clearCurrentPerson();
		removeAttribute(toggle_map_tag);
	}

	@Override
	public final  A getCurrentPerson() {
		if( person == null && haveCurrentUser()){
			person = lookupPerson();
		}
		
		return person;
	}

	private void clearRoleMap(){
		removeAttribute(role_map_tag);
		role_map=null;
	}
	@Override
	public Set<String> getStandardRoles(){
		Set<String> result = new LinkedHashSet<>();
		try {
			for(String s :getContext().getExpandedProperty(ROLE_LIST_CONFIG, SessionService.ADMIN_ROLE).split(",")){
				result.add(s);
			}
		}catch(Exception e) {
			error(e, "Error getting role list");
		}
		return result;
	}
	/** extension point for canLogin check.
	 * superclasses may want to supress this check in SU mode to allow
	 * su to non valid account.
	 * 
	 * @param person
	 * @return
	 */
	protected boolean canLogin(A person){
		return person.canLogin();
	}

	/** extracted method to look up person from the cached id.
	 * This can be extended by sub-classes e.g. to add 
	 * login tracking.
	 * 
	 * @return
	 */
	protected A lookupPerson() {
		Integer personID = getPersonID();
		if( personID == null  || personID.intValue() == 0){
			return null;
		}
		try {
			person = getLoginFactory().find(personID);
			if( person == null ){
				// not worked for some reason
				clearCurrentPerson();
				return null;
			}
		} catch (Exception e) {
			clearCurrentPerson();  // clear first as error will try to report person
			error(e,"Error finding person by id "+personID);
			return null;
		}
		if( person != null && ! canLogin(person)){
			
			// login now forbidden
			clearCurrentPerson();
			return null;
		}
		return person;
	}

	 /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.SessionService#isAuthenticated()
	 */
	@Override
	public final boolean isAuthenticated() {
		return person != null || (getAttribute(person_tag) != null);
	}
	@Override
	public final boolean haveCurrentUser() {
		if( person != null ){
			return true;
		}
		Integer id = getPersonID();
		return id != null && id > 0;
	}
	/** Get the ID of the ccurrent person. This method can be extended to 
	 * add additional mechanisms to determine that person as it is called
	 * by both {@link #haveCurrentUser()} and {@link #getCurrentPerson()}
	 * 
	 * @return
	 */
	protected Integer getPersonID(){
		Integer id = (Integer)getAttribute(person_tag);
		if( id == null && person != null ) {
			// attribute caching disabled
			return person.getID();
		}
		return id;
	}
	

	@Override
	public void setCurrentPerson(A new_person) {
		if( new_person == null){
			clearCurrentPerson();
		}else{
			// no-op if current person is the same
			// do not call getPersonID as super-classes call this
			// method from there.
			Integer current = (Integer) getAttribute(person_tag);
			if( current == null || current.intValue() != new_person.getID() ){
				clearCurrentPerson();
				setAttribute(person_tag,new_person.getID());
				this.person=new_person;
			}	
		}
	}

	@Override
	public boolean isCurrentPerson(A person){
		if( person == null || ! haveCurrentUser()){
			return false;
		}
		return getPersonID() == person.getID();
	}
	@Override
	public void setCurrentPerson(int id) {
		if( id <= 0 ){
			clearCurrentPerson();
		}else{
			// no-op if current person is the same
			// do NOT call getPersonID as superclasses call
			// this method from the call.
			Integer current = (Integer) getAttribute(person_tag);
			if( current == null || current.intValue() != id ){
				clearCurrentPerson();
				setAttribute(person_tag,id);
			}	
		}
	}

	@Override
	public Date getAuthenticationTime() {
		return (Date) getAttribute(auth_time_tag);
	}
	
	public void setAuthenticationTime(Date d) {
		setAttribute(auth_time_tag, d);
	}
	@Override
	public String getAuthenticationType() {
		return (String) getAttribute(auth_type_tag);
	}
	
	public void setAuthenticationType(String type) {
		setAttribute(auth_type_tag, type);
	}
	@Override
	public void setCurrentRoleToggle(Map<String, Boolean> toggleMap) {
		toggle_map=toggleMap;	
	}
	/** Set a temporary (not stored to database) role.
	 * 
	 * @param role
	 */
	@Override
	public void setTempRole(String role){
		cacheRole(role);
		flushRelationships();
	}

	/** like {@link #setTempRole(String)} but does not call {@link #flushRelationships()}
	 * internal use only
	 * 
	 * @param role
	 */
	protected void cacheRole(String role) {
		if( role_map == null){
			role_map = setupRoleMap();
		}
		role_map.put(role, Boolean.TRUE);
	}

	@Override
	public String getName() {
		A user = getCurrentPerson();
		if( user != null ){
			return user.getName();
		}
		return null;
	}

	@Override
	public void cleanup() {
		if( person != null){
			person.release();
			person=null;
		}
		if( fac != null ){
			fac.release();
			fac=null;
		}
	}



	/** Perform a raw query of a users roles from the database
	 * @param conn 
	 * @param id 
	 * @param role
	 * @return boolean
	 */
	public static boolean rawRoleQuery(AppContext conn,int id,String role) {
		
		try {
			SQLContext ctx = conn.getService(DatabaseService.class).getSQLContext();
			StringBuilder role_query = new StringBuilder();
			role_query.append("SELECT * FROM ");
			ctx.quote(role_query, ROLE_TABLE);
			role_query.append(" WHERE ");
			ctx.quote(role_query,ROLE_PERSON_ID);
			role_query.append("=?   AND ");
			ctx.quote(role_query,ROLE_FIELD);
			role_query.append("=? ");
			try(PreparedStatement stmt =ctx.getConnection().prepareStatement(role_query.toString())) {
				stmt.setInt(1, id);
				stmt.setString(2, role);
				try(ResultSet rs = stmt.executeQuery()){
					if (rs.next()) {
						return true;
					}
				}
			} 
		} catch (SQLException e) {
			conn.getService(DatabaseService.class).logError("Error checking AppUser role",e);
			// maybe table missing
			// this is null if table exists
			setupRoleTable(conn);
		}
		return false;
	}

	public static Set<String> getExplicitRoles(AppContext conn,int id){
		HashSet<String> roles = new HashSet<String>();
		try {
			SQLContext ctx = conn.getService(DatabaseService.class).getSQLContext();
			StringBuilder role_query = new StringBuilder();
			role_query.append("SELECT DISTINCT ");
			ctx.quote(role_query,ROLE_FIELD);
			role_query.append(" FROM ");
			ctx.quote(role_query, ROLE_TABLE);
			role_query.append(" WHERE ");
			ctx.quote(role_query,ROLE_PERSON_ID);
			role_query.append("=? ");
			try(PreparedStatement stmt =ctx.getConnection().prepareStatement(role_query.toString())) {
				stmt.setInt(1, id);
				try(ResultSet rs = stmt.executeQuery()){
					while (rs.next()) {
						roles.add(rs.getString(1));
					}
				}
			} 
		} catch (SQLException e) {
			conn.getService(DatabaseService.class).logError("Error getting AppUser roles",e);
		}

		return roles;
	}
	
//public Set<A> withRole(String role) {
//		role = mapRoleName(role);
//		AppContext conn = getContext();
//		Set<A> result = new HashSet<A>();
//		try {
//			SQLContext ctx=conn.getService(DatabaseService.class).getSQLContext();
//			StringBuilder role_query=new StringBuilder();
//			AppUserFactory<A> fac = getLoginFactory();
//			role_query.append("SELECT ");
//			ctx.quote(role_query, ROLE_PERSON_ID).append(" FROM ");
//			ctx.quote(role_query,ROLE_TABLE).append(" WHERE ");
//			ctx.quote(role_query,ROLE_FIELD).append("=? ");
//			PreparedStatement stmt = null;
//			try {
//				stmt = ctx.getConnection().prepareStatement(role_query.toString());
//				stmt.setString(1, role);
//				ResultSet rs = stmt.executeQuery();
//				while(rs.next()) {
//					try {
//						result.add(fac.find(rs.getInt(1)));
//					} catch (DataException e) {
//						error(e,"Error getting person from role");
//					}
//				}
//			} finally {
//				if( stmt != null ){
//				   stmt.close();
//				}
//			}
//		} catch (SQLException e) {
//			error(e,"Error checking AppUser role");
//			// maybe table missing
//			// this is null if table exists
//			setupRoleTable(conn);
//		}
//		return result;
//	}


	public static  void removeRoleByID(AppContext context, int id, String role) throws DataFault {
		DatabaseService service = context.getService(DatabaseService.class);
		try {
		  
		   SQLContext ctx=service.getSQLContext();
			StringBuilder role_query=new StringBuilder();
			role_query.append("DELETE FROM ");
			ctx.quote(role_query, ROLE_TABLE).append(" WHERE ");
			ctx.quote(role_query,ROLE_PERSON_ID).append("=? AND ");
			ctx.quote(role_query,ROLE_FIELD).append("=?");
					
		
			try(PreparedStatement stmt =ctx.getConnection().prepareStatement(role_query.toString())) {
				stmt.setInt(1, id);
				stmt.setString(2, role);
				stmt.executeUpdate();
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(context)){
					Logger log = context.getService(LoggerService.class).getLogger(context.getClass());
					log.debug(role_query+" id="+id+" role="+role);
				}
			} 
		} catch (SQLException e) {
			service.handleError("SQLException ", e);
		}
	}



	public static void addRoleByID(AppContext c, int id, String role) throws DataFault {
		DatabaseService service = c.getService(DatabaseService.class);
		try {
			
			SQLContext ctx=service.getSQLContext();
			StringBuilder role_query=new StringBuilder();
			role_query.append( "INSERT INTO ");
			ctx.quote(role_query,ROLE_TABLE).append(" (");
			ctx.quote(role_query, ROLE_PERSON_ID).append(",");
			ctx.quote(role_query, ROLE_FIELD).append(") VALUES(?,?)");;
					
			try(PreparedStatement stmt= ctx.getConnection().prepareStatement(role_query.toString())) {
				stmt.setInt(1, id);
				stmt.setString(2, role);
				stmt.executeUpdate();
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(c)){
					Logger log = c.getService(LoggerService.class).getLogger(c.getClass());
					log.debug(role_query+" id="+id+" role="+role);
				}
			} 
		} catch (SQLException e) {
			service.handleError("SQLException ", e);
		}
	}


	@Override
	public void setRole(A user, String role, boolean value)
			throws UnsupportedOperationException {
		boolean current = canHaveRole(null,user,role,false);
		if( value == current){
			return;
		}
		if( value ){
			try {
				addRoleByID(getContext(), user.getID(), role);
			} catch (DataFault e) {
				error(e,"Error setting role");
			}
		}else{
			try {
				removeRoleByID(getContext(), user.getID(), role);
			} catch (DataFault e) {
				error(e,"Error removing role");
			}
		}
		// in case this is us clear the cache.
		clearRoleMap();  
		
	}


	private <X extends DataObject> boolean checkRelationshipRole(A user,String tag, String relationship, String name_filter) {
		try {
			DataObjectFactory<X> fac = getContext().makeObject(DataObjectFactory.class, tag);
			if( fac == null) {
				error("tag "+tag+" failed to resolve to DataObjectFactory");
				return false;
			}
			AndFilter<X> fil = new AndFilter<>(fac.getTarget());
			fil.addFilter(getTargetInRelationshipRoleFilter(fac, relationship, user));
			if( name_filter != null ) {
				BaseFilter<? super X> nf = makeNamedFilter(fac, name_filter);
				if( nf == null ) {
					error("Name filter "+name_filter+" failed to resolve on "+tag);
					return false;
				}
				fil.addFilter(nf);
			}
			// Does a target object exist which matches the named filter and
			// the designated relationship
			return fac.exists(fil);
		}catch(Exception t) {
			error(t,"Error checking relationship based role");
			return false;
		}
	}
	private <X extends DataObject> BaseFilter<A> getRelationshipRoleFilter(String tag, String relationship, String name_filter) {
		AppUserFactory<A> login = getLoginFactory();
		try {
			
			DataObjectFactory<X> fac = getContext().makeObject(DataObjectFactory.class, tag);
			if( fac == null) {
				error("tag "+tag+" failed to resolve to DataObjectFactory");
				return new FalseFilter<A>(login.getTarget());
			}
			if( name_filter == null) {
				// Any target
				return getPersonInRelationshipRoleFilter(fac, relationship, null);
			}
			BaseFilter<X> nf = makeNamedFilter(fac, name_filter);
			if( nf == null ) {
				error("Bad name filter on "+fac.getTag()+" "+name_filter);
				return new FalseFilter(login.getTarget());
			}
			OrFilter<A> fil = new OrFilter<>(login.getTarget(),login);
			for(X o : fac.getResult(nf)) {
				fil.addFilter(getPersonInRelationshipRoleFilter(fac, relationship, o));
			}
			return fil;
		}catch(Exception t) {
			error(t,"Error checking relationship based role");
			return new FalseFilter<A>(login.getTarget());
		}
	}
	/** look for a named filter from the factory or composites.
	 * 
	 */
	protected <T extends DataObject> BaseFilter<T> makeNamedFilter(DataObjectFactory<T> fac2, String name){
		NamedFilterWrapper<T> wrapper = new NamedFilterWrapper<>(fac2);
		return wrapper.getNamedFilter(name);
	}
	/** get a {@link BaseFilter} for all {@link AppUser}s who
	 * have access to a global role.
	 * 
	 * This is the same selection as {@link #canHaveRole(AppUser, String)
	 * 
	 * @param role
	 * @return
	 */
	@Override
	public final BaseFilter<A> getGlobalRoleFilter(String ...roles){
		AppUserFactory<A> loginFactory = getLoginFactory();
		if( roles == null || roles.length == 0) {
			return new FalseFilter<A>(loginFactory.getTarget());
		}else if( roles.length == 1) {
			return getGlobalRoleFilter(null, roles[0]);
		}else {
			OrFilter<A> or = new OrFilter<A>(loginFactory.getTarget(), loginFactory);
			for(String role : roles) {
				or.addFilter(getGlobalRoleFilter(null, role));
			}
			return or;
		}
	}
	public final BaseFilter<A> getGlobalRoleFilter(Set<String> skip,String role){
		AppUserFactory<A> login = getLoginFactory();
		if( role == null || role.isEmpty() || role.equalsIgnoreCase("false")) {
			return new FalseFilter<A>(login.getTarget());
		}
		if( role.equalsIgnoreCase("true")) {
			return new GenericBinaryFilter<A>(login.getTarget(), true);
		}
		if(role.startsWith("@")) {
			BaseFilter<A> nf = makeNamedFilter(login, role.substring(1));
			if( nf == null ) {
				error("Bad named filter on AppUser "+role);
				return new FalseFilter(login.getTarget());
			}
			return nf;
		}
		int pos = role.indexOf('%');
		if( pos > 0) {
			// role based on relationship and optional nameFilter
		    String tag = role.substring(0, pos);
		    String rel = role.substring(pos+1);
		    String name=null;
		    int namepos = rel.indexOf('@');
		    if( namepos > 0) {
		    	name=rel.substring(namepos+1);
		    	rel=rel.substring(0, namepos);
		    }
		    // can't alias a complex role to return directly
		    return getRelationshipRoleFilter(tag, rel, name);
		}
		BaseFilter<A> fil = getPersonInRoleFilter(role);
		if( skip == null ) {
			skip = new HashSet<String>();
		}
		skip.add(role);
		String list = mapRoleName(role);
		if( ! list.equals(role)) {
			OrFilter<A> or = new OrFilter<A>(login.getTarget(), login);
			or.addFilter(fil);
			for(String r : list.split("\\s*,\\s*")) {
				if( r.contains("+")) {
					AndFilter<A> and = new AndFilter<A>(login.getTarget());
					for(String r2 : r.split("\\s*+\\s*")) {
						if( skip.contains(r2)) {
							and.addFilter(new FalseFilter<A>(login.getTarget()));
						}else {
							and.addFilter(getGlobalRoleFilter(skip, r2));
						}
					}
					or.addFilter(and);
				}else if( ! skip.contains(r)) {
					or.addFilter(getGlobalRoleFilter(skip, r));
				}
			}
			fil = or;
		}
		return fil;
	}
	
	@Override
	public boolean canHaveRole(A user, String role) {
		return canHaveRole(null,user,role,true);
	}
	protected boolean canHaveRole(Set<String> skip, A user, String role,boolean expand_alias) {	
		if( user == null || role == null || role.isEmpty()){
			return false;
		}
		if( role.equalsIgnoreCase("true")) {
			return true;
		}
		if( role.equalsIgnoreCase("false")) {
			return false;
		}
		if( role.startsWith("@")) {
			AppUserFactory<A> login = getLoginFactory();
			if( role.length() < 2) {
				return false;
			}
			BaseFilter<A> nf = makeNamedFilter(login, role.substring(1));
			if( nf == null) {
				error("Invalid named filter on AppUser "+role);
				return false;
			}
			return login.matches(nf, user);
		}
		int pos = role.indexOf('%');
		if( pos > 0) {
			// role based on relationship and optional nameFilter
		    String tag = role.substring(0, pos);
		    String rel = role.substring(pos+1);
		    String name=null;
		    int namepos = rel.indexOf('@');
		    if( namepos > 0) {
		    	name=rel.substring(namepos+1);
		    	rel=rel.substring(0, namepos);
		    }
		    return checkRelationshipRole(user ,tag, rel, name);
		}
		if( rawRoleQuery(getContext(), user.getID(), role)){
			return true;
		}
		AppUserFactory<A> login_fac = getLoginFactory();
		if( login_fac instanceof StateRoleProvider){
			if(((StateRoleProvider<A>)login_fac).testRole(user, role)){
				return true;
			}
		}
		for(StateRoleProvider<A> comp : login_fac.getComposites(StateRoleProvider.class)){
			if( comp.testRole(user, role)){
				return true;
			}
		}
		if( expand_alias) {
			if( skip == null ) {
				skip = new HashSet<String>();
			}
			skip.add(role);
			String list = mapRoleName(role);
			if( ! list.equals(role)) {
				for(String r : list.split("\\s*,\\s*")) {
					if( r.contains("+")) {
						boolean ok = true;
						for(String r2 : r.split("\\s*+\\s*")) {
							if( skip.contains(r2) || ! canHaveRole(skip, user, r2, expand_alias)) {
								ok=false;
								break;
							}
						}
						return ok;
					}else if( ! skip.contains(r)) {
						if(canHaveRole(skip, user, r,expand_alias)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		if( haveCurrentUser()){
			if( person != null ){
				sb.append(person.getIdentifier());
			}else{
				sb.append(getPersonID());
			}
		}
		sb.append("]");
		return sb.toString();
	}



	@Override
	public Class<SessionService> getType() {
		return SessionService.class;
	}


	/** Get the Locale to use in the current context
	 * 
	 * @return Locale
	 */
	@Override
	public Locale getLocale() {
		return Locale.UK;
	}
	@Override
	public TimeZone getTimeZone(){
		return TimeZone.getDefault();
	}

	
	
	
	@Override
	public final <T extends DataObject> BaseFilter<T> getRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role) throws UnknownRelationshipException {
		// We may be storing roles from different types so prefix all tags with the type.
		// prefix is never seen outsite this cache.
		String store_tag=fac.getTag()+"."+role;
		BaseFilter<T> result = roles.get(store_tag);
		if( result == null ){
			result = makeRelationshipRoleFilter(fac,role,null);
			if( result == null) {
				throw new UnknownRelationshipException(role);
			}
			roles.put(store_tag, result);
		}
		return result;
	}
	@Override
	public final <T extends DataObject> BaseFilter<T> getRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role,BaseFilter<T> fallback) {
		
		BaseFilter<T> result;
		try {
			result = makeRelationshipRoleFilter(fac,role,null);
			if( result == null ) {
				return fallback;
			}
			// narrow the selection
			result = new AndFilter<>(fac.getTarget(),result,fallback);
		} catch (UnknownRelationshipException e) {
			Throwable t = e.getCause();
			if( t != null ) {
				// Problem in a nested definition
				getLogger().error("Error in nested relationship defn of "+role+" on "+fac.getTag(), t);
			}
			// should never be thrown with a default specified.
			return fallback;
		}
		
		return result;
	}
	private <T extends DataObject> BaseFilter<T> makeRelationshipRoleFilterForPerson(DataObjectFactory<T> fac2, String role, A person,
			BaseFilter<T> fallback) throws UnknownRelationshipException {
		
		PermissionClause<T> ast = perm_parser.parse(fac2, role);
		if( ast == null) {
			if( fallback != null) {
				return fallback;
			}
			throw new UnknownRelationshipException(role);
		}
		BaseFilter<T> fil = ast.accept(new RelationshipRoleFilterPermissionVisitor<A, T>(this, fac2, person));
		if( fil == null) {
			return fallback;
		}
		return fil;
	}
	private <T extends DataObject> BaseFilter<T> makeRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,
			BaseFilter<T> fallback) throws UnknownRelationshipException {
		
		PermissionClause<T> ast = perm_parser.parse(fac2, role);
		if( ast == null) {
			if( fallback != null) {
				return fallback;
			}
			throw new UnknownRelationshipException(role);
		}
		BaseFilter<T> fil = ast.accept(new SessionRelationshipRoleFilterPermissionVisitor<A, T>(this, fac2));
		if( fil == null) {
			return fallback;
		}
		return fil;
	}

	@Override
	public final <T extends DataObject> BaseFilter<A> getPersonInRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role, T target) throws UnknownRelationshipException {
		// We may be storing roles from different types so prefix all tags with the type.
		// prefix is never seen outside this cache.
		// This filter is also parameterised by the target
		String store_tag="PersonFilter."+fac.getTag()+(target == null ? "" : "."+target.getID())+"."+role;
		BaseFilter<A> result = roles.get(store_tag);
		if( result == null ){
			
			result = makePersonInRelationshipRoleFilter(fac, role,target);
			if(APPLY_DEFAULT_PERSON_RELATIONSHIP_FILTER.isEnabled(getContext())){
				// Add in the default relationship filte on person
				SQLFilter<A> fil = getLoginFactory().getDefaultRelationshipFilter();
				if( fil != null ){
					result = new AndFilter<>(getLoginFactory().getTarget(),result,fil);
				}
			}
			if( result == null ) {
				throw new UnknownRelationshipException(role);
			}
			roles.put(store_tag, result);
		}
		return result;
	}
	private <T extends DataObject> BaseFilter<A> makePersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role, T target) throws UnknownRelationshipException {
		
		PermissionClause<T> ast = perm_parser.parse(fac2, role);
		if( ast == null ) {
			throw new UnknownRelationshipException(role);
		}
		BaseFilter<A> fil = ast.accept(new PersonInRelationshipRoleFilterPermissionVisitor<A, T>(this, fac2, target));
		if( fil == null) {
			throw new UnknownRelationshipException(role);
		}
		return fil;
	}

	@Override
	public <T extends DataObject> Object explainRelationship(DataObjectFactory<T> fac2, String role) {
		try {
			return perm_parser.parse(fac2, role).accept(new ExplainPermissionVisitor<T>());
		} catch (UnknownRelationshipException e) {
			return "Error(Unknown relationship "+e.getMessage()+")";
		}
	}

	@Override
	public <T extends DataObject> BaseFilter<T> getTargetInRelationshipRoleFilter(DataObjectFactory<T> fac, String role,
			A person) throws UnknownRelationshipException {
		if( person == null){
			return getRelationshipRoleFilter(fac, role);
		}
		String store_tag="TargetFilter."+fac.getTag()+"."+person.getID()+"."+role;
		BaseFilter<T> result = roles.get(store_tag);
		if( result == null ){
			result = makeRelationshipRoleFilterForPerson(fac,role,person,null);
			if( result == null) {
				throw new UnknownRelationshipException(role);
			}
			if(APPLY_DEFAULT_TARGET_RELATIONSHIP_FILTER.isEnabled(getContext())){
				// Add in the default relationship filter on target
				//
				// TODO This breaks everything for some reasons
				//
				SQLFilter<T> fil = fac.getDefaultRelationshipFilter();
				if( fil != null ){
					result = new AndFilter<>(fac.getTarget(),result,fil);
				}
			}
			roles.put(store_tag, result);
		}
		return result;
	}

	@Override
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target, String role) throws UnknownRelationshipException {
		// We could interpret null target as hasRelationship with any target
		// and cache result using an id of 0.
		// but at the moment do 
		// fac.exists(this.getRelationshipRoleFilter(fac,role)
		// explicitly
		if( target == null || role == null) {
			return false;
		}
		// For the moment we only cache relationships within a request
		// to avoid stale values as a users state changes
		if( relationship_map == null && CACHE_RELATIONSHIP_FEATURE.isEnabled(getContext())){
			relationship_map = new HashMap<>();
		}
		RelationshipTag tag = new RelationshipTag(fac.getTag(), target.getID(), role);
		if( relationship_map != null && relationship_map.containsKey(tag)){
			return relationship_map.get(tag).booleanValue();
		}
		try(TimeClosable t = new TimeClosable(getContext(),() -> "hasRelationship."+tag.toString()) ){
			boolean result = fac.matches(getRelationshipRoleFilter(fac, role), target);
			if( relationship_map != null ){
				relationship_map.put(tag, result);
			}

			return result;
		}
	}	
	@Override
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target, String role, Supplier<Boolean> fallback) {
		try(TimeClosable time = new TimeClosable(conn,() -> "hasRelationship("+fac.getTag()+","+role+")")) {
			return hasRelationship(fac, target, role);
		}catch(UnknownRelationshipException e) {
			return fallback.get();
		}
	}

	/**
	 * Report an application error.
	 * Needs to handle the possiblity of the LoggerService not being present as
	 * we can't make it a pre-requisite here
	 * 
	 * @param errors
	 *            Text of error.
	 */
	
	final void error(String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors);
			}
		}
	}
	final void error(Throwable t,String errors) {
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null ){
			Logger log = serv.getLogger(getClass());
			if( log != null ){
				log.error(errors,t);
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.SessionService#setApplyToggle(boolean)
	 */
	@Override
	public void setApplyToggle(boolean value) {
		apply_toggle=value;
	}
	public boolean getApplyToggle() {
		return apply_toggle;
	}

	/** Extension point for sub-classes used to implement  {@link #getGlobalRoleFilter(String)}
	 * 
	 * @param role_list
	 * @return
	 */
	protected BaseFilter<A> getPersonInRoleFilter(String... role_list) {
		return getLoginFactory().getRoleFilter(role_list);
	}

	@Override
	public void addSecurityContext(Map att) {
		// Use the same tags as the session
		Integer id = getPersonID();
		if( id != null && id.intValue() > 0) {
			att.put(person_tag, id);
			A p = getCurrentPerson();
			if( p != null ) {
				att.put("user",p.getIdentifier());
			}
		}
		String type = getAuthenticationType();
		if( type != null ) {
			att.put(auth_type_tag, type);
		}
		Date d = getAuthenticationTime();
		if( d != null ) {
			att.put(auth_time_tag,d);
		}
		
	}
}