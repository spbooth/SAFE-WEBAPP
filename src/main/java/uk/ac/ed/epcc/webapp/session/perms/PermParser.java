package uk.ac.ed.epcc.webapp.session.perms;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

public class PermParser<A extends AppUser> extends AbstractContexed {
	/** Dereference a field and take relationship on referenced object.
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
	/** prefix for relationship definitions that map to a boolean filter
	 * generated by a feature
	 */
	private static final String FEATURE_RELATIONSHIP_BASE = "feature";
	/** property prefix for relationship defns
	 * 
	 */
	private static final String USE_RELATIONSHIP_PREFIX = "use_relationship.";

	private SessionService<A> sess;
	public PermParser(SessionService<A> sess) {
		super(sess.getContext());
		this.sess=sess;
	}

	private Set<String> searching_roles = new LinkedHashSet<>();
	
	public <T extends DataObject> PermissionClause<T> parse(DataObjectFactory<T> fac2, String role) throws UnknownRelationshipException{
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
				OrPermissionClause<T> or = new OrPermissionClause<>(fac2);
				for( String  s  : role.split(OR_RELATIONSHIP_COMBINER)){
					
						try{
							PermissionClause<T> p = parse(fac2,s);
							if(p == null) {
								throw new UnknownRelationshipException(s);
							}
							or.add(p);
						}catch(UnknownRelationshipException e){
							if(AbstractSessionService.ALLOW_UNKNOWN_RELATIONSHIP_IN_OR_FEATURE.isEnabled(getContext())){
								getLogger().error( "Bad relationship in OR branch",e);
							}else{
								throw e;
							}
						}

				}
				return or;
			}
			if( role.contains(AND_RELATIONSHIP_COMBINER)){
				// AND combination of filters
				AndPermissionClause<T> and = new AndPermissionClause<>();
				for( String  s  : role.split("\\+")){
					try {
					PermissionClause<T> p = parse(fac2,s);
					if(p == null) {
						throw new UnknownRelationshipException(s);
					}
					and.add(p);
					}catch(UnknownRelationshipException e){
						if(AbstractSessionService.ALLOW_UNKNOWN_RELATIONSHIP_IN_OR_FEATURE.isEnabled(getContext())){
							getLogger().error( "Bad relationship in AND branch",e);
						}else{
							throw e;
						}
					}
				}
				return and;
			}
			// should be a single filter now.
			if( role.startsWith("!")) {
				return new NegatingClause<T>( parse(fac2,role.substring(1)));
			}else if( role.contains(RELATIONSHIP_DEREF)){
				// This is a remote relationship
				// Note this will also catch remote NamedRoles
				// Match this first as the remote relationship
				// might be qualified but the field name never is
				int pos = role.indexOf(RELATIONSHIP_DEREF);
				boolean is_optional=false;
				String link_field = role.substring(0, pos);
				if( link_field.endsWith("?")) {
					is_optional=true;
					link_field = link_field.substring(0, link_field.indexOf('?'));
				}
				String remote_role = role.substring(pos+RELATIONSHIP_DEREF.length());
				return new RemotePermissionClause<T>(fac2, link_field, remote_role,is_optional);
			}else if( role.contains(".")){
				// qualified role
				int pos = role.indexOf('.');
				String base =role.substring(0, pos);
				String sub = role.substring(pos+1);
				if( base.equals(GLOBAL_ROLE_RELATIONSHIP_BASE)){
					return new GlobalPermissionClause<T>( sub);
				}
				if( base.equals(BOOLEAN_RELATIONSHIP_BASE)){
					return new BinaryPermissionClause<T>( Boolean.valueOf(sub));
				}
				if( base.contentEquals(FEATURE_RELATIONSHIP_BASE)) {
		    		return new BinaryPermissionClause<T>(Feature.checkDynamicFeature(getContext(), sub,false));
		    	}
				

				if( base.equals(fac2.getTag())){
					// This is a reference a factory/composite role from within a redefined
					// definition. direct roles can be qualified if we want qualified names cannot
					// be overridden. 
					PermissionClause<T> result = parseDirect(fac2, sub);
					if( result != null ){
						return result;
					}
					// unrecognised direct role alias maybe
					String defn = getContext().getInitParameter(USE_RELATIONSHIP_PREFIX+fac2.getTag()+"."+sub);
					if( defn != null) {
						return parse(fac2,defn);
					}else {
						throw new UnknownRelationshipException(sub);
					}
				}
				AccessRoleProvider<A,T> arp = getContext().makeObjectWithDefault(AccessRoleProvider.class,null,base);
				if( arp != null ){
					if( arp.providesRelationship(sub)) {
						return new AccessRoleProviderPermissionClause<A, T>( arp,()->role, sub);
					}
		    		throw new UnknownRelationshipException(role+"@"+fac2.getTag());
				}
				NamedFilterProvider<T> nfp = getContext().makeObjectWithDefault(NamedFilterProvider.class, null, base);
				if( nfp != null ) {
					return new FilterPermissionClause(nfp.getNamedFilter(sub),role);
				}
			}else{
				// Non qualified name

				PermissionClause<T> result = parseDirect(fac2, role);
				if( result != null ) {
					return result;
				}
				throw new UnknownRelationshipException(role);
			}
			throw new UnknownRelationshipException(role);
		}catch(UnknownRelationshipException ur){
			if( ur.getMessage().equals(role)){
				throw ur;
			}else{
				throw new UnknownRelationshipException(role+"@"+fac2.getTag(), ur);
			}
		}finally{
			searching_roles.remove(search_tag);
		}
	}
	
	protected <T extends DataObject> PermissionClause<T> parseDirect(DataObjectFactory<T> fac2, String role) throws UnknownRelationshipException{
		if( fac2 instanceof AccessRoleProvider) {
			AccessRoleProvider<A, T> arp = (AccessRoleProvider<A, T>)fac2;
			if( arp.providesRelationship(role)) {
				return new AccessRoleProviderPermissionClause<A, T>(arp,()->fac2.getTag()+"."+role ,role);
			}
		}
		for(AccessRoleProvider arp : fac2.getComposites(AccessRoleProvider.class)) {
			if( arp.providesRelationship(role)) {
				return new AccessRoleProviderPermissionClause<A, T>( arp,()->fac2.getTag()+"."+arp.getClass().getSimpleName()+"."+role ,role);
			}
		}
	
		String defn = getContext().getInitParameter(USE_RELATIONSHIP_PREFIX+fac2.getTag()+"."+role);
		if( defn != null) {
			return parse(fac2,defn);
		}
		NamedFilterWrapper<T> wrapper = new NamedFilterWrapper<T>(fac2);
		BaseFilter<T> fil = wrapper.getNamedFilter(role);
		if( fil != null) {
			return new FilterPermissionClause<T>(fil, role);
		}
		return null;
	}
		
}
