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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.email.inputs.ServiceAllowedEmailFieldValidator;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;
import uk.ac.ed.epcc.webapp.servlet.session.token.Scopes;

/** A {@link AppUserNameFinder} to handle users canonical Email
 * 
 * The email address is required not to resolve to an existing name on the factory unless it
 * corresponds to the current object.
 * (we query the full factory in case we have a {@link NameFinder} for known aliases as well.
 * @author spb
 * @param <AU> type of AppUser
 *
 */
@Scopes(scopes={"email","impersonate"})
public class EmailNameFinder<AU extends AppUser> extends AppUserNameFinder<AU,EmailNameFinder<AU>> implements HistoryFieldContributor,SummaryContributer<AU>,AppUserTransitionContributor,AnonymisingComposite<AU>,RequiredPageProvider<AU>{

	/** property to set the email input box width
	 * 
	 */
	public static final String EMAIL_MAXWIDTH_PROP = "email.maxwidth";
	public static final String EMAIL = "Email";
	public static final String EMAIL_VERIFIED_FIELD = "EmailVerified";
	public static final Feature CHANGE_EMAIL_FEATURE = new Feature("email.change_transition",true,"Users can change their email address");
	public static final Feature ANON_TO_DUMMY_FEATURE = new Feature("email.anon_to_dummy",false,"Anonymisation generated dummy email address");
	@Override
	public TableSpecification modifyDefaultTableSpecification(
			TableSpecification spec, String table) {
		spec.setField(EMAIL, new StringFieldType(true, null, EmailInput.MAX_EMAIL_LENGTH));
		spec.setOptionalField(EMAIL_VERIFIED_FIELD, new DateFieldType(true, null) );
		try{
			spec.new Index("Email_index", true,EMAIL);
		}catch(Exception e){
			getLogger().error("Error making index",e);
		}
		return spec;
	}

	

	@Override
	public void addToHistorySpecification(TableSpecification spec) {
		spec.setField(EmailNameFinder.EMAIL, new StringFieldType(true, null, EmailInput.MAX_EMAIL_LENGTH));
		
	}



	@Override
	public Map<String, Selector> addSelectors(Map<String, Selector> selectors) {
		EmailInput email = new EmailInput();
		email.setBoxWidth(getContext().getIntegerParameter(EMAIL_MAXWIDTH_PROP, 32));
		selectors.put(EMAIL, new Selector() {

			@Override
			public Input getInput() {
				EmailInput email = new EmailInput();
				email.setBoxWidth(getContext().getIntegerParameter(EMAIL_MAXWIDTH_PROP, 32));
				return email;
			}
			
		});
		return super.addSelectors(selectors);
	}

	

	

