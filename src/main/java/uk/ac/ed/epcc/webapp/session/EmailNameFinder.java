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
import java.util.LinkedHashSet;
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
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ConfirmTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.*;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;
import uk.ac.ed.epcc.webapp.model.lifecycle.ActionList;
import uk.ac.ed.epcc.webapp.servlet.session.token.Scopes;

/**
 * A {@link AppUserNameFinder} to handle users canonical Email
 * 
 * The email address is required not to resolve to an existing name on the
 * factory unless it corresponds to the current object. (we query the full
 * factory in case we have a {@link NameFinder} for known aliases as well.
 * 
 * 
 * 
 * @author spb
 * @param <AU> type of AppUser
 *
 */
@Scopes(scopes = { "email", "impersonate" })
public class EmailNameFinder<AU extends AppUser> extends AppUserNameFinder<AU, EmailNameFinder<AU>>
		implements HistoryFieldContributor, SummaryContributer<AU>, AppUserTransitionContributor,
		AnonymisingComposite<AU>, RequiredPageProvider<AU>, AllowedEmailContributor<AU>, IndexTableContributor<AU> {

	

	public static final String INVALIDATE_EMAIL_ROLE = "invalidate_email";

	protected static class EmailStatus extends BasicType<EmailStatus.Value> {
		public EmailStatus() {
			super("EmailStatus");
		}

		class Value extends BasicType.Value {
			private Value(String tag, String name) {
				super(EmailStatus.this, tag, name);
			}
		}
	}

	static final EmailStatus e_status = new EmailStatus();
	public static final EmailStatus.Value VALID = e_status.new Value("V", "Valid");
	public static final EmailStatus.Value UNKNOWN = e_status.new Value("U", "Unknown");
	public static final EmailStatus.Value INVALID = e_status.new Value("I", "InValid");
	/**
	 * property to set the email input box width
	 * 
	 */
	public static final String EMAIL_MAXWIDTH_PROP = "email.maxwidth";
	public static final String EMAIL = "Email";
	public static final String EMAIL_VERIFIED_FIELD = "EmailVerified";
	public static final Feature CHANGE_EMAIL_FEATURE = new Feature("email.change_transition", true,
			"Users can change their email address");
	public static final Feature ANON_TO_DUMMY_FEATURE = new Feature("email.anon_to_dummy", false,
			"Anonymisation generated dummy email address");

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(EMAIL, new StringFieldType(true, null, EmailInput.MAX_EMAIL_LENGTH));
		spec.setOptionalField(EMAIL_VERIFIED_FIELD, new DateFieldType(true, null));
		spec.setOptionalField(e_status.getField(), e_status.getFieldType(UNKNOWN));
		try {
			spec.new Index("Email_index", true, EMAIL);
		} catch (Exception e) {
			getLogger().error("Error making index", e);
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
		super(factory, EMAIL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		String email = getRecord(object).getStringProperty(EMAIL);
		if (email != null) {
			// just to be safe
			email = email.trim();
		}
		return email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getNameLabel()
	 */
	@Override
	public String getNameLabel() {
		return EMAIL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.session.AppUserNameFinder#getStringFinderFilter(java.
	 * lang.Class, java.lang.String)
	 */
	@Override
	public SQLFilter<AU> getStringFinderFilter(String name) {
		return new SQLValueFilter<>(getRepository(), EMAIL, name);
	}

	@Override
	public SQLFilter<AU> hasCanonicalNameFilter(){
		return new NullFieldFilter<AU>( getRepository(), EMAIL, false);
	}
	public static FieldValidator<String> getEmailValidator(AppContext conn) {
		return conn.makeObjectWithDefault(FieldValidator.class, ServiceAllowedEmailFieldValidator.class,
				"email.field-validator");
	}

	@Override
	public void customiseForm(Form f) {
		Field field = f.getField(EMAIL);
		if (field != null) {
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
		if (target.getEmail() != null && !operator.hasRole(SessionService.ADMIN_ROLE)) {
			// only admin can edit in update However allow if email is unset
			// This could happen if a external auth person is auto created outside of
			// normal registration
			field.lock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.session.AppUserNameFinder#setName(uk.ac.ed.epcc.webapp.
	 * session.AppUser, java.lang.String)
	 */
	@Override
	public void setName(AU user, String name) {
		getRecord(user).setOptionalProperty(EMAIL, name);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.session.AppUserNameFinder#active()
	 */
	@Override
	public boolean active() {
		return getRepository().hasField(EMAIL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.session.AppUserNameFinder#validateName(java.lang.String)
	 */
	@Override
	public void validateNameFormat(String name) throws ParseException {
		if (!Emailer.checkAddress(name)) {
			throw new ParseException("Not a valid email address: " + name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map,
	 * uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		String email = getCanonicalName(target);
		if (email != null) {
			attributes.put(EMAIL, email);
		}
		Date d = getVerificationDate(target);
		if (d != null) {
			attributes.put("Email last verified", d);
			Date need_by = needVerifyBy(target);
			if (need_by != null) {
				attributes.put("Next Email verification required", need_by);
			}
		} else {
			if (useEmailVerificationDate()) {
				attributes.put("Email verification", "Email address has not been verified");
			}
		}
		if (useEmailStatus()) {
			attributes.put("Email status", getStatus(target).getName());
		}
	}
	@Override
	public void addIndexAttributes(Map<String, Object> attributes, AU target) {
		// Just show email status
		if (useEmailStatus()) {
			attributes.put("Email status", getStatus(target).getName());
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingComposite#anonymise(uk.ac.ed.epcc.
	 * webapp.model.data.DataObject)
	 */
	@Override
	public void anonymise(AU target) {
		if (ANON_TO_DUMMY_FEATURE.isEnabled(getContext())) {
			setName(target, "Person" + target.getID() + "@example.com");
			return;
		}
		setName(target, null);
	}

	public static CurrentUserKey CHANGE_EMAIL = new CurrentUserKey("Email", "Update email",
			"Change the email address we use to contact you") {

		@Override
		public boolean notify(AppUser user) {
			EmailNameFinder comp = (EmailNameFinder) user.getFactory().getComposite(EmailNameFinder.class);
			if (comp != null) {
				return comp.warnVerify(user);
			}
			return false;
		}

	};
	public static RoleAppUserKey INVALIDATE_EMAIL = new RoleAppUserKey("InvalidateEmail",
			"Mark email address as bouncing/invalid", INVALIDATE_EMAIL_ROLE) {
		protected boolean allowState(AppUser user) {
			EmailNameFinder finder = (EmailNameFinder) user.getFactory().getComposite(EmailNameFinder.class);
			if( finder != null && finder.useEmailStatus()) {
				return finder.allowEmail(user);
			}
			return false;
		}
	};
	public static RoleAppUserKey CLEAR_INVALIDATE_EMAIL = new RoleAppUserKey("ClearInvalidateEmail",
			"Mark email address as unknown state", INVALIDATE_EMAIL_ROLE) {
		protected boolean allowState(AppUser user) {
			EmailNameFinder finder = (EmailNameFinder) user.getFactory().getComposite(EmailNameFinder.class);
			if( finder != null && finder.useEmailStatus()) {
				return finder.isInvalid(user);
			}
			return false;
		}
	};
	public class ChangeEmailTransition extends AbstractFormTransition<AU> implements ExtraFormTransition<AU> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc
		 * .webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService,
		 * java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, AU target) {

			if (useEmailVerificationDate()) {
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				ExtendedXMLBuilder t = cb.getText();
				t.addObject(new PreDefinedContent(op.getContext(), "verify_email",
						new Object[] { getCanonicalName(target) }));
				t.appendParent();
				Date last = getVerificationDate(target);
				if (last != null) {
					ExtendedXMLBuilder inner = cb.getText();
					inner.addObject(
							new PreDefinedContent(op.getContext(), "email_last_verified", (Object) fmt.format(last)));
					inner.appendParent();
				}
				if (needVerify(target)) {
					ContentBuilder panel = cb.getPanel("warn");
					ExtendedXMLBuilder w = panel.getText();
					w.addObject(new PreDefinedContent(op.getContext(), "fail_verify_email"));
					w.appendParent();
					panel.addParent();
				} else if (warnVerify(target)) {
					ContentBuilder panel = cb.getPanel("warn");
					ExtendedXMLBuilder w = panel.getText();
					w.addObject(new PreDefinedContent(op.getContext(), "warn_verify_email",
							(Object) fmt.format(needVerifyBy(target))));
					w.appendParent();
					panel.addParent();
				}
			}
			ExtendedXMLBuilder ce = cb.getText();
			ce.addObject(new PreDefinedContent(op.getContext(), "change_email"));
			ce.appendParent();
			if (userVisible()) {
				AppUserFactory<AU> login_fac = (AppUserFactory<AU>) op.getLoginFactory();
				if (login_fac.hasComposite(PasswordAuthComposite.class)) {
					ExtendedXMLBuilder text = cb.getText();
					text.open("em");
					text.addClass("notice");
					PreDefinedContent m = new PreDefinedContent(op.getContext(), "change_email_password");
					m.addContent(text);
					text.close();
					text.appendParent();
				}
			}
			if (needVerify(target)) {
				ExtendedXMLBuilder text = cb.getText();
				text.addObject(
						new PreDefinedContent(op.getContext(), "email_verification_required", verifyRefreshDays()));
				text.appendParent();
			}
			String email = getCanonicalName(target);
			FieldValidator<String> val = getEmailValidator(getContext());
			try {
				val.validate(email);
			} catch (ValidateException e) {
				ExtendedXMLBuilder text = cb.getText();
				text.addClass("warn");
				text.clean(e.getMessage());
				text.appendParent();
			} catch (Exception x) {
				getLogger().error("Error validating email", x);
			}
			return cb;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.
		 * epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, AU target, AppContext conn) throws TransitionException {
			SessionService session_service = conn.getService(SessionService.class);
			EmailChangeRequestFactory fac = new EmailChangeRequestFactory(session_service.getLoginFactory());
			fac.makeRequestForm(target, f, useEmailVerificationDate());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor#getTransitions(uk.
	 * ac.ed.epcc.webapp.session.AppUserTransitionProvider)
	 */
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider provider) {
		Map<AppUserKey, Transition<AppUser>> map = new LinkedHashMap<>();
		if (CHANGE_EMAIL_FEATURE.isEnabled(getContext())) {
			map.put(CHANGE_EMAIL, (Transition<AppUser>) new ChangeEmailTransition());
		}
		if (useEmailStatus()) {
			map.put(INVALIDATE_EMAIL,
					(Transition<AppUser>) new ConfirmTransition<AU>(
							"Do you want to invalidate the users email? This will supress all emails to this user.",

							new AbstractDirectTransition<AU>() {

								@Override
								public FormResult doTransition(AU target, AppContext c) throws TransitionException {
									try {
										invalidate(target);
									} catch (Exception e) {
										getLogger().error("Error invalidating email", e);
										throw new TransitionException("Internal error");
									}
									return provider.new ViewResult(target);
								}
							}, new AbstractDirectTransition<AU>() {

								@Override
								public FormResult doTransition(AU target, AppContext c) throws TransitionException {

									return provider.new ViewResult(target);
								}
							}));
			map.put(CLEAR_INVALIDATE_EMAIL, (Transition<AppUser>) new AbstractDirectTransition<AU>() {

				@Override
				public FormResult doTransition(AU target, AppContext c) throws TransitionException {
					try {
						unknown(target);
						target.commit();
					}catch(Exception e) {
						getLogger().error("Error clearing invalidate",e);
						throw new TransitionException("clear failed");
					}
					return provider.new ViewResult(target);
				}
			});
		}
		return map;
	}

	/**
	 * @return
	 */
	private int verifyRefreshDays() {
		return getContext().getIntegerParameter("email_validate.refresh_days", 0);
	}

	public class VerifyEmailRequiredPage implements RequiredPageWithAction<AU> {
		private Set<RequiredPageAction> action;
		public VerifyEmailRequiredPage() {
			action = new LinkedHashSet<RequiredPageAction>();
			String tags = getContext().getExpandedProperty("VerifyEmailRequiredPage.actions");
			if( tags != null && ! tags.isEmpty()) {
				for(String s : tags.split("\\s*,\\s*")) {
					RequiredPageAction a = getContext().makeObject(RequiredPageAction.class, s);
					if( a != null ) {
						action.add(a);
					}else {
						getLogger().error("RequiredPageAction "+s+" failed to resolve");
					}
				}
			}
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.session.RequiredPage#required(uk.ac.ed.epcc.webapp.
		 * session.SessionService)
		 */
		@Override
		public boolean required(SessionService<AU> user) {
			AU currentPerson = user.getCurrentPerson();
			if( currentPerson == null) {
				return false;
			}
			String email = getCanonicalName(currentPerson);
			FieldValidator<String> val = getEmailValidator(getContext());
			try {
				val.validate(email);
			} catch (ValidateException e) {
				return true;
			} catch (Exception x) {
				getLogger().error("Error validating email", x);
			}
			return needVerify(currentPerson);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see uk.ac.ed.epcc.webapp.session.RequiredPage#getPage(uk.ac.ed.epcc.webapp.
		 * session.SessionService)
		 */
		@Override
		public FormResult getPage(SessionService<AU> user) {
			return new ChainedTransitionResult(AppUserTransitionProvider.getInstance(getContext()),
					user.getCurrentPerson(), CHANGE_EMAIL);
		}

		@Override
		public void addNotifyText(Set<String> notices, AU person) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date point = needVerifyBy(person);
			Date last = getVerificationDate(person);
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			if (time != null || last == null) {
				if (last == null || point.before(time.getCurrentTime())) {
					PreDefinedContent c = new PreDefinedContent(person.getContext(), "fail_verify_email");
					notices.add(c.toString());
					return;
				}
			}
			if (warnVerify(person)) {
				PreDefinedContent c = new PreDefinedContent(person.getContext(), "warn_verify_email",
						(Object) fmt.format(point));
				notices.add(c.toString());
			}
		}

		@Override
		public BaseFilter<AU> notifiable(SessionService<AU> sess) {
			return needVerifyFilter(true);
		}

		@Override
		public BaseFilter<AU> triggerFilter(SessionService<AU> sess) {
			AndFilter<AU> result = getFactory().getAndFilter(applyActionFilter());
			OrFilter<AU> fil = getFactory().getOrFilter();
			for(RequiredPageAction<AU> a : action) {
				fil.addFilter(a.triggerFilter(sess));
			}
			result.addFilter(fil);
			
			return result;
		}

		@Override
		public void applyAction(AU user) {
			for(RequiredPageAction<AU> a : action) {
				a.applyAction(user);
			}
			
		}
		@Override
		public void addActionText(Set<String> notices, AU person) {
			for(RequiredPageAction<AU> a : action) {
				a.addActionText(notices, person);
			}
		}

	}

	@Override
	public void addEraseFields(Set<String> fields) {
		fields.add(EMAIL);
	}

	@Override
	public void verified(AU user) {
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		if (time != null) {
			Date d = time.getCurrentTime();
			setEmailVerifiedDate(user, d);
		}
		setStatus(user, VALID);
		try {
			user.commit();
			getVerifyActions().action(user);
		}catch(Exception e) {
			getLogger().error("Error verifying email", e);
		}
	}
	public void invalidate(AU target) {
		try {
			setStatus(target, INVALID);
			target.commit();
			getInvalidateActions().action(target);
		}catch(Exception e) {
			getLogger().error("Error invalidating email", e);
		}
	}
	public void unknown(AU target) {
		try {
			setStatus(target, UNKNOWN);
		}catch(Exception e) {
			getLogger().error("Error invalidating email", e);
		}
	}
	public void setStatus(AU user, EmailStatus.Value v) {
		getRecord(user).setOptionalProperty(e_status, v);
	}

	public EmailStatus.Value getStatus(AU user) {
		return getRecord(user).getProperty(e_status, UNKNOWN);
	}

	public void setEmailVerifiedDate(AU user, Date d) {
		getRecord(user).setOptionalProperty(EMAIL_VERIFIED_FIELD, d);
	}

	public boolean useEmailVerificationDate() {
		return getRepository().hasField(EMAIL_VERIFIED_FIELD);
	}

	public boolean useEmailStatus() {
		return getRepository().hasField(e_status.getField());
	}

	public Date getVerificationDate(AU user) {
		return getRecord(user).getDateProperty(EMAIL_VERIFIED_FIELD);
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(EMAIL_VERIFIED_FIELD);
		suppress.add(e_status.getField());
		return suppress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.session.RequiredPageProvider#getRequiredPages()
	 */
	@Override
	public Set<RequiredPage<AU>> getRequiredPages() {
		Set<RequiredPage<AU>> result = new HashSet<>();
		if (useEmailVerificationDate() && CHANGE_EMAIL_FEATURE.isEnabled(getContext())) {
			if (verifyRefreshDays() > 0) {
				result.add(new VerifyEmailRequiredPage());
			}
		}
		return result;
	}

	public boolean isEmailVerified(AU user) {
		if (!useEmailVerificationDate()) {
			return true;
		}
		return getVerificationDate(user) != null;
	}

	public SQLFilter<AU> getIsVerifiedFilter() {
		SQLAndFilter<AU> fil = getFactory().getSQLAndFilter();
		if (useEmailVerificationDate()) {
			fil.addFilter(
					new NullFieldFilter<AU>(getRepository(), EMAIL_VERIFIED_FIELD, false));
		}
		if (useEmailStatus()) {
			fil.addFilter(e_status.getFilter(getFactory(), VALID));
		}
		return fil;
	}

	/** Get a filter for any user where the email state would require the actoins
	 * to be applied
	 * 
	 * @return
	 */
	public BaseFilter<AU> applyActionFilter(){
		AppUserFactory<AU> fac = (AppUserFactory<AU>) getFactory();
		OrFilter<AU> fil = fac.getOrFilter();
		if( useEmailStatus()) {
			// always apply if email invalid
			fil.addFilter(e_status.getFilter(fac, INVALID));
		}
		if( useEmailVerificationDate()) {
			// past the required verification date
			fil.addFilter(needVerifyFilter(false));
		}
		return fil;
	}
	
	/**
	 * Does the users email need to be verified
	 * 
	 * @param user
	 * @return
	 */
	public boolean needVerify(AU user) {
		if (emailMarkedInvalid(user)) {
			return true;
		}
		int days = verifyRefreshDays();
		if (days > 0) {
			Date d = getVerificationDate(user);
			if (d == null) {
				return true;
			}
			Calendar point = Calendar.getInstance();
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			point.setTime(time.getCurrentTime());
			point.add(Calendar.DAY_OF_YEAR, -1 * days);

			Date target_time = point.getTime();
			return d.before(target_time);
		}
		return false;
	}

	public boolean emailMarkedInvalid(AU user) {
		return getStatus(user) == INVALID;
	}

	public BaseFilter<AU> needVerifyFilter(boolean apply_grace) {
		int days = verifyRefreshDays();
		if (days > 0 && useEmailVerificationDate()) {

			Calendar point = Calendar.getInstance();
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			point.setTime(time.getCurrentTime());
			// 90% there
			if( apply_grace) {
				days = (int)(0.9 * days);
			}
			point.add(Calendar.DAY_OF_YEAR, - days);

			Date target_time = point.getTime();
			return new SQLValueFilter<AU>( getRepository(), EMAIL_VERIFIED_FIELD,
					MatchCondition.LT, target_time);
		}
		return new FalseFilter();
	}

	public boolean warnVerify(AU user) {
		int days = verifyRefreshDays();
		if (days > 0) {
			Date d = getVerificationDate(user);
			if (d == null) {
				return true;
			}
			Calendar point = Calendar.getInstance();
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			point.setTime(time.getCurrentTime());
			// 90% there
			point.add(Calendar.DAY_OF_YEAR, (int) (-0.9 * days));

			Date target_time = point.getTime();
			return d.before(target_time);
		}
		return false;
	}

	/**
	 * If email needs to be regularly verified when does this next need to be done
	 * by
	 * 
	 * @param user
	 * @return {@link Date} or null
	 */
	public Date needVerifyBy(AU user) {
		int days = verifyRefreshDays();
		if (days > 0) {
			Date d = getVerificationDate(user);
			Calendar point = Calendar.getInstance();
			if (d == null) {
				point.setTime(getContext().getService(CurrentTimeService.class).getCurrentTime());
			} else {
				point.setTime(d);
				point.add(Calendar.DAY_OF_YEAR, days);
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

	@Override
	public BaseFilter<AU> allowedEmailFilter() {
		if (useEmailStatus()) {
			return e_status.getExcludeFilter(getFactory(), INVALID);
		}
		return null;
	}

	@Override
	public boolean allowEmail(AU user) {
		return getStatus(user) != INVALID;
	}
	public boolean isInvalid(AU user) {
		return getStatus(user) == INVALID;
	}
	public ActionList<AU> getVerifyActions() {
		return new ActionList<AU>(getAppUserFactory().getTarget(),getAppUserFactory(), "email-verified");
	}
	public ActionList<AU> getInvalidateActions() {
		return new ActionList<AU>(getAppUserFactory().getTarget(),getAppUserFactory(), "email-invalidated");
	}
	@Override
	public void postUpdate(AU o, Form f, Map<String, Object> orig, boolean changed) throws DataException {
		String old_email = (String) orig.get(EMAIL);
		String new_email = (String) f.get(EMAIL);
		if( ! old_email.equalsIgnoreCase(new_email)) {
			// email address has been updated using a form
			// e.g. an admin
			if( useEmailStatus()) {
				// don't know the status of the new email
				unknown(o);
				o.commit();
			}
		}
	}
	
}