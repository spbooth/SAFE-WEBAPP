//| Copyright - The University of Edinburgh 2015                            |
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.UnsupportedException;
import uk.ac.ed.epcc.webapp.ssl.SSLService;

/** A {@link PasswordAuthComposite} that queries an external pre-populated LDAP server for authentication.
 * 
 * The assumption is that the user has read access to their own LDAP entry.
 * 
 * All the password-change functionality is not supported but
 * database records will be auto-created when an authentication succeeds.
 * 
 * We assume all users are at the same level of the ldap tree and the supplied username corresponds to
 * a unique attribute (default to uid) of the user record. This allows a LdapName for the record to be
 * generated simply and does not require an anonymous bind to search for the user.
 * 
 * An anonymous bind can be introduced by sub-classing.
 * 
 * The identifying attribute name is stored as the web-name.
 * <p>
 * Configuration properties:
 * <ul>
 * <li><b>authentication.ldap.factory</b> factory class defaults to <b>com.sun.jndi.ldap.LdapCtxFactory</b></li>
 * <li><b>authentication.ldap.url</b> connection url </li>
 * <li><b>authentication.ldap.base</b> base dn, this should be the path immediately above the user records</li>
 * <li><b>ldap.connection_domain</b> AD domain. If set LDAP authentication will be as <i>username</i>@<i>domain</i> instead of LDAP principal DN.</li>
 * <li><b>authentication.ldap.name_attr</b> Name component to add to base_dn to generate user DN defaults to <i>uid</i></li>
 * <li><b>authentication.ldap.property.</b><i>attributeName</i> name of DB field to store attribute</li>
 * </ul>
 * 
 * @author spb
 * @param <T> type of {@link AppUser}
 *
 */

public class LdapPasswordComposite<T extends AppUser> extends PasswordAuthComposite<T> {

	/**
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_NAME_ATTR = "authentication.ldap.name_attr";
	/**
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_FACTORY = "authentication.ldap.factory";
	/**
	 * 
	 */
	private static final String LDAP_CONNECTION_DOMAIN = "ldap.connection_domain";
	/** configuration parameter prefix that maps attribute ids to database field names for
	 * attributes that should be cached in the database.
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_PROPERTY_PREFIX = "authentication.ldap.property.";
	/** default ldap filter for selecting user records.
	 * 
	 */
	private static final String DEFAULT_LDAP_FILTER = "(cn=*)";
	/** property to set the ldap filter to use when authenticating.
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_FILTER = "authentication.ldap.filter";
	/** base DN for user entries
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_BASE = "authentication.ldap.base";
	/** property to set LDAP connection url
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_URL = "authentication.ldap.url";
	/** 
	 * 
	 */
	private static final String AUTHENTICATION_LDAP_SSL = "authentication.ldap.ssl";

