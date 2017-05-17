package uk.ac.ed.epcc.webapp.session;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
/** An {@link AppUserComposite} that adds information to the person summary from a directory lookup.
 * 
 * The users WebName is assumed to map to the ldap <b>uid</b> attribute
 * <p>
 * Parameters:
 * <ul>
 * <li> <b>directory_info.label</b> Attribute label shown in table. 
 * </ul>
 * 
 * @author spb
 *
 * @param <AU>
 */
public class DirectoryComposite<AU extends AppUser> extends AppUserComposite<AU,DirectoryComposite> implements SummaryContributer<AU> {

	public DirectoryComposite(AppUserFactory fac) {
		super(fac);
	}

	@Override
	protected Class getType() {
		return DirectoryComposite.class;
	}

	public Map<String,Object> getDirectoryInfo(AU target){
		String name = target.getRealmName(WebNameFinder.WEB_NAME);
		if( name == null || name.isEmpty()){
			return null;
		}
		
		try{
			
			
			DirContext dctx = makeDirContext();
			if( dctx == null){
				return null;
			}
			SearchControls sc = new SearchControls();
			
		     sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		     String attributes = getContext().getInitParameter("directory_info.attributes");
		     if( attributes != null){
		    	 sc.setReturningAttributes(attributes.split(","));
		     }
		     String filter = "uid="+name;
		     String base_dn = getContext().getInitParameter("directory_info.base_dn");
		     NamingEnumeration results = dctx.search(new LdapName(base_dn),filter,  sc);

		     if( results.hasMore()){
		    	 Map<String,Object> data = new LinkedHashMap<>();
		    	 SearchResult sr = (SearchResult) results.next();
		    	 Attributes attrs = sr.getAttributes();
		    	 NamingEnumeration<? extends Attribute> it = attrs.getAll();
		    	 while( it.hasMore()){
		    		 Attribute at = it.next();
		    		 String id = at.getID();
		    		 if( at.size() == 1){
		    			 data.put(id, at.get());
		    		 }else{
		    			 Set set = new LinkedHashSet<>();
		    			 for(int i=0; i< at.size(); i++){

		    				 set.add(at.get(i));
		    			 }
		    			 data.put(id, set);
		    		 }
		    	 }
		    	 return data;
		     }
		}catch(Throwable t){
			getLogger().error("Error doing directory lookup",t);
			
		}
		return null;
	}

	private DirContext dircontext=null;
	private boolean error=false;
	private DirContext makeDirContext() throws NamingException {
		if( error ){
			return null;
		}
		if( dircontext != null){
			return dircontext;
		}
		Hashtable<String,String> env = new Hashtable<String,String>();
		
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		
		
		//env.put(Context.SECURITY_PRINCIPAL, getConnectionName(name));
		//env.put(Context.SECURITY_CREDENTIALS, password);
		
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		
		String url = getContext().getInitParameter("directory_info.url");
		env.put(Context.PROVIDER_URL, url);
		dircontext = new InitialDirContext(env);
		return dircontext;
	}
	
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		Map<String,Object> data = getDirectoryInfo(target);
		if( data != null && ! data.isEmpty()){
			Table dir_table = new Table();
			dir_table.addMap("Value", data);
			dir_table.setKeyName("Property");
			dir_table.setPrintHeadings(false);
			if( dir_table.hasData()){
				attributes.put(getContext().getInitParameter("directory_info.label","Directory info"), dir_table);
			}
		}

	}

}
