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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.exception.ForceRollBack;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.NegatingFilterVisitor;
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
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
import uk.ac.ed.epcc.webapp.model.data.RemoteAccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.GlobalRoleFilter;
import uk.ac.ed.epcc.webapp.model.relationship.RelationshipProvider;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;
/** Abstract base implementation of {@link SessionService}
 * <p>
 * A config parameter of the form <b>use_role.<i>role-name</i></b> defines a role-name mapping
 * the value of the parameter is the actual role queried. A comma separated list of sufficient roles 
 * may also be specified.
 * <p>
 * A role of the form <b><i>tag</i>%<i>rel[@name]</i></b> is possessed by a user if that user
 * has relationship (see below) <i>rel</i> against one of the records from factory constructed using <i>tag</i>.
 * If the optional name-filter  <i>name</i> is specified it must be one of the records that match that filter.
 * <p>
 * The {@link AppUserFactory} or its {@link Composite}s can provide roles by implementing
 * {@link StateRoleProvider}. 
 * <p>
 * Relationships are configured via the {@link ConfigService} by setting:
 * <b>use_relationship.<em>factory-tag</em>.<em>relationship</em></b>
 * If this is a comma separated list it implies an OR of the component parts.
 * within this AND combinations can be specified as + separated terms.
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
 * <li> <em>factory-tag</em> un-modified relationship from factory or a named filter from
 * a {@link NamedFilterWrapper} wrapping the factory. Named filters resolve true/false depending
 * on whether any targets exist that match the filter.
 * <li> The tag of a {@link RelationshipProvider} for the target.</li>
 * <li> The tag of a {@link AccessRoleProvider}</li>
 * </ul> 
 * 
 * @author spb
 * @see NamedFilterWrapper
 * @see RemoteAccessRoleProvider
 * @param <A>
 */
@PreRequisiteService(ConfigService.class)
public abstract class AbstractSessionService<A extends AppUser> extends AbstractContexed implements SessionService<A>{
	/**
	 * 
	 */
	private static final String RELATIONSHIP_DEREF = "->";
	/** string that separates OR combination of relationship defns
	 * 
	 */
	private static final String OR_RELATIONSHIP_COMBINER = ",";
	/** string that separates AND combinations of relationship defns
	 * 
	 */
	private static final String AND_RELATIONSHIP_COMBINER = "+";
	/** prefix for relationship definitions that map to a global role
	 * 
	 */
	private static final String GLOBAL_ROLE_RELATIONSHIP_BASE = "global";
	/** prefix for relationship definitions that map to a boolean filter
	 * 
	 */
	private static final String BOOLEAN_RELATIONSHIP_BASE = "boolean";
	/** property prefix for relationship defns
	 * 
	 */
	private static final String USE_RELATIONSHIP_PREFIX = "use_relationship.";
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
	
	private static final String person_tag = "SESSION_PersonID";
	private static final String toggle_map_tag = "SESSION_toggle_map";
	private static final String role_map_tag = "SESSION_role_map";
	
