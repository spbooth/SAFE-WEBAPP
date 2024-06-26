//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Span;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.CanSubmitVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput;
import uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.PlaceHolderFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.*;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.filter.IdAcceptFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.PrimaryOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLIdFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateAction;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput.Options;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedDataCache;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.history.PersonHistoryFactory;
import uk.ac.ed.epcc.webapp.model.lifecycle.ActionList;
import uk.ac.ed.epcc.webapp.model.lifecycle.LifeCycleException;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.servlet.RegisterServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;



/** A Factory for creating {@link AppUser} objects that represent users of the system.
 * 
 * If the <b>bootstrap.admin</b> {@link Feature} is set then the first user in the system will automatically be given the ADMIN role (or the role
 * specified in the <b>bootstrap-role</b> property is this is set). The role is added when the table for this factory is created not when the user is added.
 * 
 * @author spb
 *
 * @param <AU> type of {@link AppUser}
 */


public class AppUserFactory<AU extends AppUser> extends DataObjectFactory<AU> implements 
RequiredPageProvider<AU>,
NameFinder<AU> ,
RegisterTrigger<AU>, 
SummaryContributer<AU>,
AccessRoleProvider<AU, AU>,
AnonymisingFactory,
NamedFilterProvider<AU>,
Targetted<AU>
{
	/** config property for a list of stand-alone RequiredPageProviders
	 * ie those that are not the AppUserFactory or its composites.
	 * 
	 */
	public static final String STAND_ALONE_REQUIRED_PAGES = "required-pages";
	private static final String MY_SELF_RELATIONSHIP = "MySelf";
	
	//RegistrationDateComposite<AU> signup_date = new RegistrationDateComposite<AU>(this);
	/**
	 * 
	 */
	protected AppUserFactory() {
		super();
	}
	public AppUserFactory(AppContext conn,String table){
		this();
		setContext(conn, table);
	}
	public static final Feature BOOTSTRAP_ADMIN_FEATURE = new Feature("bootstrap.admin",true,"automatically give first user Admin role");
	public static final String BOOTSTRAP_ROLE_PROPERTY = "bootstrap-role";

	// Might need to set this if auto-creating users f
	public static final Feature USER_SUPPLIED_LOOKUP = new Feature("app_user.name_lookup.user_supplied_only",true,"The default NameFinder for AppUsers only considers user supplied ids");
	public static final Feature REQUIRE_PERSON_UPDATE_FEATURE = new Feature("require-person-update",false,"require person update if needed");
	 public static final Feature AUTO_COMPLETE_APPUSER_INPUT = new Preference("app_user.autocomplete_input",false,"Use auto-complete input as the default person input");
	 // optional field to allow users to be marked as never to recieve emails
	public static final String ALLOW_EMAIL_FIELD ="AllowEmail";
	
	
	public static final String ALLOW_EMAIL_FILTER = "AllowEmail";
	public static final String CAN_LOGIN_FILTER = "CanLogin";
	
    /** A {@link SQLFilter} to select {@link AppUser}s based on their roles in the role table
     * 
     * @author Stephen Booth
     *
     */
    public class RoleFilter implements SQLFilter<AU>,PatternFilter<AU>{
    	private final String roles[];
    	private final SQLContext ctx;
    	public RoleFilter(SQLContext ctx,String ... roles){
    		this.ctx=ctx;
    		this.roles=roles;
    	}
		
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			for(String role : roles) {
				addRoleParameter(list, role);
			}
			return list;
		}
		/**
		 * @param list
		 * @param test_role
		 */
		public void addRoleParameter(List<PatternArgument> list, String test_role) {
			list.add(new PatternArg(null,AbstractSessionService.ROLE_FIELD,test_role));
//			String use = getContext().getInitParameter(AbstractSessionService.USE_ROLE_PREFIX+test_role);
//			if( use != null) {
//				for(String r : use.split("\\s*,\\s*")) {
//					addRoleParameter(list, r);
//				}
//			}
		}
		public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
			sb.append(" EXISTS( SELECT 1 FROM ");
			ctx.quote(sb, AbstractSessionService.ROLE_TABLE);
			sb.append(" WHERE ");
			ctx.quoteQualified(sb, AbstractSessionService.ROLE_TABLE, AbstractSessionService.ROLE_PERSON_ID);
			sb.append(" = ");
			res.addUniqueName(sb, true, false);
			sb.append(" AND (");
			boolean seen=false;
			for(String role : roles) {
				if( seen ) {
					sb.append(" OR ");
				}
				addRolePattern(sb,role);
				seen=true;
			}
			sb.append("))");
			return sb;
		}
		
		public void addRolePattern(StringBuilder sb,String test_role) {
			ctx.quoteQualified(sb, AbstractSessionService.ROLE_TABLE, AbstractSessionService.ROLE_FIELD);
			sb.append("=?");
//			String use = getContext().getInitParameter(AbstractSessionService.USE_ROLE_PREFIX+test_role);
//			if( use != null) {
//				for(String r : use.split("\\s*,\\s*")) {
//					sb.append(" OR ");
//					addRolePattern(sb, r);
//				}
//			}
		}
	
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		@Override
		public String getTag() {
			return AppUserFactory.this.getTag();
		}
		public String toString() {
			return "RoleFilter("+String.join(",",roles)+")";
		}


		private AppUserFactory getEnclosingInstance() {
			return AppUserFactory.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Arrays.hashCode(roles);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RoleFilter other = (RoleFilter) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (!Arrays.equals(roles, other.roles))
				return false;
			return true;
		}
	
    }
   
    // Feature to control anonymise feature.
	public static final Feature ANONYMISE_DATABASE_FEATURE = new Feature("anonymise_database",false,"Can the database be anonymised for developer copies");
	
	
	
	public final AU findByEmail(String email) throws DataException {
		return findByEmail(email,false);
	}
	public final AU findByEmail(String email, boolean allow_null) throws DataException {
		AppUserNameFinder<AU,?> finder = getRealmFinder(EmailNameFinder.EMAIL);
		if( finder == null ){
			return null;
		}
		try {
		return find(finder.getStringFinderFilter( email),allow_null);
		}catch(DataNotFoundException e) {
			throw new DataNotFoundException("No AppUser found with email "+email,e);
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObject.Factory#getSupress()
	 */
	@Override
	protected Set<String> getSupress() {
		Set<String> supress = new HashSet<>();

		
		
		supress.add(AppUser.UPDATED_TIME);
		return supress;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RequiredPageProvider#getRequiredPages()
	 */
    @Override
	public Set<RequiredPage<AU>> getRequiredPages(){
    	Set<RequiredPage<AU>> requiredPages= new LinkedHashSet<>();
    	
    	for(RequiredPageProvider<AU> c : getComposites(RequiredPageProvider.class)){
    			requiredPages.addAll(c.getRequiredPages());
    	}
    	// want email verified before details
    	// for security don't want ssh key tickets if email not verified
    	if( REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())){
			requiredPages.add(new UpdatePersonRequiredPage());
		}
    	// stand alone RequiedPageProviders
    	RequiredPageProvider<AU> list = new RequiredPageProviderList<AU>(getContext(), STAND_ALONE_REQUIRED_PAGES);
    	requiredPages.addAll(list.getRequiredPages());
    	return requiredPages;
    }
    /** get a filter equivalent to {@link AppUser#canLogin()}
     * 
     * @see AppUser#canLogin()
     * @return {@link SQLFilter}
     */
    public SQLFilter<AU> getCanLoginFilter(){
    	return new GenericBinaryFilter<AU>( true);
    }
    public class UpdatePersonRequiredPage implements RequiredPage<AU>{

		public boolean required(SessionService<AU> user) {
			if( user instanceof ServletSessionService && ((ServletSessionService)user).isSU()){
				// Can't update if not real person
				return false;
			}
			AU person = user.getCurrentPerson();
			
			return needDetailsUpdate(person);
		}

		public FormResult getPage(SessionService<AU> user) {
			return new ChainedTransitionResult(AppUserTransitionProvider.getInstance(user.getContext()), user.getCurrentPerson(), AppUserTransitionProvider.UPDATE);
		}

		@Override
		public BaseFilter<AU> notifiable(SessionService<AU> sess) {
			return getWarnRefreshFilter();
		}

		@Override
		public void addNotifyText(Set<String> notices,AU person) {
			if( needDetailsUpdate(person)) {
				notices.add("Your user details need to be updated/verified");
				return;
			}
			Date d = person.nextRequiredUpdate();
			if( d == null ) {
				return;
			}
			if( person.warnRequiredUpdate()) {
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				notices.add("Your user details need to be updated/verified before "+fmt.format(d));
			}
		}
		
	}

	
    /** Does a String resolve to a known user.
     * 
     * @param username
     * @return
     * @throws DataException
     */
	public boolean isRegisteredUsername(String username)
			throws DataException {
		DataObject person = findFromString(username);
		if (person != null) {
			person.release();
			return true;
		}
		return false;
	}

	protected RoleFilter getRoleFilter(String ... roles) {
		return new RoleFilter(res.getSQLContext(),roles);
	}
    @Override
    protected final void postCreateTableSetup(AppContext c, String table){
    	postAutoTableCreateSetup(c);
    }

	protected void postAutoTableCreateSetup(AppContext ctx) {
		AbstractSessionService.setupRoleTable(ctx);
		if( BOOTSTRAP_ADMIN_FEATURE.isEnabled(ctx)){
			try{
				String roles = ctx.getInitParameter(BOOTSTRAP_ROLE_PROPERTY, SessionService.ADMIN_ROLE);
				if( roles != null && ! roles.isEmpty()){
					// allow property to contain a comma seperated list
					for(String role : roles.split("\\s*,\\s*")){
						SimpleSessionService.addRoleByID(ctx, 1, role);
					}
				}
			}catch(Exception t){
				getLogger().error("Error adding Admin to first user",t);
			}
		}
	}
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext ctx,String table) {
		TableSpecification s = new TableSpecification("PersonID");
		
		// reserve position for any email field at the start
		s.setField(EmailNameFinder.EMAIL, new PlaceHolderFieldType());
		s.setField(AppUser.UPDATED_TIME, new DateFieldType(true, null));
		s.setOptionalField(ALLOW_EMAIL_FIELD, new BooleanFieldType(false, true));
		
		return s;
	}
	

	/** do the persons details need updating.
	 * This is only used by  {@link UpdatePersonRequiredPage} and the jsp pages that
	 * update the personal details so if the corresponding feature is disabled it always return false.
	 * @return boolean
	 */
	public boolean needDetailsUpdate(AU user){
		if( ! AppUserTransitionProvider.USER_SELF_UPDATE_FEATURE.isEnabled(getContext())) {
			// user cannot update details
			return false;
		}
		if( REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())){
			Form f = new BaseForm(getContext());
			try {
				// Check if the update form would show errors as well.
				// bring this into the person method as this makes it easier
				// to call from within a jsp
				StandAloneFormUpdate<AU> u = (StandAloneFormUpdate<AU>) getFormUpdate(getContext());
				
				SessionService service = user.getContext().getService(SessionService.class);
				u.buildUpdateForm(f, user,service);
				if( ! service.hasRole(SessionService.ADMIN_ROLE)){
					f.removeField(ALLOW_EMAIL_FIELD);
				}
				if( ! CanSubmitVisitor.canSubmit(f)) {
					// As a safety check don't force a form that can't
					// be submitted
					getLogger().warn("User details form cannot be submitted for "+user.getIdentifier());
					return false;
				}
				if( ! f.validate()){
					return true;
				}
			} catch (Exception e) {
				getLogger().error("Error checking for person update",e);
			}

			if( res.hasField(AppUser.UPDATED_TIME)){
				Date last  = user.getLastTimeDetailsUpdated();
				if( last == null ){
					return true;
				}
				Calendar point = Calendar.getInstance();
				point.setTime(getContext().getService(CurrentTimeService.class).getCurrentTime());
				point.add(Calendar.DAY_OF_YEAR, -1 * requireRefreshDays());

				Date target_time = point.getTime();
				try{
					String force = getContext().getInitParameter("force_details_update_time");
					if( force != null){
						Date d = DateFormat.getInstance().parse(force);
						if( d.after(target_time)){
							target_time=d;
						}
					}
				}catch(Exception t){
					getLogger().error("Error checking force_time",t);
				}
				return last.before(target_time);
			}
		}
		return false;
	}
	
	/** number of days between required review of personal details
	 * 
	 * @return
	 */
	public int requireRefreshDays() {
		return getContext().getIntegerParameter("person_details.refresh_days", 365);
	}
	public int warnRefreshDays() {
		return (int)(0.9 * requireRefreshDays());
	}
	
	public BaseFilter<AU> getWarnRefreshFilter(){
		if( REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())) {
			Calendar thresh = Calendar.getInstance();
			thresh.setTime(getContext().getService(CurrentTimeService.class).getCurrentTime());
			thresh.add(Calendar.DAY_OF_YEAR, -1 * warnRefreshDays());
			return new SQLValueFilter<AU>( res,AppUser.UPDATED_TIME ,MatchCondition.LT , thresh.getTime());
		}
		return new FalseFilter<AU>();
	}
	
	/** a filter for people that we might ever send emails to.
	 * 
	 * @return
	 */
	public BaseFilter<AU> getEmailFilter(){
		AndFilter<AU> fil = getAndFilter();
		if( res.hasField(ALLOW_EMAIL_FIELD)) {
			fil.addFilter(new SQLValueFilter<AU>( res, ALLOW_EMAIL_FIELD, Boolean.TRUE));
		}
		for( AllowedEmailContributor<AU> c : getComposites(AllowedEmailContributor.class)) {
			fil.addFilter(c.allowedEmailFilter());
		}
		return fil;
	}
	/** add Notes to be included in a signup/update form.
	 * This is included within the block element above the
	 * form.
	 * 
	 * @param cb
	 * @return
	 */
	public <CB extends ContentBuilder> CB addUpdateNotes(CB cb,AU target){
		for(Composite<AU,?> c : getComposites()){
			if( c instanceof UpdateNoteProvider){
				cb = ((UpdateNoteProvider<AU>) c).addUpdateNotes(cb,target);
			}
		}
		return cb;
	}


	/** Get a specified {@link AppUserNameFinder}.
	 * 
	 * A null or empty string returns the first {@link AppUserNameFinder}.
	 * 
	 * @param realm
	 * @return
	 */
	public final AppUserNameFinder<AU,?> getRealmFinder(String realm){
		Map<String,AppUserNameFinder> map = getRealmMap();
		if( map == null || map.isEmpty()){
			return null;
		}
		if(realm == null || realm.trim().length()==0){
			return map.values().iterator().next();
		}
		AppUserNameFinder finder = map.get(realm);
//		if( finder != null ){
//			getLogger().debug("realm "+realm+" -> "+finder.getClass().getCanonicalName());
//		}
		return finder;
	}
	
	private Map<String,AppUserNameFinder> realms=null;
	
	/** This is a role that allows access to all personal details in the application
	 * this role can be used to short-circuit an expensive access check using {@link #VIEW_PERSON_RELATIONSHIP}
	 */
	public static final String VIEW_PERSON_ROLE = "ViewPersonalDetails";
	/** This is the relationship that allows personal details to be viewed.
	 * this should always include the {@link #VIEW_PERSON_ROLE} as well as person specific relationships
	 * 
	 */
	public static final String VIEW_PERSON_RELATIONSHIP = "ViewPerson";
	/** key for a session attibute holding a newly registered user.
	 * This is to support users being able to link external identities
	 * in the same session after registration
	 * 
	 */
	public static final String EXTAUTH_REGISTER_ID_ATTR = "EXTAUTH_REGISTER_ID_ATTR";
	private  Map<String,AppUserNameFinder> getRealmMap(){
		if( realms == null){
			// Generate lazily only want to do this AFTER factory is constructed
			// as order of construction is complicated
			realms = new LinkedHashMap<>();
			for( AppUserNameFinder finder : getComposites(AppUserNameFinder.class)){
				if( finder.active()){
					realms.put(finder.getRealm(), finder);
				}
			}
		}
		return realms;
	}
	/**
	 * @return
	 */
	public Collection<AppUserNameFinder> getRealms() {
		return getRealmMap().values();
	}
	public boolean supportsRealm(String realm) {
		return getRealmMap().containsKey(realm);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#findFromString(java.lang.String)
	 */
	@Override
	public final  AU findFromString(String name) {
		if( name == null || name.trim().length() == 0){
			return null;
		}
		name = name.trim();
		try {
			return find(getStringFinderFilter(name),true);
		}catch(MultipleResultException mr) {
			// In principle we might have duplicate ids in different realms.
			// e.g. a remote-id that is the same as a users email but the user
			// has two accounts. We really should not be using the default
			// NameFinder in these contexts so warn.
			// however we can use the supplied realms in order to break a tie
			getLogger().warn("Multiple results from person StringFilterFinder", mr);
			// Search each realm in turn
			boolean require_vis = USER_SUPPLIED_LOOKUP.isEnabled(getContext());
			for( AppUserNameFinder finder : getRealms()) {
				if( finder.userVisible() || ! require_vis) {
					AU user = (AU) finder.findFromString(name);
					if( user != null ) {
						return user;
					}
				}
			}
			
			
			return null;
		}catch(DataNotFoundException e){
			return null;
		} catch (DataException e) {
			getLogger().error("Error in name lookup", e);
			return null;
		}
	}

	/** Get the user presented label asking for a login-id.
	 * 
	 * @return
	 */
	public String getNameLabel(){
		StringBuilder sb = new StringBuilder();
		boolean seen=false;
		for( AppUserNameFinder<AU,?> finder : getRealms()){
			if( finder.userVisible() && finder.getNameLabel() != null && finder.getNameLabel().trim().length() > 0 ){
				if( seen ){
					sb.append(" or ");
				}
				sb.append(finder.getNameLabel());
				seen=true;
			}
		}
		if( ! seen ){
			return getContext().getInitParameter("name.label.default", "LoginID");
		}
		return sb.toString();
	}
	public static class  AppUserNameInput<A extends AppUser> extends NameFinderInput<A, AppUserFactory<A>> implements HTML5Input{

		

		public AppUserNameInput(AppUserFactory<A> factory, ParseFactory<A> finder, BaseFilter<A> restrict,
				BaseFilter<A> autocomplete) {
			super(factory, finder, restrict, autocomplete);
			if( useEmail()) {
				setBoxWidth(EmailInput.defaultBoxWidth(getContext()));
				setFormatHint("name@example.com");
			}
			
		}

		public AppUserNameInput(AppUserFactory<A> factory, ParseFactory<A> finder, boolean create,
				BaseFilter<A> restrict, BaseFilter<A> autocomplete) {
			super(factory, finder, create, restrict, autocomplete);
			if( useEmail()) {
				setBoxWidth(EmailInput.defaultBoxWidth(getContext()));
				setFormatHint("name@example.com");
			}
		}

		public AppUserNameInput(AppUserFactory<A> factory, ParseFactory<A> finder, Options options,
				BaseFilter<A> restrict, BaseFilter<A> autocomplete) {
			super(factory, finder, options, restrict, autocomplete);
			if( useEmail()) {
				setBoxWidth(EmailInput.defaultBoxWidth(getContext()));
				setFormatHint("name@example.com");
			}
		}

		/**
		 * @return
		 */
		public boolean useEmail() {
			return finder instanceof EmailNameFinder;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
		 */
		@Override
		public String getType() {
			if( useEmail()){
				return "email";
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput#getSuggestionText(uk.ac.ed.epcc.webapp.model.data.DataObject)
		 */
		@Override
		public String getSuggestionText(A item) {
			return getValue(item)+": "+item.getName();
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getInput()
	 */
	@Override
	public DataObjectItemInput<AU> getInput(){
		return getInput(restrictDefaultInput());
	}
	public DataObjectItemInput<AU> getInput(boolean restrict) {
		BaseFilter<AU> fil = getFinalSelectFilter();
		if( useAutoCompleteForSelect()) {
			if( restrict ) {
				return new AppUserNameInput<AU>(this,getRealmFinder(getDefaultRealm()),Options.TEXT_COMPLETE,fil,null );
			}else {
				return new AppUserNameInput<AU>(this,getRealmFinder(getDefaultRealm()),Options.TEXT_COMPLETE,null, fil );
			}
			
		}
		return super.getInput(fil,restrict);
	}
	/** Are we using an auto-complete input for {@link #getInput()}
	 * 
	 * If this is true and {@link #restrictDefaultInput()} also returns true
	 * we can afford to be more restritive in the default filter applied
	 * @return
	 */
	protected boolean useAutoCompleteForSelect() {
		return AUTO_COMPLETE_APPUSER_INPUT.isEnabled(getContext());
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		
		AppUserNameFinder<AU,?> finder = getRealmFinder(getDefaultRealm());
		if( finder != null ){
			//assert(finder.userVisible());
			return finder.getCanonicalName(object);
		}
		// Use the first userSupplied canonical name.
		for(  AppUserNameFinder<AU,?> f : getRealms()){
			if( f.userVisible()){
				return f.getCanonicalName(object);
			}
		}
		return null;
	}
	/** Get the realm to use as a default when generating a name from the factory.
	 * 
	 * @return
	 */
	public String getDefaultRealm(){
		return getContext().getInitParameter(getConfigTag()+".default_realm");
	}

	public   SQLFilter<AU> getStringFinderFilter(String name){
		return getStringFinderFilter(name, USER_SUPPLIED_LOOKUP.isEnabled(getContext()));
	}
	public   SQLFilter<AU> getStringFinderFilter(String name,boolean require_user_supplied){
		SQLOrFilter<AU> fil = getSQLOrFilter();
		for(  AppUserNameFinder<AU,?> finder : getRealms()){
			if( finder.userVisible() || ! require_user_supplied){
				fil.addFilter(finder.getStringFinderFilter( name));
			}
		}
	
		return fil;
	}
	
	@Override
	public   SQLFilter<AU> hasCanonicalNameFilter(){
		SQLOrFilter<AU> fil = getSQLOrFilter();
		for(  AppUserNameFinder<AU,?> finder : getRealms()){
			if( finder.userVisible() ){
				fil.addFilter(finder.hasCanonicalNameFilter());
			}
		}
	
		return fil;
	}
	/** Get a set of names that can be used to find the target via
	 * {@link #findFromString(String)} or {@link #getStringFinderFilter(String)}
	 * 
	 * @param target
	 * @return
	 */
	public Collection<String> getNames(AU target){
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for(  AppUserNameFinder<AU,?> finder : getRealms()){
			if( finder.userVisible() ){
				result.add(finder.getCanonicalName(target));
			}
		}
		return result;
	}
	
	/** Get a name where the String sort order matches the 
	 * presentation order. If we change this we need to keep getIdentifier roughly consistent.
	 * 
	 * Returns null or an empty string for the default sort order.
	 * @param user
	 * @return String
	 */
	public String getSortName(AU user){
		StringBuilder name = new StringBuilder();
		boolean inserted = false;
		for(SortNameContributor<AU> sn : getComposites(SortNameContributor.class)){
			if( inserted){
				name.append(" ");
			}
			if( sn.addSortName(user, name)){
				inserted=true;
			}
		}
		return name.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeFromString(java.lang.String)
	 */
	@Override
	public final AU makeFromString(String name) throws DataFault, ParseException {
		AU result = findFromString(name);
		if( result != null ){
			return result;
		}
		AppUserNameFinder default_finder = getRealmFinder(getDefaultRealm());
		if(default_finder == null){
			return null;
		}
		default_finder.validateNameFormat(name);
		result = makeUser();
		if( result != null){
			default_finder.setName(result, name);
			result.commit();
		}
		return result;
	}
	@Override
	public boolean canMakeFromString() {
		AppUserNameFinder default_finder = getRealmFinder(getDefaultRealm());
		return default_finder.canMakeFromString();
	}
	/** make an uncommited user suitable for automatic user creation.
	 *  return null if automatic creation is not supported.
	 * 
	 * @return
	 * @throws DataFault
	 */
	public AU makeUser() throws DataFault{
		return makeBDO();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getDataCache()
	 */
	@Override
	public DataCache<String, AU> getDataCache(boolean auto_create) {
		return new IndexedDataCache<String, AU>(getContext()) {

			
			@SuppressWarnings("unchecked")
			@Override
			protected IndexedReference<AU> getReference(AU dat) {
				return new IndexedReference<>(dat.getID(),(Class<? extends IndexedProducer<AU>>) AppUserFactory.this.getClass(),getTag());
			}

			@Override
			protected AU findIndexed(String key) throws DataException {
				if( auto_create){
					try {
						return makeFromString(key);
					} catch (ParseException e) {
						throw new DataFault("Bad format", e);
					}
				}else{
					return findFromString(key);
				}
			}
		};
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected AU makeBDO(Record res) throws DataFault {
		
		return (AU) new AppUser(this,res);
	}


	/** Actions to perform on a newly signed up user.
	 * 
	 * @throws Exception 
	 * 
	 */
	public void newSignup(AU user) throws Exception {
		for( NewSignupAction<AU> action : getComposites(NewSignupAction.class)) {
			action.newSignup(user);
		}
		// Will force update on first login otherwise 
		user.markDetailsUpdated();
		registerNewUser(getContext(), user);
	}
	
	/** Get a {@link FormCreator} to use when users sign-up
	
	 * @return
	 */
	public final FormCreator getSignupFormCreator() {
				return new SignupFormCreator<>(this);
		
	}
	/** Form for first time visitors to self register
	 * optionally this can also provide a name for the default realm if the servlet uses external authentication.
	 * 
	 * @author spb
	 *
	 * @param <T>
	 */
	public  static class SignupFormCreator<T extends  AppUser> extends Creator<T> implements UpdateTemplate<T>  {
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.Creator#customiseCreationForm(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public void customiseCreationForm(Form f) throws Exception {
			for(SignupCustomiser c : getAppUserFactory().getComposites(SignupCustomiser.class)){
				c.customiseSignupForm(f);
			}
		}
		/**
		 * 
		 */
		private static final String REGISTER_ACTION = " Register ";

		@Override
		public void preCommit(T dat, Form f) throws DataException, ActionException {
			super.preCommit(dat, f);
			// if we have external auth configured for the RegisterServlet and
			// and an id is generated then set the appropriate realm
			String realm = RegisterServlet.getRealm(getContext());
			if( realm != null && realm.trim().length() > 0  ) {
				ServletService serv = getContext().getService(ServletService.class);
				if( serv != null) {
					String webname = serv.getWebName();
					if( webname != null && webname.trim().length() > 0){

						dat.setRealmName(realm,webname);
					}
				}
			}
			getAppUserFactory().postRegister(dat);
		}
		@Override
		public void postCreate(T dat, Form f) throws Exception {
			super.postCreate(dat, f);
			getAppUserFactory().newSignup(dat);
			
		}
		/**
		 * @return
		 */
		protected AppUserFactory<T> getAppUserFactory() {
			return (AppUserFactory<T>)getFactory();
		}
		@Override
		public void addActions(Form f) {
			// Check for a placeholder record and update that instead if it exists
			T existing = existingUser();

			if( existing != null ){
				f.addAction(REGISTER_ACTION, new UpdateAction<>( this, existing));
				return;
			}

			f.addAction(REGISTER_ACTION, new CreateAction<>(this));
		}
		
		private T existingUser() {
			String realm=RegisterServlet.getRealm(getContext());
			if( realm == null || realm.trim().isEmpty()) {
				return null;
			}
			ServletService serv = getContext().getService(ServletService.class);
			if( serv != null) {
				String webname = serv.getWebName();
				if( webname != null && ! webname.trim().isEmpty()) {
					AppUserNameFinder<T, ?> finder = getAppUserFactory().getRealmFinder(realm);
					return finder.findFromString(webname);
				}
				
			}
			return null;
		}
		/**
		 * 
		 * @param fac  {@link AppUserFactory}
		 */
		public SignupFormCreator(AppUserFactory<T> fac) {
			super(fac);
		}


		@Override
		public FormResult getResult(T dat, Form f) {
			if( getFactory().getComposite(PasswordAuthComposite.class)!=null){			
				return new MessageResult("signup_ok_password");
			}else{
				return new MessageResult("signup_ok");
			}
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory#getSupress()
		 */
		@Override
		protected Set<String> getSupress() {
			Set<String> supress = super.getSupress();
			supress.add(ALLOW_EMAIL_FIELD);
			for(SignupCustomiser c : getAppUserFactory().getComposites(SignupCustomiser.class)){
				c.addSignupSuppress(supress);
			}
			return supress;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory#getDefaults()
		 */
		@Override
		public Map<String, Object> getCreationDefaults() {
			Map<String, Object> defaults = super.getCreationDefaults();
			defaults.put(ALLOW_EMAIL_FIELD, Boolean.TRUE);
			
			// look for defaults set in properties
			AppContext conn = getFactory().getContext();
			for (String field : getFields()) {
				FieldInfo info = getAppUserFactory().res.getInfo(field);
				String property = "service.signup_default." + field;
				if (info.isNumeric()) {
					int value = conn.getIntegerParameter(property, -1);
					if (value >= 0) {
						defaults.put(field, new Integer(value));
					}
				}
				else if (info.isString()) {
					String value = conn.getInitParameter(property);
					if (value != null) {
						defaults.put(field, value);
					}
				}
			}
			
			return defaults;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#postUpdate(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
		 */
		@Override
		public void postUpdate(T o, Form f, Map<String, Object> orig, boolean changed) throws Exception {
			postCreate(o, f);	
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
		 */
		@Override
		public final void preCommit(T dat, Form f, Map<String, Object> orig) throws DataException {
			// forward to  creation version
			try {
				preCommit(dat, f);
			} catch (ActionException e) {
				getLogger().error("Unexpected ActionException", e);
			}	
		}

		
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RegisterTrigger#mustRegister(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean mustRegister(AU user) {
		for( RegisterTrigger<AU> trigger : getComposites(RegisterTrigger.class)){
			if( trigger.mustRegister(user)){
				return true;
			}
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RegisterTrigger#postRegister(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void postRegister(AU user) {
		for( RegisterTrigger<AU> trigger : getComposites(RegisterTrigger.class)){
			trigger.postRegister(user);
		}
	}
	/**
	 * @return
	 */
	public boolean autoCreate() {
		return getContext().getBooleanParameter("auto_create_person."+getConfigTag(), false);
	}
	@Override
	public void addAttributes(Map<String, Object> attributes, AU target) {
		attributes.put("Name", target.getName());
		//attributes.put("Email",target.getEmail());
		
		
		// Catch all for remaining SummaryContibuters
		for( SummaryContributer<AU> c : getComposites(SummaryContributer.class)){
			c.addAttributes(attributes, target);
		}
		
		addUpdateAttributes(attributes, target);
		
	}
	public void addUpdateAttributes(Map<String, Object> attributes, AU target) {
		Date d = target.getLastTimeDetailsUpdated();
		if( d != null ) {
			attributes.put("Details updated",d);
		}
		if( REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())) {
			Date next = target.nextRequiredUpdate();
			if( next != null ) {
				if( target.warnRequiredUpdate()) {
					attributes.put("Next update required",new Span("warn",next.toString()));
				}else {
					attributes.put("Next update required",next);
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#validateNameFormat(java.lang.String)
	 */
	@Override
	public void validateNameFormat(String name) throws ParseException {
		for( AppUserNameFinder finder : getRealms()) {
			try {
				if( finder.userVisible()) {
					finder.validateNameFormat(name);
					// valid in one realm
					return;
				}
			}catch(ParseException e) {
				
			}
		}
		// Throw exception for the default realm
		NameFinder<AU> def = getRealmFinder(getDefaultRealm());
		def.validateNameFormat(name);
	}
	@Override
	public BaseFilter<AU> hasRelationFilter(String role, AU user) {
		if( role.equals(MY_SELF_RELATIONSHIP)) {
			return new DualFilter<AU>(new SQLIdFilter<>(res, user.getID()),
					new IdAcceptFilter(user));
		}
		return null;
	}
	@Override
	public BaseFilter<AU> personInRelationFilter(SessionService<AU> sess, String role,
			AU target) {
		if( role.equals(MY_SELF_RELATIONSHIP)) {
			return new DualFilter<AU>(new SQLIdFilter<>( res, target.getID()),
					new IdAcceptFilter(target));
		}
		return null;
	}
	@Override
	public SQLFilter<AU> personInRelationToFilter(SessionService<AU> sess, String role, SQLFilter<AU> fil) {
		if( role.equals(MY_SELF_RELATIONSHIP)) {
			return fil;
		}
		return null;
	}
	@Override
	public boolean providesRelationship(String role) {
		if( role.equals(MY_SELF_RELATIONSHIP)) {
			return true;
		}
		return false;
	}
	@Override
	protected Set<String> getOptional() {
		return new HashSet<>();
	}
	@Override
	public BaseFilter<AU> getSelectFilter() {
		/* Because input selectors can leak personal information
		 * we allways narrow selections using the view person relationship.
		 * In principal an autocomplete input might still allow a person to be
		 * selected where their id (usually email) is known.
		 */
		SessionService sess = getContext().getService(SessionService.class);
		if( sess.hasRole(AppUserFactory.VIEW_PERSON_ROLE)) {
			// shortcut the relationship lookup as
			// the role is sufficient
			return super.getSelectFilter();
		}
		// degault filter must be true as it narrows the relationship
		return getAndFilter(super.getSelectFilter(),
				sess.getRelationshipRoleFilter(this, AppUserFactory.VIEW_PERSON_RELATIONSHIP,new GenericBinaryFilter<>(true)));
	}
	@Override
	public FormUpdate<AU> getFormUpdate(AppContext c) {
		return new AppUserUpdater<AU>(this);
	}
	/** Anonymise the database.
	 * 
	 *  All passwords are made invalid except for the person anonymising the database where the
	 *  password is reset to <b>Password</B>
	 * 
	 * @throws DataFault
	 */
	@Override
	public void anonymise() throws DataFault {
		if( ! AppUserFactory.ANONYMISE_DATABASE_FEATURE.isEnabled(getContext())){
			throw new ConsistencyError("Call to disabled feature");
		}
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		SessionService sess =  getContext().getService(SessionService.class);
		AppUser currentPerson = sess == null ? null : sess.getCurrentPerson();
		try(FilterResult<AU> set = getResult(new PrimaryOrderFilter<>(res, false))){
			for(AU p : set){
               try {

				if(currentPerson == null || ! currentPerson.equals(p)){
					log.debug("Anonymise "+p.getIdentifier()+" "+p.getID());
					for(AnonymisingComposite anon : getComposites(AnonymisingComposite.class)){
						anon.anonymise(p);
					}
				}else{
					// for debugging current user just has password reset
					PasswordAuthComposite<AU> comp = getComposite(PasswordAuthComposite.class);
					if( comp != null) {
						comp.setPassword(p,"Password");
					}
				}
				p.commit();
               }catch(Exception e1) {
            	   log.error("Error anonymising person",e1);
               }
			}
		}catch(Exception e) {
			log.error("Error anonymising people",e);
		}

	}
	private ActionList<AU> getEraseListeners(){
		return new ActionList<>(getTarget(),this, "EraseActions");
	}
	
	public boolean canErase(AU person) {
		try {
			return getEraseListeners().allow(person, false);
		} catch (LifeCycleException e) {
			getLogger().error("Error in canErase",e);
			return false;
		}
	}
	/** Erase the personal data for the specified user
	 * 
	 * @param p
	 * @throws Exception 
	 */
	public void erasePersonalData(AU p) throws Exception {
		Set<String> fields = new HashSet<>();
		for(AnonymisingComposite anon : getComposites(AnonymisingComposite.class)){
			anon.anonymise(p);
			anon.addEraseFields(fields);
		}
		getEraseListeners().action(p);
		p.commit();
		AppContext conn = getContext();
		if (AppUser.PERSON_HISTORY_FEATURE.isEnabled(conn)) {
			try {
				PersonHistoryFactory<AU> fac = new PersonHistoryFactory<>(this);
				fac.wipe(p, fields);
				fac.update(p);
				fac.terminate(p);
			} catch (Exception e) {
				getLogger().error("Error updating PersonHistory", e);
				return;
			}
		}
		
	}
	@Override
	public SQLFilter<AU> getDefaultRelationshipFilter() {
		// clear this explicitly 
		// Can't default to the select filter as this is overridden to use View permission
		// which in turn will call this
		return null;
	}
	public Table<String,AU> getPersonTable(SessionService<?> sess,BaseFilter<AU> fil) throws DataFault{
		Table<String,AU> t = new Table<>();
		Collection<GlobalNamePolicy> composites = getComposites(GlobalNamePolicy.class);
		Collection<IndexTableContributor> att = getComposites(IndexTableContributor.class);
		for(AU p : getResult(fil)){
			p.addIndexTable(t);
			
			
			
			for(GlobalNamePolicy policy : composites){
				if( policy.active()){
					String label = policy.getNameLabel();
					if( label != null ){
						t.put(label,p,policy.getCanonicalName(p));
					}
				}
			}
			
			LinkedHashMap<String, Object> attr = new LinkedHashMap<>();
			for(IndexTableContributor c: att) {
				try {
				c.addIndexAttributes(attr, p);
				}catch(Exception tr) {
					getLogger().error("Error adding attribute from "+c.getClass().getCanonicalName(), tr);
				}
			}
			for(Entry<String,Object> e : attr.entrySet()) {
				t.put(e.getKey(), p, e.getValue());
			}
		}
		return t;
	}
	@Override
	public void addRelationships(Set<String> roles) {
		roles.add(MY_SELF_RELATIONSHIP);
		
	}
	@Override
	public BaseFilter<AU> getNamedFilter(String name) {
		if( name  == null || name.isEmpty()) {
			return null;
		}
		switch(name) {
		case ALLOW_EMAIL_FILTER: return getEmailFilter();
		case CAN_LOGIN_FILTER: return getCanLoginFilter();
		default: return null;
		}
	}
	@Override
	public void addFilterNames(Set<String> names) {
		names.add(ALLOW_EMAIL_FILTER);
		names.add(CAN_LOGIN_FILTER);
		
	}
	@Override
	public Class<AU> getTarget() {
		return (Class<AU>) AppUser.class;
	}
	/** Set the user that should be registered (and logged in) if the
	 * external authentication succeeds. This is for when a user registers
	 * and id given an opportunity to bind an existing id. If the binding succeeds the
	 * user has a proven id (even though the email is not verified) and can be logged in
	 * if the external auth succeeds.
	 * 
	 * If the session already contains a user this user must match the
	 * supplied arguement.
	 * 
	 * @param conn
	 * @param user
	 * @return true if binding should be offered.
	 */
	public static boolean registerNewUser(AppContext conn, AppUser user){
		SessionService sess = conn.getService(SessionService.class);
		if( sess.haveCurrentUser()){
			// This is probably a user created via a form rather than a real reggistration
			return sess.isCurrentPerson(user);
		}else{
			// Save the user-id for use in linking steps
			sess.setAttribute(EXTAUTH_REGISTER_ID_ATTR, user.getID());
			return true;
		}
	}
	
	
}