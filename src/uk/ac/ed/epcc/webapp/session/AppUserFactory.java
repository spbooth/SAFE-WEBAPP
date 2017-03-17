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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.StandAloneFormUpdate;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.PlaceHolderFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.CreateAction;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.PatternArg;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateAction;
import uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedDataCache;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.servlet.RemoteAuthServlet;
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


public class AppUserFactory<AU extends AppUser> extends DataObjectFactory<AU> implements RequiredPageProvider<AU>,NameFinder<AU> ,RegisterTrigger<AU>
{
	
	EmailNameFinder<AU> email_finder = new EmailNameFinder<AU>(this);
	WebNameFinder<AU> web_name_finder = new WebNameFinder<AU>(this);
	RegistrationDateComposite<AU> signup_date = new RegistrationDateComposite<AU>(this);
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

	public static final Feature REQUIRE_PERSON_UPDATE_FEATURE = new Feature("require-person-update",false,"require person update if needed");
	
	public static final String ALLOW_EMAIL_FIELD ="AllowEmail";
	
    
    public class RoleFilter implements SQLFilter<AU>,JoinFilter<AU>,PatternFilter<AU>{
    	private final String role;
    	private final SQLContext ctx;
    	public RoleFilter(SQLContext ctx,String role){
    		this.ctx=ctx;
    		this.role=role;
    	}
		public String getJoin() {
			StringBuilder sb = new StringBuilder();
			sb.append(" join ");
			ctx.quote(sb, AbstractSessionService.ROLE_TABLE);
			sb.append(" on ");
			ctx.quoteQualified(sb, AbstractSessionService.ROLE_TABLE, AbstractSessionService.ROLE_FIELD);
			sb.append(" = ");
			res.addUniqueName(sb, true, false);
			return sb.toString();
		}
		public void accept(AU o) {
			
		}
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			list.add(new PatternArg(null,AbstractSessionService.ROLE_FIELD,role));
			return list;
		}
		public StringBuilder addPattern(StringBuilder sb,boolean qualify) {
			ctx.quoteQualified(sb, AbstractSessionService.ROLE_TABLE, AbstractSessionService.ROLE_FIELD);
			sb.append("=?");
			return sb;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		public <X> X acceptVisitor(FilterVisitor<X, ? extends AU> vis)
				throws Exception {
			return vis.visitPatternFilter(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		public Class<? super AU> getTarget() {
			return AppUserFactory.this.getTarget();
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
		return find(finder.getStringFinderFilter(getTarget(), email),allow_null);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getSelectors()
	 */
	@Override
	protected Map<String,Object> getSelectors() {
		Map<String,Object> selectors = super.getSelectors();
		if (selectors == null) {
			selectors = new HashMap<String,Object>();
		}
		
		return selectors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObject.Factory#getSupress()
	 */
	@Override
	protected Set<String> getSupress() {
		Set<String> supress = new HashSet<String>();

		
		
		supress.add(AppUser.UPDATED_TIME);
		return supress;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RequiredPageProvider#getRequiredPages()
	 */
    @Override
	public Set<RequiredPage<AU>> getRequiredPages(){
    	Set<RequiredPage<AU>> requiredPages= new LinkedHashSet<RequiredPage<AU>>();
    	if( REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())){
			requiredPages.add(new UpdatePersonRequiredPage());
		}
    	for(Composite<AU, ?> c : getComposites()){
    		if( c instanceof RequiredPageProvider){
    			requiredPages.addAll(((RequiredPageProvider<AU>)c).getRequiredPages());
    		}
    	}
    	return requiredPages;
    }
    public class UpdatePersonRequiredPage implements RequiredPage<AU>{

		public boolean required(SessionService<AU> user) {
			if( user instanceof ServletSessionService && ((ServletSessionService)user).isSU()){
				// Can't update if not real person
				return false;
			}
			AU person = user.getCurrentPerson();
			
			return person.needDetailsUpdate();
		}

		public FormResult getPage() {
			return new RedirectResult("/personal_update.jsp");
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
    public DataObjectItemInput<AU> getRoleInput(String role){
    	return new DataObjectInput(getRoleFilter(role));
    }


	public RoleFilter getRoleFilter(String role) {
		return new RoleFilter(res.getSQLContext(),role);
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
			}catch(Throwable t){
				ctx.error(t,"Error adding Admin to first user");
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
	
	@Override
	public Class<? super AU> getTarget(){
		return AppUser.class;
	}


	/**
	 * create a Form for editing a person.
	 * 
	 * @param f
	 *            Form to build
	 * 
	 * @param person
	 *            Person to be updated
	 * @throws Exception 
	 * 
	 */
	public void buildUpdateForm(Form f, AU person) throws Exception {
		
		StandAloneFormUpdate<AU> u = (StandAloneFormUpdate<AU>) getFormUpdate(getContext());
		
		SessionService service = person.getContext().getService(SessionService.class);
		u.buildUpdateForm("Person", f, person,service);
		if( ! service.hasRole(SessionService.ADMIN_ROLE)){
			f.removeField(ALLOW_EMAIL_FIELD);
		}
	}
	/** add Notes to be included in a signup/update form.
	 * This is included within the block element above the
	 * form.
	 * 
	 * @param cb
	 * @return
	 */
	public <CB extends ContentBuilder> CB addUpdateNotes(CB cb){
		for(Composite<AU,?> c : getComposites()){
			if( c instanceof UpdateNoteProvider){
				cb = ((UpdateNoteProvider) c).addUpdateNotes(cb);
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
		if( finder != null ){
			getLogger().debug("realm "+realm+" -> "+finder.getClass().getCanonicalName());
		}
		return finder;
	}
	
	private Map<String,AppUserNameFinder> realms=null;
	private  Map<String,AppUserNameFinder> getRealmMap(){
		if( realms == null){
			// Generate lazily only want to do this AFTER factory is constructed
			// as order of construction is complicated
			realms = new LinkedHashMap<String, AppUserNameFinder>();
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


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#findFromString(java.lang.String)
	 */
	@Override
	public final  AU findFromString(String name) {
		if( name == null || name.trim().length() == 0){
			return null;
		}
		try {
			return find(getStringFinderFilter(name));
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getCanonicalName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(AU object) {
		
		AppUserNameFinder<AU,?> finder = getRealmFinder(getDefaultRealm());
		if( finder != null ){
			assert(finder.userVisible());
			return finder.getCanonicalName(object);
		}
		// Use the first userSuppied canonical name.
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
	protected String getDefaultRealm(){
		return getContext().getInitParameter(getConfigTag()+".default_realm");
	}

	public   SQLFilter<AU> getStringFinderFilter(String name){
		return getStringFinderFilter(name, false);
	}
	public   SQLFilter<AU> getStringFinderFilter(String name,boolean require_user_supplied){
		SQLOrFilter<AU> fil = new SQLOrFilter<AU>(getTarget());
		for(  AppUserNameFinder<AU,?> finder : getRealms()){
			if( finder.userVisible() || ! require_user_supplied){
				fil.addFilter(finder.getStringFinderFilter(getTarget(), name));
			}
		}
	
		return fil;
	}
	
	
	/** Get a name where the String sort order matches the 
	 * presentation order. If we change this we need to keep getIdentifier roughly consistent.
	 * 
	 * 
	 * @param user
	 * @return String
	 */
	public String getSortName(AU user){
		return user.getName();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeFromString(java.lang.String)
	 */
	@Override
	public final AU makeFromString(String name) throws DataFault {
		AU result = findFromString(name);
		if( result != null ){
			return result;
		}
		AppUserNameFinder default_finder = getRealmFinder(getDefaultRealm());
		if(default_finder == null){
			return null;
		}
		result = makeUser();
		if( result != null){
			default_finder.setName(result, name);
			result.commit();
		}
		return result;
	}
	/** make an uncommited user suitable for automatic user creation.
	 *  return null if automatic creation is not supported.
	 * 
	 * @return
	 * @throws DataFault
	 */
	public AU makeUser() throws DataFault{
		if( getContext().getBooleanParameter("auto_create_person."+getConfigTag(), false)){
			return makeBDO();
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getDataCache()
	 */
	@Override
	public DataCache<String, AU> getDataCache() {
		return new IndexedDataCache<String, AU>(getContext()) {

			
			@SuppressWarnings("unchecked")
			@Override
			protected IndexedReference<AU> getReference(AU dat) {
				return new IndexedReference<AU>(dat.getID(),(Class<? extends IndexedProducer<AU>>) AppUserFactory.this.getClass(),getTag());
			}

			@Override
			protected AU findIndexed(String key) throws DataException {
				return makeFromString(key);
			}
		};
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		
		return new AppUser(this,res);
	}


	/**
	 * randomise a persons SAF password and send new password in an Email
	 * @throws Exception 
	 * 
	 */
	public void newSignup(AU user) throws Exception {
		
		PasswordAuthComposite<AU> comp = getComposite(PasswordAuthComposite.class);
		if( comp != null){
			// Make a new password
			String new_password = comp.firstPassword(user);
			Emailer m = new Emailer(getContext());
			m.newSignup(user, new_password);
		}
		
	}
	
	/** Get a {@link FormCreator} to use when users sign-up
	 * Optionally a name and a realm can be supplied which will be set as part of signup.
	 * This is needed for external auth when the name is known but other details need to be
	 * gathered via the form.
	 * 
	 * @param realm     String realm to set webname in (may be null)
	 * @param webname   String name to set 
	 * @return
	 */
	public final FormCreator getSignupFormCreator(String realm,String webname) {
				return new SignupFormCreator<AU>(this,realm,webname);
		
	}
	/** Form for first time visitors to self register
	 * optionally this can also provide a name for the default realm if the servlet uses external authentication.
	 * 
	 * @author spb
	 *
	 * @param <T>
	 */
	public  static class SignupFormCreator<T extends  AppUser> extends Creator<T> implements UpdateTemplate<T>  {
		/**
		 * 
		 */
		private static final String REGISTER_ACTION = " Register ";

		@Override
		public void preCommit(T dat, Form f) throws DataException {
			super.preCommit(dat, f);
			if( realm != null && realm.trim().length() > 0  && webname != null && webname.trim().length() > 0){
				dat.setRealmName(realm,webname);
			}
			getAppUserFactory().postRegister(dat);
		}
		@Override
		public void postCreate(T dat, Form f) throws Exception {
			super.postCreate(dat, f);
			getAppUserFactory().newSignup(dat);
			// Store user in session so we can bind remote-ids and
			// login if the user has a valid external id to bind.
			RemoteAuthServlet.registerNewUser(getContext(), dat);
			
		}
		/**
		 * @return
		 */
		protected AppUserFactory<T> getAppUserFactory() {
			return (AppUserFactory<T>)getFactory();
		}
		@Override
		public void setAction(String type_name,Form f) {
			// Check for a placeholder record and update that instead if it exists
			if( webname != null && ! webname.isEmpty()){
				T existing = getAppUserFactory().getRealmFinder(realm).findFromString(webname);
				if( existing != null ){
					f.addAction(REGISTER_ACTION, new UpdateAction<T>("Person", this, existing));
					return;
				}
			}
			f.addAction(REGISTER_ACTION, new CreateAction<T>(type_name,this));
		}
		String realm;
		String webname;
      
		/**
		 * 
		 * @param fac  {@link AppUserFactory}
		 * @param realm String realm to set webname in
		 * @param name  String webname from external authentication.
		 */
		public SignupFormCreator(AppUserFactory<T> fac,String realm, String name) {
			super(fac);
			this.realm=realm;
			webname = name;
		}


		@Override
		public FormResult getResult(String type_name,T dat, Form f) {
			if( getFactory().getComposite(PasswordAuthComposite.class)!=null){			
				return new MessageResult("signup_ok_password",type_name);
			}else{
				return new MessageResult("signup_ok",type_name);
			}
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory#getSupress()
		 */
		@Override
		protected Set<String> getSupress() {
			Set<String> supress = super.getSupress();
			supress.add(ALLOW_EMAIL_FIELD);
			return supress;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory#getDefaults()
		 */
		@Override
		public Map<String, Object> getDefaults() {
			Map<String, Object> defaults = super.getDefaults();
			defaults.put(ALLOW_EMAIL_FIELD, Boolean.TRUE);
			return defaults;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#postUpdate(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
		 */
		@Override
		public void postUpdate(T o, Form f, Map<String, Object> orig) throws Exception {
			postCreate(o, f);
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.UpdateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
		 */
		@Override
		public final void preCommit(T dat, Form f, Map<String, Object> orig) throws DataException {
			// forward to  creation version
			preCommit(dat, f);	
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
	
}