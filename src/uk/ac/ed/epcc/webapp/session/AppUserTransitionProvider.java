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
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionProvider;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;

/** A {@link TransitionProvider} for operations on {@link AppUser}s
 * @author spb
 *
 */

public class AppUserTransitionProvider extends AbstractViewTransitionProvider<AppUser, AppUserKey> {
	public static final String VIEW_PERSON_RELATIONSHIP = "ViewPerson";
	public static final AppUserKey SU_KEY = new AppUserKey("SU","Become another user") {

		@Override
		public boolean allow(AppUser user, SessionService op) {
			if( op instanceof ServletSessionService) {
				return user != null && ((ServletSessionService)op).canSU(user);
			}
			return false;
		}
	};
	
	public static final class SUTransition extends AbstractDirectTransition<AppUser>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AppUser target, AppContext c) throws TransitionException {
			SessionService sess = c.getService(SessionService.class);
			if( sess instanceof ServletSessionService) {
				((ServletSessionService)sess).su(target);
			}
			return new RedirectResult("/main.jsp");
		}
		
	}
	private final AppUserFactory<?> fac;
	/**
	 * @param c
	 */
	public AppUserTransitionProvider(AppContext c) {
		super(c);
		fac = c.getService(SessionService.class).getLoginFactory();
		for(AppUserTransitionContributor cont : fac.getComposites(AppUserTransitionContributor.class)) {
			for(Entry<AppUserKey, Transition<AppUser>> e : cont.getTransitions().entrySet()) {
				addTransition( e.getKey(), e.getValue());
			}
		}
		addTransition(SU_KEY, new SUTransition());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider#getTarget(java.lang.String)
	 */
	@Override
	public AppUser getTarget(String id) {
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
	public String getTargetName() {
		return "Person";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, AppUser target, AppUserKey key) {
		return key.allow(target, c.getService(SessionService.class));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#getSummaryContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getSummaryContent(AppContext c, X cb, AppUser target) {
		cb.addHeading(2, "Person details");
		Map<String,Object> attr = new LinkedHashMap<>();
		((AppUserFactory)fac).addAttributes(attr, target);
		Table t = new Table();
		String col = "Value";
		t.addMap(col, attr);
		t.setKeyName("Property");
		if( t.hasData()) {
			cb.addColumn(getContext(), t, col);
		}
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(AppUser target, SessionService<?> sess) {
		try {
			return ((SessionService)sess).isCurrentPerson(target) || ((SessionService)sess).hasRelationship((AppUserFactory)sess.getLoginFactory(), target, VIEW_PERSON_RELATIONSHIP);
		} catch (UnknownRelationshipException e) {
			getLogger().error("Error checking view", e);
			return false;
		}
	}

}