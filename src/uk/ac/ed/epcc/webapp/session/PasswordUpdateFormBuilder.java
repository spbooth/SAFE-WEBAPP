//| Copyright - The University of Edinburgh 2016                            |
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

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.inputs.PasswordInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraFormTransition;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.LoginServlet;

/** A class that build password update forms
 * @author spb
 * @param <U> type of AppUser
 *
 */
public class PasswordUpdateFormBuilder<U extends AppUser>  extends AbstractFormTransition<U> implements ExtraFormTransition<U>{
	
	/**
	 * 
	 */
	public static final String CANCEL_ACTION = "Cancel";
	/**
	 * 
	 */
	public static final String CHANGE_ACTION = " Change ";
	private static final Feature CHECK_COMPLEXITY = new Feature("password.check_complexity",true,"Perform complexity check on user generated passwords");
    /**
	 * 
	 */
	public static final String NEW_PASSWORD2 = "password2";
	/**
	 * 
	 */
	public static final String NEW_PASSWORD1 = "password1";
	/**
	 * 
	 */
	public static final String PASSWORD_FIELD = "password";

	/** Do we ask for the old web password when changing.
	 * 
	 * This is a protection against users leaving their terminal unattended
	 * and is therefore an archaic practice in the era of personal devices.
	 * enabling this feature will also prevent a user with an alternative
	 * login mechanism from resetting a forgotten password using that.
	 * 
	 * 
	 */
	public static final Feature REQUIRE_OLD_PASSWORD= new Feature("password_change.require_old_password",false,"Do website password changes need the old password from a logged in user");

	public static final Feature NOTIFY_PASSWORD_CHANGE= new Feature("password_change.notify_changes",true,"Send notifications when password reset");

	/** create the form builder.
	 * 
	 * This is used both for an externally authenticated link reset
	 * and a reset for a logged in user.
	 * In the latter case check_old should be true but this can still be
	 * supressed via a feature.
	 * 
	 * @param comp {@link PasswordAuthComposite}
	 * @param check_old do we ask for the old password (if feature enabled)
	 */
	public PasswordUpdateFormBuilder(PasswordAuthComposite<U> comp,boolean check_old) {
		super();
		this.comp = comp;
		this.check_old= REQUIRE_OLD_PASSWORD.isEnabled(comp.getContext()) && check_old;
	}
	private final PasswordAuthComposite<U> comp;
   private final boolean check_old;
    
    /** Checks the field matches the current password
     * 
     * @author spb
     *
     */
    private class MatchValidator implements FieldValidator<String>{

    	/**
		 * @param user
		 */
		public MatchValidator(U user) {
			super();
			this.user = user;
		}
		private final U user;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
		 */
		@Override
		public void validate(String password) throws ValidateException {
			if( ! comp.checkPassword(user, password)){
				throw new ValidateException("Does not match your current password");
			}
			
		}
    	
    }
    
    /** Validate the complexity of the password
     * 
     * @author spb
     *
     */
    private class ComplexityValidator implements FormValidator{
    	