	protected final String ldap_url;
	protected final boolean use_ssl;
	protected final LdapName base_name;
	protected final String factory;
	protected final String filter;
	protected final String name_attr;
	/**
	 * @param fac
	 */
	public LdapPasswordComposite(AppUserFactory<T> fac) {
		super(fac);
		AppContext context = fac.getContext();
		ldap_url = context.getInitParameter(AUTHENTICATION_LDAP_URL);
		use_ssl = context.getBooleanParameter(AUTHENTICATION_LDAP_SSL, ldap_url == null ? false : ldap_url.startsWith("ldaps"));
		String base= context.getInitParameter(AUTHENTICATION_LDAP_BASE);
		if( base == null){
			base_name=null;
		}else{
			LdapName tmp=null;
			try {
				tmp=new LdapName(base);
			} catch (InvalidNameException e) {
				context.getService(LoggerService.class).getLogger(getClass()).error("mal-formed base", e);
			}
			base_name=tmp;
		}
		// probably only change the factory for mock object testing
		factory = context.getInitParameter(AUTHENTICATION_LDAP_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		filter = context.getInitParameter(AUTHENTICATION_LDAP_FILTER, DEFAULT_LDAP_FILTER);
		name_attr = context.getInitParameter(AUTHENTICATION_LDAP_NAME_ATTR, "uid");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#checkPassword(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public boolean checkPassword(T u, String password) {
		String name = ((AppUserFactory)getFactory()).getCanonicalName(u);
		try {
			T user = findByLoginNamePassword(name, password);
			if( user != null && u.equals(user)){
				return true;
			}
		} catch (DataException e) {
			getLogger().error("Error in lookup !!!",e);
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#findByLoginNamePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public T findByLoginNamePassword(String name, String password)
			throws DataException {
		
		AppContext context = getContext();
		Logger log = getLogger();
		
		if( name == null || password == null) {
			return null;
		}
		if( ldap_url == null || base_name == null){
			log.error("No LDAP connection URL or basename");
			return null;
		}
		
		
		if( use_ssl){
			try {
				context.getService(SSLService.class).makeDefaultContext();
			} catch (Exception e1) {
				getLogger().error("Error setting default keystore",e1);
			}
		}
		
		try {
			
			
			
			DirContext dctx = getContext(name, password);

			SearchControls sc = new SearchControls();
			//String[] attributeFilter = { "cn", "mail" };
			//sc.setReturningAttributes(attributeFilter);
		     sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		     
		     
		     
			NamingEnumeration results = dctx.search(getPrincipal(name),filter,  sc);
		     if(results.hasMore()) {
		    	 log.debug("found an entry");
		    	 // we have a result
		    	 AppUserFactory<T> user_fac = (AppUserFactory<T>)getFactory();
		    	 AppUserNameFinder<T, ?> realmFinder = user_fac.getRealmFinder(WebNameFinder.WEB_NAME);
				T user = realmFinder.findFromString(name);
		    	
		    	 if( user == null ){
		    		 log.debug("making new user "+name);
		    		 user = fac.makeBDO();
		    		 realmFinder.setName(user, name);
		    	 }else{
		    		 log.debug("found user "+name);
		    	 }

		    	 SearchResult sr = (SearchResult) results.next();
		    	 Attributes attrs = sr.getAttributes();
		    	 NamingEnumeration<? extends Attribute> it = attrs.getAll();
		    	 Record record = getRecord(user);
		    	 while( it.hasMore()){
		    		 Attribute at = it.next();
		    		 String id = at.getID();
		    		 log.debug("found attr "+id);
					String at_name = context.getInitParameter(AUTHENTICATION_LDAP_PROPERTY_PREFIX+id);
					if( at_name != null ){
						record.setOptionalProperty(at_name, at.get());
					}
		    	 }
		    	 user.commit();
		    	 return user;
		     }
		     dctx.close();
		     return null;
		     
		} catch (NamingException e) {
			getLogger().info("Authentication failed", e);
			return null;
		}
	}

	/**
	 * @param name
	 * @param password
	 * @return
	 * @throws InvalidNameException
	 * @throws NamingException
	 */
	protected DirContext getContext(String name, String password) throws InvalidNameException, NamingException {
		Hashtable<String,String> env = new Hashtable<>();
		
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		
		
		env.put(Context.SECURITY_PRINCIPAL, getConnectionName(name));
		env.put(Context.SECURITY_CREDENTIALS, password);
		if( use_ssl ){
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}
		env.put(Context.PROVIDER_URL, ldap_url);
		DirContext dctx = new InitialDirContext(env);
		return dctx;
	}

	private String getConnectionName(String name) throws InvalidNameException{
		String connection_domain = getContext().getInitParameter(LDAP_CONNECTION_DOMAIN);
		if( connection_domain != null ){
			// Active directory also allows username@domain as well as the LDAP principal
			// can avoid sub-org search this way
			return name+"@"+connection_domain;
		}
		return getPrincipal(name).toString();
	}
	/** generate the name of the authenticating user from the supplied id.
	 * 
	 * @param name
	 * @param base_name
	 * @return
	 * @throws InvalidNameException
	 */
	private Name getPrincipal(String name) throws InvalidNameException {
		// This assumes all users are at the same level of the heirarchy.
		// if this is not the case we can sub-class here to bind anonymously and search
		// for a record.
		LdapName result = (LdapName) base_name.clone();
		return result.add(new Rdn(name_attr, name));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#findByLoginNamePassword(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public T findByLoginNamePassword(String name, String password,
			boolean check_fail_count) throws DataException {
		// no fail count supported
		return findByLoginNamePassword(name, password);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#canResetPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean canResetPassword(T user) {
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#setPassword(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setPassword(T user, String password) throws DataFault {
		throw new UnsupportedException("Password modification not supported");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#lockPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void lockPassword(T user) throws UnsupportedException{
		throw new UnsupportedException("Password modification not supported");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#newPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void newPassword(T user) throws Exception {
		throw new UnsupportedException("Password modification not supported");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#randomisePassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public String randomisePassword(T user) throws DataFault {
		throw new UnsupportedException("Password modification not supported");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#firstPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public String firstPassword(T user) throws DataFault {
		throw new UnsupportedException("Password modification not supported");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordAuthComposite#mustResetPassword(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean mustResetPassword(T user) {
		return false;
	}
	@Override
	public String reasonForReset(T user) {
		return "";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		// This is allowed to be null as it may be an auxiliary login method
		// for Pass
		spec.setField(WebNameFinder.WEB_NAME, new StringFieldType(true, null, 255));
		try {
			spec.new Index("webname_key", true, WebNameFinder.WEB_NAME);
		} catch (InvalidArgument e) {
			getContext().getService(LoggerService.class).getLogger(getClass()).error("Error making unique webname_key",e);
		}
		return spec;
	}

	
}