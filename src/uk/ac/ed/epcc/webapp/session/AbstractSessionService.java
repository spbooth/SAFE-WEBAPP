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
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.DualFalseFilter;
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
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.model.relationship.GlobalRoleFilter;
import uk.ac.ed.epcc.webapp.model.relationship.RelationshipProvider;
/** Abstract base implementation of {@link SessionService}
 * 
 * 
 * 
 * @author spb
 *
 * @param <A>
 */
public abstract class AbstractSessionService<A extends AppUser> implements Contexed, SessionService<A>{
	/** Property prefix to allow role name aliasing.
	 * The property use_role.<i>name</i> defines a role name to use
	 * instead of name.
	 * 
	 */
	public static final String USE_ROLE_PREFIX = "use_role.";
	public static final Feature TOGGLE_ROLES_FEATURE = new Feature("toggle_roles",true,"allow some roles to toggle on/off");
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
	
	public AbstractSessionService(AppContext c) {
		this.c=c;
		
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
		String role=null;
		if( canHaveRole(name)){ // check queried role first
			role = name;
		}else{
			String list=mapRoleName(name);
			if( ! list.equals(name)){
				// consider each equivalent role in turn
				for(String r : list.split(",")){
					if( canHaveRole(r)){
						role=r;
					}
				}
			}
		}
		if( role == null){
			// none of the equivalents are allowed
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

	/**
	 * @param name
	 * @return
	 */
	public String mapRoleName(String name) {
		return getContext().getInitParameter(USE_ROLE_PREFIX+name, name);
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
		if( shortcutTestRole(role)){
			return true;
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
		return rawRoleQuery(getContext(),getPersonID(),role);
	}

	/** clears all record of the current person.
	 * 
	 * The toggle-map is not cleared. It will have no effect if a new person
	 * does not have the role but it allows toggle state to be retained acSross a SU.
	 * 
	 */
	public void clearCurrentPerson() {
		
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

public Set<A> withRole(String role) {
		role = mapRoleName(role);
		AppContext conn = getContext();
		Set<A> result = new HashSet<A>();
		try {
			SQLContext ctx=conn.getService(DatabaseService.class).getSQLContext();
			StringBuilder role_query=new StringBuilder();
			AppUserFactory<A> fac = getLoginFactory();
			role_query.append("SELECT ");
			ctx.quote(role_query, ROLE_PERSON_ID).append(" FROM ");
			ctx.quote(role_query,ROLE_TABLE).append(" WHERE ");
			ctx.quote(role_query,ROLE_FIELD).append("=? ");
			PreparedStatement stmt = null;
			try {
				stmt = ctx.getConnection().prepareStatement(role_query.toString());
				stmt.setString(1, role);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					try {
						result.add(fac.find(rs.getInt(1)));
					} catch (DataException e) {
						error(e,"Error getting person from role");
					}
				}
			} finally {
				if( stmt != null ){
				   stmt.close();
				}
			}
		} catch (SQLException e) {
			error(e,"Error checking AppUser role");
			// maybe table missing
			// this is null if table exists
			setupRoleTable(conn);
		}
		return result;
	}


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
		return rawRoleQuery(getContext(), user.getID(), role);
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

	// map of roles to filters.
	private Map<String,BaseFilter> roles = new HashMap<String, BaseFilter>();
	
	
	@Override
	public final <T extends DataObject> BaseFilter<? super T> getRelationshipRoleFilter(DataObjectFactory<T> fac,
			String role) throws UnknownRelationshipException {
		// We may be storing roles from different types so prefix all tags with the type.
		// prefix is never seen outsite this cache.
		String store_tag=fac.getTag()+"."+role;
		BaseFilter<? super T> result = roles.get(store_tag);
		if( result == null ){
			result = makeRelationshipRoleFilter(fac,role,null);
			roles.put(store_tag, result);
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
			result = makeRelationshipRoleFilter(fac,role,person);
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
	 * <li> <em>factory-tag</em> un-modified role from factory or {@link Composite}.</li>
	 * <li> The tag of a {@link RelationshipProvider} for the target.</li>
	 * <li> The tag of a {@link AccessRoleProvider}</li>
	 * </ul>
	 * 
	 * @param fac2   target factory
	 * @param role   relationship string
	 * @param person person to query (null for current person)
	 * @return
	 * @throws UnknownRelationshipException 
	 */
	protected <T extends DataObject> BaseFilter<? super T> makeRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person) throws UnknownRelationshipException {
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
			// OR combination of filters
			AndFilter<T> and = new AndFilter<T>(fac2.getTarget());
			for( String  s  : role.split("+")){
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
	    	if( base.equals("global")){
	    		if( person == null){
	    			// Only the global role filter can allow relationship without a current person
	    			// as roles may be asserted from the container.
	    			return new GlobalRoleFilter<T>(this, sub);
	    		}else{
	    			return new DualFalseFilter<T>(fac2.getTarget());
	    		}
	    	}
	    	if( person == null && ! haveCurrentUser()){
	    		return new DualFalseFilter<T>(fac2.getTarget());
	    	}
	    	
	    	if( base.equals(fac2.getTag())){
	    		// This is a reference a factory/composite role from within a redefined
	    		// definition. direct roles can be qualified if we want qualified names cannot
	    		// be overridden. 
	    		BaseFilter<? super T> result = makeDirectRelationshipRoleFilter(fac2, sub,person);
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
	    	if( person == null){
	    		if( ! haveCurrentUser()){
	    			return new DualFalseFilter<T>(fac2.getTarget());
	    		}
	    	}
	    	// direct roles can be un-qualified though not if we want multiple levels of qualification.
	    	BaseFilter<? super T> result = makeDirectRelationshipRoleFilter(fac2, role,person);
			if( result != null ){
				return result;
			}
	    }
		
		throw new UnknownRelationshipException(role);
		}finally{
			searching_roles.remove(role);
		}
	}
	protected <T extends DataObject> BaseFilter<A> makePersonInRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,T target) throws UnknownRelationshipException {
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
			for( String  s  : role.split("+")){
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
	    	if( base.equals("global")){
	    		// roles don't enumerate
	    		return new DualFalseFilter<A>(target_type);
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
	    }else{
	    	// direct roles can be un-qualified though not if we want multiple levels of qualification.
	    	BaseFilter<A> result = makeDirectPersonInRelationshipRoleFilter(fac2, role,target);
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
	protected <T extends DataObject> BaseFilter<? super T> makeDirectRelationshipRoleFilter(DataObjectFactory<T> fac2, String role,A person) throws UnknownRelationshipException {
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
	    // Its not one of the directly implemented roles maybe its derived
	    String defn = getContext().getInitParameter("use_relationship."+fac2.getTag()+"."+role);
	    if( defn != null){
	    	return makeRelationshipRoleFilter(fac2, defn,person);
	    }
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
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
	    String defn = getContext().getInitParameter("use_relationship."+fac2.getTag()+"."+role);
	    if( defn != null){
	    	return makePersonInRelationshipRoleFilter(fac2, defn,target);
	    }
	    // unrecognised role
	    throw new UnknownRelationshipException(role);
	}
	@Override
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target, String role) throws UnknownRelationshipException {
		return fac.matches(getRelationshipRoleFilter(fac, role), target);
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