    	private ValidateException decorate(ValidateException e) {
    		e.setField(NEW_PASSWORD1);
    		return e;
    	}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
		 */
		@Override
		public void validate(Form f) throws ValidateException {
		   String data = (String) f.get(NEW_PASSWORD1);
		   Set<Character> chars = new HashSet<>();
		   int neighbours=0;
		   int specials=0;
		   int numbers=0;
		   char prev = 0;
		   for(int i=0 ; i < data.length() ; i++){
			   char c = data.charAt(i);
			   if( Character.isDigit(c)){
				   numbers++;
			   }else if( ! Character.isLetter(c)){
				   specials++;
			   }
			   chars.add(Character.valueOf(c));
			   if( c == prev+1 || c+1 == prev || c == prev){
				   neighbours++;
			   }
			   prev=c;
			   
		   }
		   int min = minDiffChars();
		   if( chars.size() < min){
			   throw decorate(new ValidateException("Password must contain at least "+min+" different characters"));
		   }
		   if( (data.length() - neighbours) < minPasswordLength()){
			   throw decorate(new ValidateException("Password too simple, too many repeated or consecutive characters"));
		   }
			
		   int mindigit = minDigits();
		   if( numbers < mindigit){
			   throw decorate(new ValidateException("Password must contain at least "+mindigit+" numerical digits"));
		   }
		   int minspecial = minNonAlphaNumeric();
		   if( specials < minspecial){
			   throw decorate(new ValidateException("Password must contain at least "+minspecial+" non alpha-numeric characters"));
		   }
		}
    	
    }
    /** checks both copies of the new password are the same
     * 
     * @author spb
     *
     */
    private static class SamePasswordValidator implements FormValidator{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FormValidator#validate(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public void validate(Form f) throws ValidateException {
			String a = (String) f.get(NEW_PASSWORD1);
			String b = (String) f.get(NEW_PASSWORD2);
			if( a == null || b==null || ! a.equals(b)){
				throw new ValidateException("New Passwords don't match");
			}
		}
    	
    }
    
    private class CanChangeValidator implements FormValidator{

    	/**
		 * @param user
		 */
		public CanChangeValidator(U user) {
			super();
			this.user = user;
		}

		private final U user;
    	
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FormValidator#validate(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public void validate(Form f) throws ValidateException {
			if( ! comp.canResetPassword(user)){
				throw new ValidateException("Password changes are disabled for this account");
			}
			
		}
    	
    }
    /** Check the new password is different from the old.
     * 
     * @author spb
     *
     */
    private static class ChangeValidator implements FormValidator{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FormValidator#validate(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public void validate(Form f) throws ValidateException {
			String a = (String) f.get(PASSWORD_FIELD);
			String b = (String) f.get(NEW_PASSWORD2);
			if( a == null || b==null || a.equals(b)){
				throw new ValidateException("New Password is the same as the old one");
			}
		}
    	
    }
    
    public void buildForm(Form f,U user, AppContext conn){
    	SessionService<U> sess = conn.getService(SessionService.class);
    	boolean logged_in = sess != null && sess.haveCurrentUser();
    	if( check_old ){
    		f.addInput(PASSWORD_FIELD, "Current Password:", new PasswordInput());
    		f.getField(PASSWORD_FIELD).addValidator(new MatchValidator(user));
    		f.addValidator(new ChangeValidator());
    	}
    	f.addInput(NEW_PASSWORD1, "New Password:", makeNewInput());
    	f.addInput(NEW_PASSWORD2, "New Password (again):", makeNewInput());
    	f.addValidator(new SamePasswordValidator());
    	
    	f.addValidator(new CanChangeValidator(user));
    	if( CHECK_COMPLEXITY.isEnabled(getContext())){
    		f.addValidator(new ComplexityValidator());
    	}
    	f.addAction(CHANGE_ACTION, new UpdateAction(user));
    	if( comp.mustResetPassword(user)){
    		f.addAction(CANCEL_ACTION, new CancelLogoutAction());
    	}else if ( ! logged_in) {
    		// must be reset link. Cancelling will leave pasword as is
    		// but will servlet will strill remove the request object as well
    		f.addAction(CANCEL_ACTION, new CancelAction());
    	}
    }
    
    public class CancelAction extends FormAction{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getHelp()
		 */
		@Override
		public String getHelp() {
			return "Cancel this password change.";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getText()
		 */
		@Override
		public String getText() {
			return CANCEL_ACTION;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			return new MessageResult("password_change_cancel");
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getMustValidate()
		 */
		@Override
		public boolean getMustValidate() {
			return false;
		}
    	
    }
    public class CancelLogoutAction extends FormAction{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getHelp()
		 */
		@Override
		public String getHelp() {
			return "Cancel this password change and logout. You will still need to reset the password the next time you login.";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getText()
		 */
		@Override
		public String getText() {
			return "Cancel/Logout";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			getContext().getService(SessionService.class).logOut();
			return new MessageResult("password_change_cancel");
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getMustValidate()
		 */
		@Override
		public boolean getMustValidate() {
			return false;
		}
    	
    }
    public class UpdateAction extends FormAction{

    	/**
		 * @param user
		 */
		public UpdateAction(U user) {
			super();
			this.user = user;
		}
		private final U user;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			boolean doWelcome = comp.doWelcome(user); // setPassword will reset this
			String new_password = (String) f.get(NEW_PASSWORD1);
			try {
				comp.setPassword(user, new_password);
				user.commit();
			} catch (DataFault e) {
				throw new ActionException("Error setting password",e);
			}
			try{
				PasswordChangeListener listener = getContext().makeObjectWithDefault(PasswordChangeListener.class,null, PasswordChangeListener.PASSWORD_LISTENER_PROP);
				if( listener != null ){
					listener.setPassword(user, new_password);
				}
			}catch(Exception t){
				getLogger().error("Error calling PasswordChangeListener", t);
			}
			SessionService<U> service = getContext().getService(SessionService.class);
			if( ! service.haveCurrentUser()) {
				// This must be a reset or initial password via an email link
				service.setCurrentPerson(user);
				if (doWelcome) {
					getLogger().debug("Doing welcome page");
					// Ok, got a first time visit from a new user - send
					// them to the welcome page
					return new RedirectResult(LoginServlet.getWelcomePage(getContext()));
				}else {
					try {
						// Notify password reset
						Emailer emailer = new Emailer(getContext());
						emailer.passwordChanged(user);
					}catch(Exception t) {
						getLogger().error("Error in email notification", t);
					}
				}
			}
			return new MessageResult("password_changed");
		}
    	
    }
	
	private PasswordInput makeNewInput(){
		PasswordInput input = new PasswordInput();
		input.setMinimumLength(minPasswordLength());
		return input;
	}
	
	public int minPasswordLength() {
		return getContext().getIntegerParameter("password.min_length", 8);
	}
	public int minDiffChars() {
		int min = getContext().getIntegerParameter("password.min_diff_char", 6);
		if( min > minPasswordLength()){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		return min;
	}
	public int minDigits() {
		int min = getContext().getIntegerParameter("password.min_digits", 0);
		if( min > minPasswordLength()){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		return min;
	}
	public int minNonAlphaNumeric() {
		int min = getContext().getIntegerParameter("password.min_special", 0);
		if( min > (minPasswordLength()- minDigits())){
			return minPasswordLength(); // can't ask for more chars than minimum length
		}
		if( min < 0){
			return 0;
		}
		return min;
	}
	/**
	 * @return
	 */
	public AppContext getContext() {
		return comp.getContext();
	}
	
	public String getPasswordPolicy(){
		if( CHECK_COMPLEXITY.isEnabled(getContext())){
			StringBuilder sb = new StringBuilder();
			
			sb.append( "Passwords must be at least ");
			sb.append(Integer.toString(minPasswordLength()));
			sb.append(" characters long (not counting repeated characters and character sequences). ");
			int mindiff = minDiffChars();
			if( mindiff > 1){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(mindiff));
				sb.append(" different characters. ");
			}
			int mindigit = minDigits();
			if(mindigit > 0){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(mindigit));
				sb.append(" numerical characters. ");
			}
			int minspecial = minNonAlphaNumeric();
			if(minspecial > 0){
				sb.append("Passwords must contain at least ");
				sb.append(Integer.toString(minspecial));
				sb.append(" non alpha-numeric characters. ");
			}
			return sb.toString();
		}
		return "Passwords must be at least "+minPasswordLength()+" characters long";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
	 */
	@Override
	public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, U target) {
		cb.addText(getPasswordPolicy());
		return cb;
	}

	public Logger getLogger(){
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}
}
