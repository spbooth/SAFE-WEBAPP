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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
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
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.GlobalRoleFilter;
import uk.ac.ed.epcc.webapp.model.relationship.RelationshipProvider;
/** Abstract base implementation of {@link SessionService}
 * 
 * A config parameter of the form <b>use_role.<i>role-name</i></b> defines a role-name mapping
 * the value of the parameter is the actual role queried. 
 * 
 * 
 * 
 * Relationships are configured via the {@link ConfigService} by setting:
 * <b>use_relationship.<em>factory-tag</em>.<em>role</em></b>
 * If this is a comma separated list it implies an OR of the component parts.
 * within this AND combinations can be specified as + separated terms.
 * 
 * The factory (or its {@link Composite}s) can implement {@link AccessRoleProvider} to provide roles.
 * 
 * Role names containing a period are qualified names the qualifier can be:
 * <ul>
 * <li> <b>global</b> the role is a global role not a relationship.</li>
 * <li> <b>boolean</b> Use a boolean filter so all/none relationships match.</li>
 * <li> <em>factory-tag</em> un-modified role from factory or {@link Composite} or a named filter if the factory implements {@link NamedFilterProvider}</li>
 * <li> The tag of a {@link RelationshipProvider} for the target.</li>
 * <li> The tag of a {@link AccessRoleProvider}</li>
 * </ul> 
 * 
 * @author spb
 *
 * @param <A>
 */