	/**
	 * @param factory
	 */
	public EmailNameFinder(AppUserFactory<AU> factory) {
		super(factory,EMAIL);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		String email = getRecord(object).getStringProperty(EMAIL);
		if( email != null) {
			// just to be safe 
			email=email.trim();
		}
		return email;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getNameLabel()
	 */
	@Override
	public String getNameLabel() {
		return EMAIL;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getStringFinderFilter(java.lang.Class, java.lang.String)
	 */
	@Override
	public SQLFilter<AU> getStringFinderFilter(Class<? super AU> target,
			String name) {
		return new SQLValueFilter<>(getFactory().getTarget(), getRepository(), EMAIL, name);
	}


   public static FieldValidator<String> getEmailValidator(AppContext conn){
	   return conn.makeObjectWithDefault(FieldValidator.class, ServiceAllowedEmailFieldValidator.class, "email.field-validator");
   }
	
	@Override
	public void customiseForm(Form f) {
		Field field = f.getField(EMAIL);
		if( field !=null) {
			field.addValidator(new ParseFactoryValidator<>(this, null));
			field.addValidator(getEmailValidator(getContext()));
		}
	}



	@Override
	public void customiseUpdateForm(Form f, AU target, SessionService operator) {
		
		Field field = f.getField(EMAIL);
		// Current target is allowed have to replace the default validator
		field.removeValidator(new ParseFactoryValidator<>(this, null));
		field.addValidator(new ParseFactoryValidator<>(this, target));
		if( target.getEmail() != null && ! operator.hasRole(SessionService.ADMIN_ROLE)){
			// only admin can edit in update However allow if email is unset
			// This could happen if a external auth person is auto created outside of
			// normal registration
			field.lock();
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#setName(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setName(AU user, String name) {
		getRecord(user).setOptionalProperty(EMAIL, name);
		
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#active()
	 */
	@Override
	public boolean active() {
		return getRepository().hasField(EMAIL);
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#validateName(java.lang.String)
	 */
	@Override
	public void validateName(String name) throws ParseException {
		if( ! Emailer.checkAddress(name)){
			throw new ParseException("Not a valid email address: "+name);
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		String email = getCanonicalName(target);
		if( email != null){
			attributes.put(EMAIL, email);
		}
		Date d = getVerificationDate(target);
		if( d != null ) {
			attributes.put("Email last verified", d);
			Date need_by = needVerifyBy(target);
			if( need_by != null ){
				attributes.put("Next Email verification required",need_by);
			}
		}
	}



	



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingComposite#anonymise(uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void anonymise(AU target) {
		if(ANON_TO_DUMMY_FEATURE.isEnabled(getContext())) {
			setName(target, "Person"+target.getID()+"@example.com");
			return;
		}
		setName(target, null);
	}

	public static CurrentUserKey CHANGE_EMAIL = new CurrentUserKey("Email", "Update email", "Change the email address we use to contact you") {

		@Override
		public boolean notify(AppUser user) {
			EmailNameFinder comp  = (EmailNameFinder) user.getFactory().getComposite(EmailNameFinder.class);
			if( comp != null ) {
				return comp.warnVerify(user);
			}
			return false;
		}
		
	};


	public class ChangeEmailTransition extends AbstractFormTransition<AU> implements ExtraFormTransition<AU>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, AU target) {
			
			if( useEmailVerificationDate()) {
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ExtendedXMLBuilder t = cb.getText();
				t.addObject(new PreDefinedContent(op.getContext(), "verify_email"));
				t.appendParent();
				Date last = getVerificationDate(target);
				if( last != null ) {
					ExtendedXMLBuilder inner = cb.getText();
					inner.addObject(new PreDefinedContent(op.getContext(), "email_last_verified",(Object)fmt.format(last)));
					inner.appendParent();
				}
				if( warnVerify(target)) {
					ContentBuilder panel = cb.getPanel("warn");
					ExtendedXMLBuilder w = panel.getText();
					w.addObject(new PreDefinedContent(op.getContext(), "warn_verify_email",(Object)fmt.format(needVerifyBy(target))));
					w.appendParent();
					panel.addParent();
				}
			}
			ExtendedXMLBuilder ce = cb.getText();
			ce.addObject(new PreDefinedContent(op.getContext(), "change_email"));
			ce.appendParent();
			if( userVisible()) {
				AppUserFactory<AU> login_fac =  (AppUserFactory<AU>) op.getLoginFactory();
				if( login_fac.hasComposite(PasswordAuthComposite.class)){
					ExtendedXMLBuilder text = cb.getText();
					text.open("em");
					text.addClass("notice");
					PreDefinedContent m = new PreDefinedContent(op.getContext(),"change_email_password");
					m.addContent(text);
					text.close();
					text.appendParent();
				}
			}
			if( needVerify(target)) {
				ExtendedXMLBuilder text = cb.getText();
				text.addObject(new PreDefinedContent(op.getContext(), "email_verification_required",verifyRefreshDays()));
				text.appendParent();
			}
			String email = getCanonicalName(target);
			FieldValidator<String> val = getEmailValidator(getContext());
			try {
				val.validate(email);
			}catch(ValidateException e) {
				ExtendedXMLBuilder text = cb.getText();
				text.addClass("warn");
				text.clean(e.getMessage());
				text.appendParent();
			}catch(Exception x) {
				getLogger().error("Error validating email", x);
			}
			return cb;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AU target, AppContext conn) throws TransitionException {
			SessionService session_service = conn.getService(SessionService.class);
			EmailChangeRequestFactory fac = new EmailChangeRequestFactory(session_service.getLoginFactory());
			fac.makeRequestForm(target, f,useEmailVerificationDate());
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor#getTransitions(uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider)
	 */
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider provider) {
		Map<AppUserKey, Transition<AppUser>> map = new LinkedHashMap<>();
		if(CHANGE_EMAIL_FEATURE.isEnabled(getContext())) {
			map.put(CHANGE_EMAIL, (Transition<AppUser>) new ChangeEmailTransition());
		}
		return map;
	}

	/**
	 * @return
	 */
	private int verifyRefreshDays() {
		return getContext().getIntegerParameter("email_validate.refresh_days", 0);
	}
	public class VerifyEmailRequiredPage implements RequiredPage<AU>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.session.RequiredPage#required(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean required(SessionService<AU> user) {
			AU currentPerson = user.getCurrentPerson();
			String email = getCanonicalName(currentPerson);
			FieldValidator<String> val = getEmailValidator(getContext());
			try {
				val.validate(email);
			}catch(ValidateException e) {
				return true;
			}catch(Exception x) {
				getLogger().error("Error validating email", x);
			}
			return needVerify(currentPerson);
		}

		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.session.RequiredPage#getPage(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public FormResult getPage(SessionService<AU> user) {
			return new ChainedTransitionResult(AppUserTransitionProvider.getInstance(getContext()),user.getCurrentPerson(),CHANGE_EMAIL);
		}



		



		@Override
		public String getNotifyText(AU person) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date point = needVerifyBy(person);
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			if( time != null) {
				if( point.before(time.getCurrentTime())) {
					PreDefinedContent c = new PreDefinedContent(person.getContext(), "fail_verify_email");
					return c.toString();
				}
			}
			PreDefinedContent c = new PreDefinedContent(person.getContext(), "warn_verify_email",(Object)fmt.format(point));
			return c.toString();
		}



		@Override
		public BaseFilter<AU> notifiable(SessionService<AU> sess) {
			return needVerifyFilter();
		}
		
	}
	


