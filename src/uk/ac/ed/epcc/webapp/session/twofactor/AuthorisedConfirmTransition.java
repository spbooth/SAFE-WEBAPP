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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Warp
 * @author Stephen Booth
 * @param <A> type of AppUser
 * @param <T> type of transition target
 *
 */
public class AuthorisedConfirmTransition<A extends AppUser,T> extends ConfirmTransition<T> {
	/**
	 * 
	 */
	public static final String CODE = "Code";
	private final String sufficient_role;
	/**
	 * @param name
	 * @param yes
	 * @param no
	 */
	public AuthorisedConfirmTransition(String name, DirectTransition<T> yes, DirectTransition<T> no,String sufficienct_role) {
		super(name, yes, no);
		this.sufficient_role=sufficienct_role;
	}
	@Override
	public void buildForm(Form f, T target, AppContext c) throws TransitionException {
		SessionService sess = c.getService(SessionService.class);
		if( sufficient_role == null || ! sess.hasRole(sufficient_role)) {
			// check for additional auth
			AppUserFactory<A> fac = sess.getLoginFactory();
			A user = (A) sess.getCurrentPerson();
			CodeAuthComposite comp = fac.getComposite(CodeAuthComposite.class);
			// supress any that need side-channel tokens as form is made 
			// multiple times.
			if( comp != null  && ! comp.needToken() && comp.needAuth(user)) {
				f.addInput(CODE, "2fa Verification code", comp.getInput());
				f.getField(CODE).addValidator(new TokenFieldValidator(comp, user));
			}
		}
		super.buildForm(f, target, c);
	}

}
