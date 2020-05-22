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
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextCached;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TitleTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;


/** A {@link TransitionProvider} for operations on {@link AppUser}s
 * @author spb
 *
 */

public class AppUserTransitionProvider<AU extends AppUser> extends AbstractViewTransitionProvider<AU, AppUserKey<AU>> implements TitleTransitionProvider<AppUserKey<AU>, AU>, ContextCached {
	
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
	public static final AppUserKey LOGIN_HISTORY = new RelationshipAppUserKey<AppUser>("LoginHistory", "See when this user logged into the website", SEE_LOGIN_HISTORY_ROLE);
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
	public final class LoginHistoryTransition extends AbstractDirectTransition<AU>{

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
	private final AppUserFactory<AU> fac;
	/**
	 * @param c
	 */
	public AppUserTransitionProvider(AppContext c) {
		super(c);
		SessionService sess = c.getService(SessionService.class);
		fac = sess.getLoginFactory();
		for(AppUserTransitionContributor<AU> cont : fac.getComposites(AppUserTransitionContributor.class)) {
			for(Entry<AppUserKey<AU>, Transition<AU>> e : cont.getTransitions(this).entrySet()) {
				addTransition( e.getKey(), e.getValue());
			}
		}
		if( USER_SELF_UPDATE_FEATURE.isEnabled(c)) {
			addTransition(UPDATE, new UpdateDetailsTransition(this,fac));
		}
		addTransition(SET_ROLE_KEY, new SetRoleTransition<AU>());
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
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public AU getTarget(String id) {
		try {
			return fac.find(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			return null;
		} catch (DataException e) {
			getLogger().error("Error getting AppUser",e );
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getID(java.lang.Object)
	 */
	@Override
	public String getID(AppUser target) {
		return Integer.toString(target.getID());
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionFactory#getText(uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey)
	 */
	@Override
	public String getText(AppUserKey key) {
		return getContext().expandText(key.getText());
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
			return target.getIdentifier();
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
		cb.addHeading(2, target.getIdentifier());
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
	    	text.clean(c.expandText("All information supplied is held and processed in accordance with the ${service.name} Personal Data and Privacy Policy.\n" + 
	    			"You can find full details "));
	    	text.open("a");
	    		text.attr("href",privacy_policy);
	    		text.attr("target", "_blank");
	    		text.clean("here");
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
}