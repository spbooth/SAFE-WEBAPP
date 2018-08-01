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
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserComposite;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** super-class for {@link TwoFactorComposite}s that validate using a pin-code
 * @author Stephen Booth
 *
 */
public abstract class FormAuthComposite<AU extends AppUser> extends AppUserComposite<AU, FormAuthComposite> implements TwoFactorComposite<AU> {
   /**
	 * 
	 */
	private static final String FORM_COMPOSITE_LAST_AUTH_ATTR = "FormCompositeLastAuth";
	private int re_auth_minutes=30;
	/**
	 * @param fac
	 */
	protected FormAuthComposite(AppUserFactory<AU> fac) {
		super(fac);
		re_auth_minutes = getContext().getIntegerParameter(getConfigPrefix()+".re_auth_minutes", 30);
	}

	@Override
	public final FormResult requireAuth(AU user) {
		if( user != null && needAuth(user)) {
			// Need to ask for auth
			if( needToken()) {
				sendToken(user);
			}
			return new ChainedTransitionResult<AU, TransitionKey<AU>>(new CodeAuthTransitionProvider<AU>(getContext()), user, CodeAuthTransitionProvider.AUTHENTICATE);
		}
		return null;
	}
	/** Do we need to send tokens via a side channel
	 * 
	 * @return
	 */
	public boolean needToken() {
		return false;
	}
	/** send side-channel tokens.
	 * 
	 * @param user
	 */
	protected void sendToken(AU user) {
		
	}
	
	protected abstract boolean enabled(AU user);
	
	/** Add two factor fields to a form.
	 * The added fields should not validate unless the correct authentication is applied
	 * 
	 * @param f
	 */
	public abstract  void modifyForm(AU user,Form f);
	/** Add extra content when being shown a modified auth form.
	 * 
	 * @param cb
	 * @return
	 */
	public ContentBuilder addExtra(ContentBuilder cb) {
		return cb;
	}

	@Override
	public boolean needAuth(AU user) {
		if( ! enabled(user)) {
			return false;
		}
		SessionService<AU> sess = getContext().getService(SessionService.class);
		Date last_auth = (Date) sess.getAttribute(FORM_COMPOSITE_LAST_AUTH_ATTR);
		if( last_auth != null && re_auth_minutes > 0) {
			// did we authenticate recently
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Calendar thresh = Calendar.getInstance();
			thresh.setTime(time.getCurrentTime());
			thresh.add(Calendar.MINUTE,-re_auth_minutes );
			if( thresh.getTime().before(last_auth)) {
				// no re-auth needed
				return false;
			}
		}
		return true;
	}

	/** action to perform when authentication completed
	 * 
	 * @param ok was it a successful authentication.
	 */
	public void authenticated() {
		Logger logger = getLogger();
		SessionService<AU> sess = getContext().getService(SessionService.class);

		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		Date currentTime = getContext().getService(CurrentTimeService.class).getCurrentTime();
		sess.setAttribute(FORM_COMPOSITE_LAST_AUTH_ATTR, currentTime);
		logger.debug("Set last auth to "+currentTime);

	}
	@Override
	protected final Class<? super FormAuthComposite> getType() {
		return FormAuthComposite.class;
	}

	protected abstract String getConfigPrefix();
}
