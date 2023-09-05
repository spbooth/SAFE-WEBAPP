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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextCached;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ScriptCustomPage;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractTargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.DataObjectTransitionProvider;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;


/** A {@link TransitionProvider} for operations on {@link AppUser}s
 * @author spb
 *
 */

public class AppUserTransitionProvider<AU extends AppUser> extends AbstractViewTransitionProvider<AU, AppUserKey<AU>> implements 
TitleTransitionProvider<AppUserKey<AU>, AU>, 
ContextCached,
DataObjectTransitionProvider<AU, AppUserFactory<AU>, AppUserKey<AU>>{
	
	/**
	 * 
	 */
	public static final String SEE_LOGIN_HISTORY_ROLE = "SeeLoginHistory";

	/** Relationship that allows a different user to edit this persons details
	 * 
	 */
	public static final String EDIT_DETAILS_ROLE = "EditDetails";

	/** Relationship that allows somebody to edit this persons roles
	 * 
	 */
	public static final String SET_ROLES_ROLE = "SetRoles";

	public static final Feature USER_SELF_UPDATE_FEATURE = new Feature("user.self.update",true,"users can update their own details");
	
	/**
	 * 
	 */
	public static final String PERSON_TRANSITION_TAG = "Person";
	public static final String VIEW_PERSON_RELATIONSHIP = "ViewPerson";
	public static final AppUserKey SU_KEY = new AppUserKey("SU","Become User","Switch to this user identity") {

		@Override
		public boolean allow(AppUser user, SessionService op) {
			if( op != null && op instanceof ServletSessionService) {
				return user != null && ((ServletSessionService)op).canSU(user);
			}
			return false;
		}
	};
	public static final AppUserKey SET_ROLE_KEY = new RoleAppUserKey("Roles", "Set roles", "Set permission roles for this user", SET_ROLES_ROLE);
	public static final AppUserKey VIEW_PERMISSIONS_KEY = new RoleAppUserKey("Permissions", "View permissions", "View explicit permissions granted to this user", SET_ROLES_ROLE);

	public static final AppUserKey QUERY_ROLE_KEY = new TargetlessAppUserKey("QueryRoles", "Query roles", "Find all users with specified roles", SET_ROLES_ROLE);
	public static final CurrentUserKey UPDATE = new CurrentUserKey("Details", "Update personal details", "Update the information we hold about you",EDIT_DETAILS_ROLE) {

		@Override
		public boolean notify(AppUser user) {
			return user.warnRequiredUpdate();
		}
		
	};
	
	public static final class SUTransition<AU extends AppUser> extends AbstractDirectTransition<AU>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AU target, AppContext c) throws TransitionException {
			SessionService sess = c.getService(SessionService.class);
			if( sess instanceof ServletSessionService) {
				((ServletSessionService)sess).su(target);
			}
			return new RedirectResult(LoginServlet.getMainPage(c));
		}
		
	}
	public static final AppUserKey ERASE = new RelationshipAppUserKey("Anonymise", "Remove personally identifiable information", "ErasePerson") {

		/* (non-Javadoc)
		 * @see uk.ac.hpcx.model.person.RoleAppUserKey#allowState(uk.ac.ed.epcc.webapp.session.AppUser)
		 */
		@Override
		protected boolean allowState(AppUser user) {
			return user.getFactory().canErase(user);
		}
		
	};
	public static final AppUserKey LOGIN_HISTORY = new BookmarkableRelationshipAppUserKey<AppUser>("LoginHistory", "See when this user logged into the website", SEE_LOGIN_HISTORY_ROLE);
	public final class EraseTransition extends AbstractDirectTransition<AU>{

		@Override
		public FormResult doTransition(AU target, AppContext c) throws TransitionException {
			try {
				fac.erasePersonalData(target);
			} catch (Exception e) {
				getLogger().error("Error erasing personal data", e);
				throw new FatalTransitionException("Error erasing personal data");
			}
			return new ViewResult(target);
		}
		
	}
	public final class LoginHistoryTransition extends AbstractDirectTransition<AU> {

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AU target, AppContext c) throws TransitionException {
			return new CustomPageResult() {
				
				@Override
				public String getTitle() {
					return "Login history for "+target.getIdentifier();
				}
				
				@Override
				public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
					cb.addHeading(1, getTitle());
					SessionService sess = conn.getService(SessionService.class);
					if( sess instanceof ServletSessionService) {
						try {
						WtmpManager man = ((ServletSessionService)sess).getWtmpManager();
						if( man != null ) {
							Table t = new Table();
							for(WtmpManager.Wtmp w : man.getLoginHistory(target)) {
								man.addTable(t, w, sess);
							}
							man.formatTable(t);
							ContentBuilder panel = cb.getPanel("scrollwrapper");
							panel.addTable(conn, t);
							panel.addParent();
						}
						}catch(Exception e) {
							getLogger().error("Error getting login history");
						}
					}
					return cb;
				}
			};
		}
		
	}
	/** A transition to view additional permissions for a user including the explicitly added roles.
	 * This is not intended to show all possible relationships but should include  permissions added explicitly to
	 * a user
	 * 
	 * @author Stephen Booth
	 *
	 */
	public final class ViewPermissionsTransition extends AbstractDirectTransition<AU> {

		@Override
		public FormResult doTransition(AU target, AppContext c) throws TransitionException {
			return new CustomPageResult() {
				
				@Override
				public String getTitle() {
					return "Permissions for "+target.getIdentifier();
				}
				
				@Override
				public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
					SessionService<AU> sess = conn.getService(SessionService.class);
					Set<String> roles = new LinkedHashSet<String>();
					for(String role : sess.getStandardRoles()) {
						if( sess.canHaveRole(target, role)) {
							String from = sess.fromRole(target, role);
							if( from != null) {
								roles.add(role+" [ via: "+from+"]");
							}else {
								roles.add(role);
							}
						}
					}
					cb.addHeading(2, "Standard roles");
					if( roles.isEmpty()) {
						cb.addText("No standard roles added");
					}else {
						ExtendedXMLBuilder text = cb.getText();
						text.addObject(roles);
						text.appendParent();
					}
					Map<String,Class> tagmap = conn.getClassMap(PermissionSummary.class);
					for(Entry<String,Class> e : tagmap.entrySet()) {
						try {
							Object s = conn.makeObject(e.getValue(), e.getKey());
							if( s != null) {
								if( s instanceof PermissionSummary) {
									((PermissionSummary<AU>) s).addPermissionSummary(cb,target);
								}else if( s instanceof DataObjectFactory) {
									for(PermissionSummary ss : ((DataObjectFactory<?>)s).getComposites(PermissionSummary.class)) {
										ss.addPermissionSummary(cb, target);
									}
								}
							}
						}catch(Exception e1) {
							getLogger().error("Error making/adding PermissionSummary",e1);
						}
					}
					
					return cb;
				}
			};
		}
		
	}
	public class QueryRoleTransition extends AbstractTargetLessTransition<AU> implements ExtraContent<AU>{

		private final class RoleTablePage extends CustomPageResult implements ScriptCustomPage {
			private final String role;

			private RoleTablePage(String role) {
				this.role = role;
			}

			@Override
			public String getTitle() {
				return "Users with role "+role;
			}

			@Override
			public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
				cb.addHeading(1, "Users with role "+role);
				SessionService<AU> sess = conn.getService(SessionService.class);
				try {
					Table<String, AU> t = fac.getPersonTable(sess, sess.getGlobalRoleFilter(role));
					if( t.hasData()) {
						t.setId("datatable");
						cb.addTable(conn, t);
					}else {
						cb.addText("No entries match");
					}
				} catch (DataFault e) {
					getLogger().error("Error building role table",e);
					cb.addText("An error occured");
				}
				return cb;
			}
			@Override
			public Set<String> getAdditionalCSS() {
				LinkedHashSet<String> result = new LinkedHashSet<>();
				result.add("${datatables.css}");
				result.add("${colVis.css}");
				result.add("${colReorder.css}");
				return result;
				
			}

			@Override
			public Set<String> getAdditionalScript() {
				LinkedHashSet<String> result = new LinkedHashSet<>();
				result.add("${jquery.script}");
				result.add("${datatables.script}");
				result.add("${colVis.script}");
				result.add("${colReorder.script}");
				result.add(
				"js/appuser_datatable.js");
				return result;
			}

		}

		@Override
		public void buildForm(Form f, AppContext c) throws TransitionException {
			f.addInput("role", "Role to query", new RoleNameInput(c.getService(SessionService.class))).setOptional(false);
			f.addAction("Search", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					String role = (String) f.get("role");
					return new RoleTablePage(role);
				}
			});
		}

		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, AU target) {
			cb.addText("This form generates a table of people with the specified role. "+
		"This uses the standard syntax for defining roles. "+
		"Role names starting with @ correspond to named filters on the person. "+
		"Roles of the form tag%relationship return any person with the relationship against any object of type tag. "+
					"Roles of the form tag%relation@name return people with the specified relationship against one of the"
					+ " objects of type tag that match the named filter name.");
			return cb;
		}
		
	}
	private final AppUserFactory<AU> fac;
	/**
	 * @param c
	 */
	public AppUserTransitionProvider(AppContext c) {
		super(c);
		SessionService sess = c.getService(SessionService.class);
		fac = sess.getLoginFactory();
	}
	@Override
	public AppUserFactory<AU> getFactory(){
		return fac;
	}
	@Override
	protected void setupTransitions() {
		super.setupTransitions();
		SessionService sess = getContext().getService(SessionService.class);
		if( fac instanceof AppUserTransitionContributor) {
			AppUserTransitionContributor<AU> cont = (AppUserTransitionContributor<AU>) fac;
			for(Entry<AppUserKey<AU>, Transition<AU>> e : cont.getTransitions(this).entrySet()) {
				addTransition( e.getKey(), e.getValue());
			}
		}
		for(AppUserTransitionContributor<AU> cont : fac.getComposites(AppUserTransitionContributor.class)) {
			for(Entry<AppUserKey<AU>, Transition<AU>> e : cont.getTransitions(this).entrySet()) {
				addTransition( e.getKey(), e.getValue());
			}
		}
		if( USER_SELF_UPDATE_FEATURE.isEnabled(getContext())) {
			addTransition(UPDATE, new UpdateDetailsTransition(this,fac));
		}
		addTransition(SET_ROLE_KEY, new SetRoleTransition<AU>());
		addTransition(VIEW_PERMISSIONS_KEY, new ViewPermissionsTransition());
		addTransition(QUERY_ROLE_KEY, new QueryRoleTransition());
		addTransition(SU_KEY, new SUTransition());
		addTransition(ERASE, new ConfirmTransition<>("Are you sure you want to anonymise this person record", new EraseTransition(), new ViewTransition()));
		if( sess instanceof ServletSessionService) {
			WtmpManager w = ((ServletSessionService)sess).getWtmpManager();
			if( w != null ) {
				addTransition(LOGIN_HISTORY, new LoginHistoryTransition());
			}
			
		}
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getTargetName()
	 */
	@Override
	public final String getTargetName() {
		return PERSON_TRANSITION_TAG;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, AU target, AppUserKey<AU> key) {
		return key.allow(target, c.getService(SessionService.class));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, AU target) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(AU target, SessionService<?> sess) {
		try(TimeClosable time = new TimeClosable(getContext(), "AppUserTransitionProvider.canView")){
		return ((SessionService)sess).isCurrentPerson(target) || ((SessionService)sess).hasRelationship((AppUserFactory)sess.getLoginFactory(), target, VIEW_PERSON_RELATIONSHIP,false);
		}
	}


	public static AppUserTransitionProvider getInstance(AppContext conn) {
		return (AppUserTransitionProvider) TransitionServlet.getProviderFromName(conn, PERSON_TRANSITION_TAG);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory#getTitle(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getTitle(AppUserKey key, AppUser target) {
		if( key == null ) {
			String identifier = target.getIdentifier();
			if( identifier != null && ! identifier.isEmpty()) {
				return identifier;
			}
			return "User details";
		}
		return getText(key);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionFactory#getHeading(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getHeading(AppUserKey key, AppUser target) {
		return getTitle(key, target);
	}

	

	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, AU target, SessionService<?> sess) {
		cb.addHeading(1, getTitle(null, target));
		AppContext c = sess.getContext();
		Map<String,Object> attr = new LinkedHashMap<>();
		((AppUserFactory)fac).addAttributes(attr, target);
		Table t = new Table();
		String col = "Value";
		t.addMap(col, attr);
		t.setKeyName("Property");
		if( t.hasData()) {
			cb.addColumn(getContext(), t, col);
		}
		String privacy_policy=c.getExpandedProperty("service.url.privacypolicy");
	    if( privacy_policy != null && ! privacy_policy.isEmpty() ){ 
	    	ExtendedXMLBuilder text = cb.getText();
	    	text.open("small");
	    	text.clean(c.expandText("All information supplied is held and processed in accordance with the ${service.name} "));
	    	text.open("a");
	    		text.attr("href",privacy_policy);
	    		text.attr("target", "_blank");
	    		text.attr("rel","noopener noreferrer external");
	    		text.clean("Personal Data and Privacy Policy");
	    	text.close();
	    	text.clean(".");
	    	text.close();
	    	text.appendParent();
	    }
		return cb;
	}
	public AppUserFactory<AU> getAppUserFactory(){
		return fac;
	}
	@Override
	public boolean useParser() {
		// stick with integer ids for AppUser as the possible parse behaviour is very complex
		return false;
	}
}