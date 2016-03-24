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
 * The ldap name is stored as the web-name.
 * 
 * @author spb
 * @param <T> type of {@link AppUser}
 *
 */

public class LdapPasswordComposite<T extends AppUser> extends PasswordAuthComposite<T> {

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

	/**
	 * @param fac
	 */
	public LdapPasswordComposite(AppUserFactory<T> fac) {
		super(fac);
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
		String ldap_url = context.getInitParameter(AUTHENTICATION_LDAP_URL);
		if( ldap_url == null ){
			log.error("No LDAP connection URL");
			return null;
		}
		boolean use_ssl = context.getBooleanParameter(AUTHENTICATION_LDAP_SSL, ldap_url.startsWith("ldaps"));
		
		if( use_ssl){
			try {
				context.getService(SSLService.class).makeDefaultContext();
			} catch (Exception e1) {
				getLogger().error("Error setting default keystore",e1);
			}
		}
		
		try {
			String base= context.getInitParameter(AUTHENTICATION_LDAP_BASE);
			if( base == null ){
				getLogger().error("No LDAP base");
				return null;
			}

			LdapName base_name = new LdapName(base);
			
			Hashtable<String,String> env = new Hashtable<String,String>();
			// probably only change the factory for mock object testing
			String factory = context.getInitParameter("authentication.ldap.factory","com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			
			
			env.put(Context.SECURITY_PRINCIPAL, getConnectionName(name, base_name));
			env.put(Context.SECURITY_CREDENTIALS, password);
			if( use_ssl ){
				env.put(Context.SECURITY_PROTOCOL, "ssl");
			}
			log.debug("ldap url="+ldap_url);
			env.put(Context.PROVIDER_URL, ldap_url);
			DirContext dctx = new InitialDirContext(env);

			SearchControls sc = new SearchControls();
			//String[] attributeFilter = { "cn", "mail" };
			//sc.setReturningAttributes(attributeFilter);
		     sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		     String filter = context.getInitParameter(AUTHENTICATION_LDAP_FILTER, DEFAULT_LDAP_FILTER);
		     
		     
			NamingEnumeration results = dctx.search(getPrincipal(name, base_name),filter,  sc);
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

	private String getConnectionName(String name, LdapName base_name) throws InvalidNameException{
		String connection_domain = getContext().getInitParameter(LDAP_CONNECTION_DOMAIN);
		if( connection_domain != null ){
			// Active directory allows username@domain as well as the LDAP principal
			// 
			return name+"@"+connection_domain;
		}
		return getPrincipal(name, base_name).toString();
	}
	private Name getPrincipal(String name, LdapName base_name) throws InvalidNameException {
		// This assumes the canonical name comes from appending the user supplied name to
		// the basename.
		return base_name.add(new Rdn("cn", name));
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