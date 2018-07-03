//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.session.twofactor;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserComposite;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** super-class for {@link TwoFactorComposite}s that validate using a pin-code
 * @author Stephen Booth
 *
 */
public abstract class CodeAuthComposite<AU extends AppUser,T> extends AppUserComposite<AU, CodeAuthComposite> implements TwoFactorComposite<AU> {
   /**
	 * 
	 */
	private static final String CODE_COMPOSITE_LAST_AUTH_ATTR = "CodeCompositeLastAuth";
	private int re_auth_minutes=30;
	/**
	 * @param fac
	 */
	protected CodeAuthComposite(AppUserFactory<AU> fac) {
		super(fac);
	}

	@Override
	public FormResult requireAuth(AU user) {
		if( needAuth(user)) {
			// Need to ask for auth
			return new ChainedTransitionResult<AU, TransitionKey<AU>>(new CodeAuthTransitionProvider<AU>(getContext()), user, CodeAuthTransitionProvider.AUTHENTICATE);
		}
		return null;
	}
	
	protected abstract boolean enabled(AU user);
	
	public abstract  Input<T> getInput();

	public abstract boolean verify(AU user,T value);
	
	public ContentBuilder addExtra(ContentBuilder cb) {
		return cb;
	}

	@Override
	public boolean needAuth(AU user) {
		if( ! enabled(user)) {
			return false;
		}
		SessionService<AU> sess = getContext().getService(SessionService.class);
		Date last_auth = (Date) sess.getAttribute(CODE_COMPOSITE_LAST_AUTH_ATTR);
		if( last_auth != null ) {
			// did we authenticate recently
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Calendar thresh = Calendar.getInstance();
			thresh.setTime(time.getCurrentTime());
			thresh.add(-re_auth_minutes, Calendar.MINUTE);
			if( thresh.getTime().before(last_auth)) {
				// no re-auth needed
				return false;
			}
		}
		return true;
	}

	/** action to perform when authentication completes
	 * 
	 * @param ok
	 */
	public void authenticated(boolean ok) {
		SessionService<AU> sess = getContext().getService(SessionService.class);
		if( ok ) {
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			sess.setAttribute(CODE_COMPOSITE_LAST_AUTH_ATTR, getContext().getService(CurrentTimeService.class).getCurrentTime());
		}else {
			sess.removeAttribute(CODE_COMPOSITE_LAST_AUTH_ATTR);
		}
	}
	@Override
	protected final Class<? super CodeAuthComposite> getType() {
		return CodeAuthComposite.class;
	}

}