@PreRequisiteService(ConfigService.class)
public abstract class AbstractSessionService<A extends AppUser> implements Contexed, SessionService<A>{
	/** prefix for relationship definitions that map to a global role
	 * 
	 */
	private static final String GLOBAL_ROLE_RELATIONSHIP_BASE = "global";
	/** prefix for relationship definitions that map to a boolean filter
	 * 
	 */
	private static final String BOOLEAN_RELATIONSHIP_BASE = "boolean";
	/**
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
	private Map<String,Boolean> toggle_map=null;
	protected AppContext c;
    
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
			return "RelationshipTag [tag=" + tag + ", id=" + id + ", role=" + role + "]";
		}
	}
	private Map<RelationshipTag,Boolean> relationship_map=null;
	// map of roles to filters.
	private Map<String,BaseFilter> roles = new HashMap<String, BaseFilter>();
	public AbstractSessionService(AppContext c) {
		this.c=c;
		
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
	
	
	@SuppressWarnings("unchecked")
	public AppUserFactory<A> getLoginFactory() {
		if( fac != null ){
			return fac;
		}
		try{
			Logger log =c.getService(LoggerService.class).getLogger(getClass());
			String table=getLoginTable();
			log.debug("login-table="+table);
			Class<? extends AppUserFactory> clazz;
			if( table == null ){
				// If the login factory has a hardwired table then we specify the class
				// name as follows
				log.debug("looking for login-factory should be "+c.getInitParameter("class.login-factory","unset"));
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
		return c.getInitParameter("login-table");
	}

	protected Class<? extends AppUserFactory> getDefaultFactoryClass(){
		return AppUserFactory.class;
	}
	public AppContext getContext() {
		return c;
	}
	/** get the current State of a role toggle or null if not a toggle role
	 * 
	 * @param role
	 * @return Boolean or null
	 */
	public final Boolean getToggle(String role){
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
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();

		map.put(SessionService.ADMIN_ROLE, Boolean.FALSE);
		String additions = getContext().getInitParameter("toggle_roles");
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
		return new HashMap<String,Boolean>(toggle_map);
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
	public final boolean hasRole(String name){
		// role name aliasing
		
		//The cahHaveRole method caches its result
		// but we still want to save the answer for
		// the original query. shortcutTest must bypass
		// all caching
		String role=null;
		if( shortcutTestRole(name) || canHaveRole(name)){ // check queried role first
			role = name;
		}else{
			for(String r : getRoleSet(null, name)){
				if( ! r.equals(name) && canHaveRole(r)){
					role=r;
					cacheRole(name, true); // remember result of expansion
				}
			}
		}
		if( role == null){
			// none of the equivalents are allowed
			cacheRole(name, false);
			return false;
		}
		

		// role now points to a real role that we can have.
		
		// check the toggle value if this returns null then this is
		// a non toggling role
		Boolean toggle = getToggle(role);
		if( toggle != null ){
			return toggle.booleanValue();
		}
		return true;
		
	}

	/** map role name to a comma separated list of alternative roles to check.
	 * 
	 * Note the original name should always be checked explicitly first
	 * with the alternatives only checked if 
	 * 
	 * @param name
	 * @return 
	 */
	public String mapRoleName(String name) {
		return getContext().getInitParameter(USE_ROLE_PREFIX+name, name);
	}
	
	private Set<String> getRoleSet(Set<String> set, String name){
		Set<String> result=set;
		if( set == null){
			set = new HashSet<>();
		}
		if( set.contains(name)){
			return set;
		}
		set.add(name);
		String alt_list = mapRoleName(name);
		if( alt_list != null && ! alt_list.isEmpty()){
			for(String c : alt_list.split("\\s*,\\s*")){
				getRoleSet(set,c);
			}
		}
		return set;
	}
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
	public Set<String> getToggleRoles(){
		if( toggle_map == null ){
			setupToggleMap();
		}
		// have predictable order
		Set<String> set = new TreeSet<String>();
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
	 * @return
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
	
	/** updates the cached result
	 * 
	 * @param role
	 * @param value
	 */
	private void cacheRole(String role, boolean value){
		if (role_map == null) {
			role_map = setupRoleMap();
		}
		role_map.put(role, Boolean.valueOf(value));
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
		result = new HashMap<String,Boolean>();
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
	public void clearCurrentPerson() {
		flushRelationships();
		clearRoleMap();
		person=null;
		removeAttribute(person_tag);
	}
	
	public void logOut(){
		clearCurrentPerson();
	}

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
		if( personID == null ){
			return null;
		}
		try {
			person = getLoginFactory().find(personID);
			if( person == null ){
				// not worked for some reason
				clearCurrentPerson();
				return null;
			}
		} catch (Throwable e) {
			clearCurrentPerson();  // clear first as error will try to report person
			error(e,"Error finding person by id "+personID);
			return null;
		}
		if( person != null && ! person.canLogin()){
			
			// login now forbidden
			clearCurrentPerson();
			return null;
		}
		return person;
	}

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
		return id;
	}
	

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

	public boolean isCurrentPerson(A person){
		if( person == null || ! haveCurrentUser()){
			return false;
		}
		return getPersonID() == person.getID();
	}
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

	public void setCurrentRoleToggle(Map<String, Boolean> toggleMap) {
		toggle_map=toggleMap;	
	}
	/** Set a temporary (not stored to database) role.
	 * 
	 * @param role
	 */
	public void setTempRole(String role){
		if( role_map == null){
			role_map = setupRoleMap();
		}
		role_map.put(role, Boolean.TRUE);
		flushRelationships();
	}

	public String getName() {
		A user = getCurrentPerson();
		if( user != null ){
			return user.getName();
		}
		return null;
	}

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
			PreparedStatement stmt = null;
			try {
				stmt = ctx.getConnection().prepareStatement(role_query.toString());
				stmt.setInt(1, id);
				stmt.setString(2, role);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return true;
				}
			} finally {
				if( stmt != null ){
				   stmt.close();
				}
			}
		} catch (SQLException e) {
			conn.getService(LoggerService.class).getLogger(AbstractSessionService.class).error("Error checking AppUser role",e);
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
		
	   try {
		   SQLContext ctx=context.getService(DatabaseService.class).getSQLContext();
			StringBuilder role_query=new StringBuilder();
			role_query.append("DELETE FROM ");
			ctx.quote(role_query, ROLE_TABLE).append(" WHERE ");
			ctx.quote(role_query,ROLE_PERSON_ID).append("=? AND ");
			ctx.quote(role_query,ROLE_FIELD).append("=?");
					
			PreparedStatement stmt = null;
			try {
				stmt = ctx.getConnection().prepareStatement(role_query.toString());
				stmt.setInt(1, id);
				stmt.setString(2, role);
				stmt.executeUpdate();
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(context)){
					Logger log = context.getService(LoggerService.class).getLogger(context.getClass());
					log.debug(role_query+" id="+id+" role="+role);
				}
			} finally {
				if( stmt != null){
				  stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new DataFault("SQLException ", e);
		}
	}



	public static void addRoleByID(AppContext c, int id, String role) throws DataFault {
		try {
			SQLContext ctx=c.getService(DatabaseService.class).getSQLContext();
			StringBuilder role_query=new StringBuilder();
			role_query.append( "INSERT INTO ");
			ctx.quote(role_query,ROLE_TABLE).append(" (");
			ctx.quote(role_query, ROLE_PERSON_ID).append(",");
			ctx.quote(role_query, ROLE_FIELD).append(") VALUES(?,?)");;
					
			PreparedStatement stmt = null;
			try {
				stmt = ctx.getConnection().prepareStatement(role_query.toString());
				stmt.setInt(1, id);
				stmt.setString(2, role);
				stmt.executeUpdate();
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(c)){
					Logger log = c.getService(LoggerService.class).getLogger(c.getClass());
					log.debug(role_query+" id="+id+" role="+role);
				}
			} finally {
				if( stmt != null ){
				  stmt.close();
				}
			}
		} catch (SQLException e) {
			throw new DataFault("SQLException ", e);
		}
	}


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


	
	public boolean canHaveRole(A user, String role) {
		if( user == null || role == null){
			return false;
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
	/** Check a specific user for role membership including name mapping
	 * 
	 * toggle roles evaluate as enabled.
	 * 
	 * @param user
	 * @param role
	 * @return
	 */
    private boolean canHaveRoleWithMapping(A user, String role){
    	for(String r : getRoleSet(null, role)){
    		if( canHaveRole(user,r)){
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



	public Class<SessionService> getType() {
		return SessionService.class;
	}


	/** Get the Locale to use in the current context
	 * 
	 * @return Locale
	 */
	public Locale getLocale() {
		return Locale.UK;
	}
	public TimeZone getTimeZone(){
		return TimeZone.getDefault();
	}

	
	
	
	@Override
	public final <T extends DataObject> BaseFilter<? super T> getRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role) throws UnknownRelationshipException {
		// We may be storing roles from different types so prefix all tags with the type.
		// prefix is never seen outsite this cache.
		String store_tag=fac.getTag()+"."+role;
		BaseFilter<? super T> result = roles.get(store_tag);
		if( result == null ){
			result = makeRelationshipRoleFilter(fac,role,null,null);
			roles.put(store_tag, result);
		}
		return result;
	}
	@Override
	public final <T extends DataObject> BaseFilter<? super A> getPersonInRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role, T target) throws UnknownRelationshipException {
		// We may be storing roles from different types so prefix all tags with the type.
		// prefix is never seen outside this cache.
		// This filter is also parameterised by the target
		String store_tag="PersonFilter."+fac.getTag()+(target == null ? "" : "."+target.getID())+"."+role;
		BaseFilter<? super A> result = roles.get(store_tag);
		if( result == null ){
			result = makePersonInRelationshipRoleFilter(fac, role,target);
			roles.put(store_tag, result);
		}
		return result;
	}
	@Override
	public <T extends DataObject> BaseFilter<? super T> getTargetInRelationshipRoleFilter(DataObjectFactory<T> fac, String role,
			A person) throws UnknownRelationshipException {
		if( person == null){
			return getRelationshipRoleFilter(fac, role);
		}
		String store_tag="TargetFilter."+fac.getTag()+"."+person.getID()+"."+role;
		BaseFilter<? super T> result = roles.get(store_tag);
		if( result == null ){
			// adding in the default filter breaks everything for some reason
			//result = new AndFilter<T>(fac.getTarget(),
			//		fac.getDefaultRelationshipFilter(),
			//		makeRelationshipRoleFilter(fac,role,person));
			result = makeRelationshipRoleFilter(fac,role,person,null);
			roles.put(store_tag, result);
		}
		return result;
	}

	private Set<String> searching_roles = new HashSet<String>();
	/** actually construct the filter.
	 * 
	 * A role can be mapped to a different implementation by setting:
	 * <b>use_relationship.<em>factory-tag</em>.<em>role</em></b>
	 * If this is a comma separated list it implies an OR of the component parts.
	 * within this AND combinations can be specified as + separated terms.
	 * 
	 * The factory (or its {@link Composite}s) can implement {@link AccessRoleProvider} to provide roles.
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
	 * @param fac2   target factory
	 * @param role   relationship string
	 * @param person person to query (null for current person)
	 * @param def default query to use if no definition (pass null to throw exception)
	 * @return
	 * @throws UnknownRelationshipException 
	 */
	protected <T extends DataObject> BaseFilter<? super T> makeRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person,BaseFilter<T> def) throws UnknownRelationshipException {
		if( searching_roles.contains(role)){
			// recursive creation
			throw new UnknownRelationshipException("recursive definition of "+role);
		}
		searching_roles.add(role);
		try{
		if( role.contains(",")){
			// OR combination of filters
			OrFilter<T> or = new OrFilter<T>(fac2.getTarget(), fac2);
			for( String  s  : role.split(",")){
				if( person == null){
					or.addFilter(getRelationshipRoleFilter(fac2, s));
				}else{
					or.addFilter(getTargetInRelationshipRoleFilter(fac2, s, person));
				}
			}
			return or;
		}
		if( role.contains("+")){
			// AND combination of filters
			AndFilter<T> and = new AndFilter<T>(fac2.getTarget());
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
		
	    if( role.contains(".")){
	    	// qualified role
	    	int pos = role.indexOf('.');
	    	String base =role.substring(0, pos);
	    	String sub = role.substring(pos+1);
	    	if( base.equals(GLOBAL_ROLE_RELATIONSHIP_BASE)){
	    		if( person == null){
	    			// Only the global role filter can allow relationship without a current person
	    			// as roles may be asserted from the container.
	    			return new GlobalRoleFilter<T>(this, sub);
	    		}else{
	    			
	    			return new GenericBinaryFilter<T>(fac2.getTarget(),canHaveRole(person, role));
	    		}
	    	}
	    	if( base.equals(BOOLEAN_RELATIONSHIP_BASE)){
	    		
	    		return new GenericBinaryFilter<T>(fac2.getTarget(),Boolean.valueOf(sub));
	    		
	    	}
	    	if( person == null && ! haveCurrentUser()){
	    		// No users specified
	    		return new GenericBinaryFilter<T>(fac2.getTarget(),false);
	    	}
	    	
	    	if( base.equals(fac2.getTag())){
	    		// This is a reference a factory/composite role from within a redefined
	    		// definition. direct roles can be qualified if we want qualified names cannot
	    		// be overridden. 
	    		BaseFilter<? super T> result = makeDirectRelationshipRoleFilter(fac2, sub,person,null);
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
	    }else{
	    	// Non qualified name
	    	if( person == null){
	    		if( ! haveCurrentUser()){
	    			// No target person to filter against
	    			return new GenericBinaryFilter<T>(fac2.getTarget(),false);
	    		}
	    	}
	    	// direct roles can be un-qualified though not if we want multiple levels of qualification.
	    	BaseFilter<? super T> result = makeDirectRelationshipRoleFilter(fac2, role,person,null);
			if( result != null ){
				return result;
			}
	    }
		if( def == null){
			throw new UnknownRelationshipException(role);
		}
		return def;
		}finally{
			searching_roles.remove(role);
		}
	}
	protected <T extends DataObject> BaseFilter<? super A> makePersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,T target) throws UnknownRelationshipException {
		AppUserFactory<A> login_fac = getLoginFactory();
		Class<? super A> target_type = login_fac.getTarget();
		if( searching_roles.contains(role)){
			// recursive creation
			throw new UnknownRelationshipException("recursive definition of "+role);
		}
		searching_roles.add(role);
		try{
		if( role.contains(",")){
			// OR combination of filters
			OrFilter<A> or = new OrFilter<A>(target_type, login_fac);
			for( String  s  : role.split(",")){
				or.addFilter(getPersonInRelationshipRoleFilter(fac2, s,target));
			}
			return or;
		}
		if( role.contains("+")){
			// OR combination of filters
			AndFilter<A> and = new AndFilter<A>(target_type);
			for( String  s  : role.split("\\+")){
				and.addFilter(getPersonInRelationshipRoleFilter(fac2, s,target));
			}
			return and;
		}
		// should be a single filter now.
		
	    if( role.contains(".")){
	    	// qualified role
	    	int pos = role.indexOf('.');
	    	String base =role.substring(0, pos);
	    	String sub = role.substring(pos+1);
	    	if( base.equals(GLOBAL_ROLE_RELATIONSHIP_BASE)){
	    		// roles don't enumerate
	    		return new GenericBinaryFilter<A>(target_type,false);
	    	}
	    	if( base.equals(fac2.getTag())){
	    		// This is to reference a factory/composite role from within a redefined
	    		// definition. direct roles can be qualified if we want qualified names cannot
	    		// be overridden. 
	    		BaseFilter<? super A> result = makeDirectPersonInRelationshipRoleFilter(fac2, sub,target);
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
	    }else{
	    	// direct roles can be un-qualified though not if we want multiple levels of qualification.
	    	BaseFilter<? super A> result = makeDirectPersonInRelationshipRoleFilter(fac2, role,target);
			if( result != null ){
				return result;
			}
	    }
		
		throw new UnknownRelationshipException(role);
		}finally{
			searching_roles.remove(role);
		}
	}
	/**
	 * @param fac2
	 * @param role
	 * @throws UnknownRelationshipException 
	 */
	protected <T extends DataObject> BaseFilter<? super T> makeDirectRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person,BaseFilter<T> def) throws UnknownRelationshipException {
		// look for directly implemented relations first
		BaseFilter<? super T> result=null;
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
	    if( fac2 instanceof NamedFilterProvider){
	    	result = ((NamedFilterProvider)fac2).getNamedFilter(role);
	    	if( result != null){
	    		return result;
	    	}
	    }
	    // Its not one of the directly implemented roles maybe its derived
	    String defn = getContext().getInitParameter(USE_RELATIONSHIP_PREFIX+fac2.getTag()+"."+role);
	    if( defn != null){
	    	// don't pass def as it is the sub-defn that is not resolving
	    	return makeRelationshipRoleFilter(fac2, defn,person,null);
	    }
	   
	    if( def != null){
	    	return def;
	    }
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
	}
	protected <T extends DataObject> BaseFilter<? super A> makeDirectPersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,T target) throws UnknownRelationshipException {
		BaseFilter<? super A> result=null;
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
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
	}
	@Override
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target, String role) throws UnknownRelationshipException {
		// For the moment we only cache relationships within a request
		// to avoid stale values as a users state changes
		if( relationship_map == null && CACHE_RELATIONSHIP_FEATURE.isEnabled(getContext())){
			relationship_map = new HashMap<RelationshipTag,Boolean>();
		}
		RelationshipTag tag = new RelationshipTag(fac.getTag(), target.getID(), role);
		if( relationship_map != null && relationship_map.containsKey(tag)){
			return relationship_map.get(tag).booleanValue();
		}
		boolean result = fac.matches(getRelationshipRoleFilter(fac, role), target);
		if( relationship_map != null ){
			relationship_map.put(tag, result);
		}
		return result;
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
	
}