	@Override
	public void addEraseFields(Set<String> fields) {
		fields.add(EMAIL);
	}



	@Override
	public void verified(AU user) {
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		if( time != null) {
			getRecord(user).setOptionalProperty(EMAIL_VERIFIED_FIELD,time.getCurrentTime() );
		}
	}

    public boolean useEmailVerificationDate() {
    	return getRepository().hasField(EMAIL_VERIFIED_FIELD);
    }
    public Date getVerificationDate(AU user) {
    	return getRecord(user).getDateProperty(EMAIL_VERIFIED_FIELD);
    }
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(EMAIL_VERIFIED_FIELD);
		return suppress;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RequiredPageProvider#getRequiredPages()
	 */
	@Override
	public Set<RequiredPage<AU>> getRequiredPages() {
		Set<RequiredPage<AU>> result = new HashSet<>();
		if( useEmailVerificationDate() && CHANGE_EMAIL_FEATURE.isEnabled(getContext())) {
			if( verifyRefreshDays() > 0 ) {
				result.add(new VerifyEmailRequiredPage());
			}
		}
		return result;
	}



	/**
	 * @param user
	 * @return
	 */
	private boolean needVerify(AU user) {
		int days = verifyRefreshDays();
		if( days > 0 ) {
			Date d = getVerificationDate(user);
			if( d == null ) {
				return true;
			}
			Calendar point = Calendar.getInstance();
			point.add(Calendar.DAY_OF_YEAR, -1 * days);

			Date target_time = point.getTime();
			return d.before(target_time);
		}
		return false;
	}
	
	public BaseFilter<AU> needVerifyFilter(){
		int days = verifyRefreshDays();
		if( days > 0 ) {
			
			Calendar point = Calendar.getInstance();
			// 90% there
			point.add(Calendar.DAY_OF_YEAR, (int)(-0.9 * days));

			Date target_time = point.getTime();
			return new SQLValueFilter<AU>(getFactory().getTarget(),getRepository(),EMAIL_VERIFIED_FIELD,MatchCondition.LT,target_time);
		}
		return new FalseFilter(getFactory().getTarget());
	}
	public boolean warnVerify(AU user) {
		int days = verifyRefreshDays();
		if( days > 0 ) {
			Date d = getVerificationDate(user);
			if( d == null ) {
				return true;
			}
			Calendar point = Calendar.getInstance();
			// 90% there
			point.add(Calendar.DAY_OF_YEAR, (int)(-0.9 * days));

			Date target_time = point.getTime();
			return d.before(target_time);
		}
		return false;
	}
	/** If email needs to be regularly verified when does this next need to be done by
	 * 
	 * @param user
	 * @return {@link Date} or null
	 */
	public Date needVerifyBy(AU user) {
		int days = verifyRefreshDays();
		if( days > 0 ) {
			Date d = getVerificationDate(user);
			Calendar point = Calendar.getInstance();
			if( d == null ) {
				point.setTime(getContext().getService(CurrentTimeService.class).getCurrentTime());
			}else {
				point.setTime(d);
				point.add(Calendar.DAY_OF_YEAR,  days);
			}
			return point.getTime();
		}
		return null;
	}



	@Override
	protected final Class<? super EmailNameFinder<AU>> getType() {
		// hardwire so we can sub-type but still only one
		// Also allows us to retreive the installed finder
		return EmailNameFinder.class;
	}


}