	private static final String auth_time_tag = "SESSION_auth_time";
	private boolean apply_toggle=true;
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
	private void flushRelationships(){
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
				setAttribute(toggle_map_tag, toggle_map);
			}
		}
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
		// all caching
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
		return getContext().getInitParameter(USE_ROLE_PREFIX+name, name);
	}
	
	
	@Override
	public boolean hasRoleFromList(String ...roles){
		if( roles == null ){
			return false;
		}
		for(String role : roles){
			if( hasRole(role)){
				return true;
			}
		}
		return false;
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
	 * No name mapping is applied.
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
				role_map.put(role, answer);
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
	 * No role mapping is applied
	 * 
	 * @param role
	 * @return
	 */
	protected  Boolean testRole(String role){
	    return canHaveRole(getCurrentPerson(), role);
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
		boolean current = canHaveRole(user,role);
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
	@Override
	public boolean canHaveRole(A user, String role) {
		if( user == null || role == null){
			return false;
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
			result = makeRelationshipRoleFilter(fac,role,null,null);
			roles.put(store_tag, result);
		}
		return result;
	}
	@Override
	public final <T extends DataObject> BaseFilter<T> getRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role,BaseFilter<T> fallback) {
		
		BaseFilter<T> result;
		try {
			result = makeRelationshipRoleFilter(fac,role,null,fallback);
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
			roles.put(store_tag, result);
		}
		return result;
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
			result = makeRelationshipRoleFilter(fac,role,person,null);
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

	private Set<String> searching_roles = new LinkedHashSet<>();
	/** actually construct the filter.
	 * 
	 * A role can be mapped to a different implementation by setting:
	 * <b>use_relationship.<em>factory-tag</em>.<em>role</em></b>
	 * If this is a comma separated list it implies an OR of the component parts.
	 * within this AND combinations can be specified as + separated terms.
	 * 
	 * The factory (or its {@link Composite}s) can implement {@link AccessRoleProvider} to provide roles.
	 * 
	 * Roles of the form <i>field</i><b>-></b><i>remote_role</i> denotes a remote filter
	 * joined via the reference field <i>field</i> A person has these roles with the targer object
	 * if they have the <i>remote_role</i> on the object the target references. The remote role must be unqualified.
	 * 
	 * Role names containing a period are qualified names the qualifier can be:
	 * <ul>
	 * <li> <b>global</b> the role is a global role not a relationship.</li>
	 * <li> <b>boolean</b> Use a boolean filter so all/none relationships match.</li>
	 * <li> <em>factory-tag</em> un-modified role from factory or {@link Composite}.</li>
	 * <li> The tag of a {@link RelationshipProvider} for the target.</li>
	 * <li> The tag of a {@link AccessRoleProvider}</li>
	 * </ul>
	 * 
	 * A role prefixed by <b>!</b> negates the filter
	 * 
	 * @param fac2   target factory
	 * @param role   relationship string
	 * @param person person to query (null for current person)
	 * @param def default query to use if no definition (pass null to throw exception)
	 * @return
	 * @throws UnknownRelationshipException 
	 */
	protected <T extends DataObject> BaseFilter<T> makeRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person,BaseFilter<T> def) throws UnknownRelationshipException {
		String search_tag = fac2.getTag()+":"+role;
		if( searching_roles.contains(search_tag)){
			// recursive creation
			throw new UnknownRelationshipException("recursive definition of "+search_tag+" via "+searching_roles.toString());
		}
		searching_roles.add(search_tag);
		try{
			if( role == null || role.trim().isEmpty()) {
				throw new UnknownRelationshipException("empty role requested");
			}
			if( role.contains(OR_RELATIONSHIP_COMBINER)){
				// OR combination of filters
				OrFilter<T> or = new OrFilter<>(fac2.getTarget(), fac2);
				for( String  s  : role.split(OR_RELATIONSHIP_COMBINER)){
					try{
						if( person == null){
							or.addFilter(getRelationshipRoleFilter(fac2, s));
						}else{
							or.addFilter(getTargetInRelationshipRoleFilter(fac2, s, person));
						}
					}catch(UnknownRelationshipException e){
						if(ALLOW_UNKNOWN_RELATIONSHIP_IN_OR_FEATURE.isEnabled(getContext())){
							error(e, "Bad relationship in OR branch");
						}else{
							throw e;
						}
					}
				}
				return or;
			}
			if( role.contains(AND_RELATIONSHIP_COMBINER)){
				// AND combination of filters
				AndFilter<T> and = new AndFilter<>(fac2.getTarget());
				for( String  s  : role.split("\\+")){
					if( person == null ){
						and.addFilter(getRelationshipRoleFilter(fac2, s));
					}else{
						and.addFilter(getTargetInRelationshipRoleFilter(fac2, s, person));
					}
				}
				return and;
			}
			// should be a single filter now.
			if( role.startsWith("!")) {
				BaseFilter<T> fil = makeRelationshipRoleFilter(fac2, role.substring(1), person, def);
				NegatingFilterVisitor<T> nv = new NegatingFilterVisitor<>(fac2);
				try {
					return fil.acceptVisitor(nv);
				}catch(UnknownRelationshipException e) {
					throw e;
				} catch (Exception e) {
					error(e, "Error negating filter");
					throw new UnknownRelationshipException(role);
				}
			}else if( role.contains(RELATIONSHIP_DEREF)){
				// This is a remote relationship
				// Note this will also catch remote NamedRoles
				// Match this first as the remote relationship
				// might be qualified but the field name never is
				int pos = role.indexOf(RELATIONSHIP_DEREF);
				String link_field = role.substring(0, pos);
				String remote_role = role.substring(pos+RELATIONSHIP_DEREF.length());
				RemoteAccessRoleProvider<A, T, ?> rarp = new RemoteAccessRoleProvider<>(this, fac2, link_field);
				BaseFilter<T> fil = rarp.hasRelationFilter(remote_role, person);
				if( fil == null ){
					throw new UnknownRelationshipException(role);
				}
				return fil;
			}else if( role.contains(".")){
				// qualified role
				int pos = role.indexOf('.');
				String base =role.substring(0, pos);
				String sub = role.substring(pos+1);
				if( base.equals(GLOBAL_ROLE_RELATIONSHIP_BASE)){
					if( person == null){
						// Only the global role filter can allow relationship without a current person
						// as roles may be asserted from the container.
						return new GlobalRoleFilter<>(this, sub);
					}else{

						return new GenericBinaryFilter<>(fac2.getTarget(),canHaveRole(person, role));
					}
				}
				if( base.equals(BOOLEAN_RELATIONSHIP_BASE)){

					return new GenericBinaryFilter<>(fac2.getTarget(),Boolean.valueOf(sub));

				}
				if( person == null && ! haveCurrentUser()){
					// No users specified
					return new GenericBinaryFilter<>(fac2.getTarget(),false);
				}

				if( base.equals(fac2.getTag())){
					// This is a reference a factory/composite role from within a redefined
					// definition. direct roles can be qualified if we want qualified names cannot
					// be overridden. 
					BaseFilter<T> result = makeDirectRelationshipRoleFilter(fac2, sub,person,null);
					if( result != null ){
						return result;
					}
					// unrecognised direct role alias maybe
					if( person == null){
						return getRelationshipRoleFilter(fac2, sub);
					}else{
						return getTargetInRelationshipRoleFilter(fac2, sub, person);
					}
				}
				AccessRoleProvider<A,T> arp = getContext().makeObjectWithDefault(AccessRoleProvider.class,null,base);
				if( arp != null ){
					if( person == null){
						person=getCurrentPerson();
					}
					return arp.hasRelationFilter(sub,person);
				}
				NamedFilterProvider<T> nfp = getContext().makeObjectWithDefault(NamedFilterProvider.class, null, base);
				if( nfp != null ) {
					return nfp.getNamedFilter(sub);
				}
			}else{
				// Non qualified name

				// direct roles can be un-qualified though not if we want multiple levels of qualification.
				BaseFilter<T> result = makeDirectRelationshipRoleFilter(fac2, role,person,null);
				if( result != null ){
					return result;
				}
			}
			if( def == null){
				throw new UnknownRelationshipException(role);
			}
			return def;
		}catch(UnknownRelationshipException ur){
			if( ur.getMessage().equals(role)){
				throw ur;
			}else{
				throw new UnknownRelationshipException(role, ur);
			}
		}finally{
			searching_roles.remove(search_tag);
		}
	}
	protected <T extends DataObject> BaseFilter<A> makePersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,T target) throws UnknownRelationshipException {
		AppUserFactory<A> login_fac = getLoginFactory();
		Class<A> target_type = login_fac.getTarget();
		String search_tag = fac2.getTag()+":"+role;
		if( searching_roles.contains(search_tag)){
			// recursive creation
			throw new UnknownRelationshipException("recursive definition of "+role);
		}
		searching_roles.add(search_tag);
		try{
		if( role.contains(OR_RELATIONSHIP_COMBINER)){
			// OR combination of filters
			OrFilter<A> or = new OrFilter<>(target_type, login_fac);
			for( String  s  : role.split(",")){
				try{
					or.addFilter(getPersonInRelationshipRoleFilter(fac2, s,target));
				}catch(UnknownRelationshipException e){
					if(ALLOW_UNKNOWN_RELATIONSHIP_IN_OR_FEATURE.isEnabled(getContext())){
						error(e, "Bad relationship in OR branch");
					}else{
						throw e;
					}
				}
			}
			return or;
		}
		if( role.contains(AND_RELATIONSHIP_COMBINER)){
			// OR combination of filters
			AndFilter<A> and = new AndFilter<>(target_type);
			for( String  s  : role.split("\\+")){
				and.addFilter(getPersonInRelationshipRoleFilter(fac2, s,target));
			}
			return and;
		}
		// should be a single filter now.
		if( role.startsWith("!")) {
			BaseFilter<A> fil = makePersonInRelationshipRoleFilter(fac2, role.substring(1), target);
			NegatingFilterVisitor<A> nv = new NegatingFilterVisitor<>(login_fac);
			try {
				return fil.acceptVisitor(nv);
			}catch( UnknownRelationshipException e) {
				throw e;
			} catch (Exception e) {
				error(e,"Error negating filter");
				throw new UnknownRelationshipException(role);
			}
		}else if( role.contains(RELATIONSHIP_DEREF)){
	    	// This is a remote relationship
	    	// Note this will also catch remote NamedRoles
	    	int pos = role.indexOf(RELATIONSHIP_DEREF);
	    	String link_field = role.substring(0, pos);
	    	String remote_role = role.substring(pos+RELATIONSHIP_DEREF.length());
	    	RemoteAccessRoleProvider<A, T, ?> rarp = new RemoteAccessRoleProvider<>(this, fac2, link_field);
	    	BaseFilter<A> fil = rarp.personInRelationFilter(this, remote_role, target);
	    	if( fil == null ){
	    		throw new UnknownRelationshipException(role);
	    	}
			return fil;
		}else if( role.contains(".")){
	    	// qualified role
	    	int pos = role.indexOf('.');
	    	String base =role.substring(0, pos);
	    	String sub = role.substring(pos+1);
	    	if( base.equals(GLOBAL_ROLE_RELATIONSHIP_BASE)){
	    		// roles don't enumerate
	    		return new GenericBinaryFilter<>(target_type,false);
	    	}
	    	if( base.equals(BOOLEAN_RELATIONSHIP_BASE)){
	    		
	    		return new GenericBinaryFilter<>(target_type,Boolean.valueOf(sub));
	    		
	    	}
	    	if( base.equals(fac2.getTag())){
	    		// This is to reference a factory/composite role from within a redefined
	    		// definition. direct roles can be qualified if we want qualified names cannot
	    		// be overridden. 
	    		BaseFilter<A> result = makeDirectPersonInRelationshipRoleFilter(fac2, sub,target);
	    		if( result != null ){
	    			return result;
	    		}
	    		// unrecognised role
	    		throw new UnknownRelationshipException(role);
	    	}
	    	AccessRoleProvider<A,T> arp = getContext().makeObjectWithDefault(AccessRoleProvider.class,null,base);
	    	if( arp != null ){
	    		return arp.personInRelationFilter(this, sub, target);
	    	}
	    	NamedFilterProvider<T> nfp = getContext().makeObjectWithDefault(NamedFilterProvider.class, null, base);
	    	if( nfp != null ) {
	    		BaseFilter<T> fil = nfp.getNamedFilter(sub);
	    		if( fil != null) {
	    			return new GenericBinaryFilter<>(target_type, fac2.matches(fil, target));
	    		}
	    	}
	    }else{
	    	// direct roles can be un-qualified though not if we want multiple levels of qualification.
	    	BaseFilter<A> result = makeDirectPersonInRelationshipRoleFilter(fac2, role,target);
			if( result != null ){
				return result;
			}
	    }
		
		throw new UnknownRelationshipException(role);
		}catch(UnknownRelationshipException ur){
			if( ur.getMessage().equals(role)){
				throw ur;
			}else{
				throw new UnknownRelationshipException(role, ur);
			}
		}finally{
			searching_roles.remove(search_tag);
		}
	}
	/** Make filter for objects in relation to a person for directly implemented roles
	 * @param fac2
	 * @param role
	 * @throws UnknownRelationshipException 
	 */
	protected <T extends DataObject> BaseFilter<T> makeDirectRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person,BaseFilter<T> def) throws UnknownRelationshipException {
		if( role == null || role.trim().isEmpty()) {
			throw new UnknownRelationshipException("empty role requested");
		}
		// look for directly implemented relations first
		BaseFilter<T> result=null;
		
	    if( person != null || haveCurrentUser()){

	    	if( fac2 instanceof AccessRoleProvider){  // first check factory itself.
	    		result = ((AccessRoleProvider<A,T>)fac2).hasRelationFilter(role,person == null ? getCurrentPerson():person);
	    		if( result != null){
	    			return result;
	    		}
	    	}
	    	// then check composites
	    	for(AccessRoleProvider prov : fac2.getComposites(AccessRoleProvider.class)){
	    		result = prov.hasRelationFilter( role,person==null?getCurrentPerson():person);
	    		if( result != null){
	    			return result;
	    		}
	    	}
	    }else{
	    	// Can't test directly without a person as null person will select all targets
	    	if( fac2 instanceof AccessRoleProvider){  // first check factory itself.
	    		if(((AccessRoleProvider<A,T>)fac2).providesRelationship(role)){
	    			if( def != null ){
	    				return def;
	    			}
	    			return new FalseFilter<>(fac2.getTarget());
	    		}
	    	}
	    	// then check composites
	    	for(AccessRoleProvider prov : fac2.getComposites(AccessRoleProvider.class)){
	    		if( prov.providesRelationship(role)){
	    			if( def != null ){
	    				return def;
	    			}
	    			return new FalseFilter<>(fac2.getTarget());
	    		}
	    	}
	    }
	    
	    // Its not one of the directly implemented roles maybe its derived
	    String name = USE_RELATIONSHIP_PREFIX+fac2.getTag()+"."+role;
		String defn = getContext().getInitParameter(name);
	    if( defn != null){
	   
	    	// don't pass def as it is the sub-defn that is not resolving
	    	return makeRelationshipRoleFilter(fac2, defn,person,null);
	    }
	    result = makeNamedFilter(fac2, role);
	    if( result != null){
	    	return result;
	    }
	    if( def != null){
	    	return def;
	    }
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
	}
	/** look for a named filter from the factory or composites.
	 * 
	 */
	protected <T extends DataObject> BaseFilter<T> makeNamedFilter(DataObjectFactory<T> fac2, String name){
		NamedFilterWrapper<T> wrapper = new NamedFilterWrapper<>(fac2);
		return wrapper.getNamedFilter(name);
	}
	protected <T extends DataObject> BaseFilter<A> makeDirectPersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,T target) throws UnknownRelationshipException {
		BaseFilter<A> result=null;
	    if( fac2 instanceof AccessRoleProvider){  // first check factory itself.
	    	result = ((AccessRoleProvider<A,T>)fac2).personInRelationFilter(this, role, target);
	    	if( result != null){
	    		return result;
	    	}
	    }
	    // then check composites
	    for(AccessRoleProvider prov : fac2.getComposites(AccessRoleProvider.class)){
	    	result = prov.personInRelationFilter(this, role, target);
	    	if( result != null){
	    		return result;
	    	}
	    }
	    
	    
	 // Its not one of the directly implemented roles maybe its derived
	    String defn = getContext().getInitParameter(USE_RELATIONSHIP_PREFIX+fac2.getTag()+"."+role);
	    if( defn != null){
	    	return makePersonInRelationshipRoleFilter(fac2, defn,target);
	    }
	    
	 // Maybe its a named filter this will translate to a binary filter on the app-user
	    BaseFilter<T> fil = makeNamedFilter(fac2, role);
	    if( fil != null){
	    	boolean matches=false;
	    	if( target == null ){
	    		try {
					matches = fac2.exists((BaseFilter<T>) fil);
				} catch (DataException e) {
					error(e, "Error checking for null target");
				}
	    	}else{
	    		matches= fac2.matches(fil, target);
	    	}
	    	return new GenericBinaryFilter<>((Class<A>) AppUser.class, matches);
	    }
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
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
		try(TimeClosable t = new TimeClosable(getContext(),"hasRelationship."+tag.toString()) ){
			boolean result = fac.matches(getRelationshipRoleFilter(fac, role), target);
			if( relationship_map != null ){
				relationship_map.put(tag, result);
			}

			return result;
		}
	}
	@Override
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target, String role, boolean fallback) {
		try {
			return hasRelationship(fac, target, role);
		}catch(UnknownRelationshipException e) {
			return fallback;
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
	
}