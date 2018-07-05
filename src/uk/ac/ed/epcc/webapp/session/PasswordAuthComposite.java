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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite.PasswordResetRequiredPage;

/** An abstract {@link Composite} that implements password authentication.
 * If a {@link AppUserFactory} contains a composite of this type then it supports password authentication
 * different sub-classes support different mechanisms
 * @author spb
 * @param <T> type of AppUSer
 *
 */

public abstract class PasswordAuthComposite<T extends AppUser> extends AppUserComposite<T, PasswordAuthComposite<T>> implements AppUserTransitionContributor , NewSignupAction<T>,RequiredPageProvider<T> {
	
	public class UpdatePasswordTransition extends AbstractFormTransition<T> implements ExtraFormTransition<T>{
		private final PasswordUpdateFormBuilder<T> builder;
		/**
		 * 
		 */
		public UpdatePasswordTransition() {
			builder= new PasswordUpdateFormBuilder<>(PasswordAuthComposite.this, true);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, T target, AppContext conn) throws TransitionException {
			
			builder.buildForm(f, target, conn);
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, T target) {
			if( mustResetPassword(target)) {
				ExtendedXMLBuilder text = cb.getText();
				text.addClass("warn");
				text.clean(getContext().expandText("Your ${service.website-name} password has expired and should be changed."));
				text.appendParent();
			}
			builder.getExtraHtml(cb, op, target);
			return cb;
		}
		
	}

	/**
	 * @param fac
	 */
	protected PasswordAuthComposite(AppUserFactory<T> fac) {
		super(fac);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#getType()
	 */
	@Override
	protected Class<? super PasswordAuthComposite<T>> getType() {
		return PasswordAuthComposite.class;
	}
	/**
	 * Check if a string matches this persons password.
	 * 
	 * @param u
	 *            AppUser to check
	 * @param password
	 *            unencrypted password to check.
	 * @return true if password matches.
	 * 
	 */
	public abstract boolean checkPassword(T u, String password);
	
	 /** Check a supplied user-name and password. return the person if it matches.
	 * 
	 * If the table is tracking an external authentication system this could create the person
	 * on first use.
	 * 
	 * @param name
	 * @param password
	 * @return user
	 * @throws DataException 
	 */
	public abstract T findByLoginNamePassword(String name, String password) throws DataException;
	/** Check a supplied user-name and password. return the person if it matches.
	 * 
	 * If the table is tracking an external authentication system this could create the person
	 * on first use.
	 * 
	 * @param name
	 * @param password
	 * @param check_fail_count set to false to ignore failed logins.
	 * @return user
	 * @throws DataException 
	 */
	public abstract T findByLoginNamePassword(String name, String password,boolean check_fail_count)
			throws DataException;
	/** Is it legal to reset the password for the current user.
	 * This can either reflect that password changes are not possible or
	 * that the user is in state where it is not legal to reset the password.
	 * 
	 * @param user or null for general test
	 * @return boolean
	 */
	public abstract boolean canResetPassword(T user);
	
	
	/** Is the user required to change their password
	 * @param user 
	 * @return boolean
	 */
	public abstract boolean mustResetPassword(T user);
	/** Change the password for a user
	 * 
	 * @param user
	 * @param password  clear-text password
	 * @throws DataFault 
	 */
	public abstract void setPassword(T user, String password) throws DataFault;
	/** lock the account so no password works.
	 * 
	 * @param user
	 */
	public abstract void lockPassword(T user) throws DataFault;
	/** Set a new randomised password for the user and send a notification email
	 * 
	 * @param user
	 * @throws Exception 
	 */
	public abstract void newPassword(T user) throws Exception;
	
	/** set a new randomised password for the user
	 * 
	 * @param user
	 * @return plain-text of new password
	 * @throws DataFault 
	 */
	public abstract String randomisePassword(T user) throws DataFault;
	/** set the initial randomised password for the user
	 * 
	 * @param user
	 * @return plain-text of new password
	 * @throws DataFault 
	 */
	public abstract String firstPassword(T user) throws DataFault;

	/** Should we show a welcome page for new users.
	 * Normally this is because the password is recorded as being an initial password.
	 * @param person
	 * @return
	 */
	public boolean doWelcome(T person) {
		return false;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.NewSignupAction#newSignup(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void newSignup(T user) throws Exception {
		// Make a new password
		String new_password = firstPassword(user);
		Emailer m = new Emailer(getContext());
		m.newSignup(user, new_password);
	}
	public static CurrentUserKey CHANGE_PASSWORD = new CurrentUserKey("Password", "Change ${service.website-name} password", "Update the password for this web-site") {

		@Override
		public boolean allowState(AppUser user, SessionService op) {
			AppUserFactory<?> fac = op.getLoginFactory();
			PasswordAuthComposite comp = fac.getComposite(PasswordAuthComposite.class);
			return comp != null && comp.canResetPassword(user);
		}
		
	};
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider provider) {
		Map<AppUserKey, Transition<AppUser>> map = new LinkedHashMap<AppUserKey, Transition<AppUser>>();
		map.put(CHANGE_PASSWORD, (Transition<AppUser>) new UpdatePasswordTransition());
		return map;
	}
	public class PasswordResetRequiredPage implements RequiredPage<T>{
    	public boolean required(SessionService<T> user){
    		T currentPerson = user.getCurrentPerson();
    		if( currentPerson == null ){
    			getLogger().error("No current person in PasswordResetRequired");
    			return false;
    		}
			return mustResetPassword(currentPerson);
    	}
    	public FormResult getPage(SessionService<T> user){
    		return new ChainedTransitionResult<T, AppUserKey>((TransitionFactory<AppUserKey, T>) AppUserTransitionProvider.getInstance(user.getContext()), user.getCurrentPerson(), CHANGE_PASSWORD);
    	}
    }
	@Override
	public Set<RequiredPage<T>> getRequiredPages() {
		LinkedHashSet<RequiredPage<T>> set = new LinkedHashSet<RequiredPage<T>>();
		// This must be the FIRST page shown
		set.add(new PasswordResetRequiredPage());
		return set;
	}
}