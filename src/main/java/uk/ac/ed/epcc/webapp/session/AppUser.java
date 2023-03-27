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
/*
 * Created on 26-Apr-2005 by spb
 *
 */
package uk.ac.ed.epcc.webapp.session;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.history.PersonHistoryFactory;


/**
 * AppUser Generic Object representing a user of the Web application potentially sub-classed
 * by actual Webapps.
 * 
 * An AppUser can support names defined in various realms (defined using {@link AppUserNameFinder} objects.
 * 
 * 
 * @author spb
 * 
 */
public class AppUser extends DataObject implements java.security.Principal, Owned{

	public static final String UPDATED_TIME="Updated";

	public static final Feature PERSON_HISTORY_FEATURE = new Feature("person.history",true,"Keep history table of person state");
	
	private AppUserFactory<?> factory;

	private String logname = null;

	public AppUser(AppUserFactory factory, Repository.Record res) {
		super(res);
		this.factory=factory;
	}
	

	public AppUserFactory getFactory(){
		return factory;
	}
	/**
	 * Is this user allowed to login applications should override this method to
	 * implement the necessary logic for this.
	 * 
	 * This will also disable forgotten password requests.
	 * 
	 * 
	 * @see AppUserFactory#getCanLoginFilter()
	 * @see #canReregister()
	 * @return boolean true for permitted.
	 */
	public boolean canLogin() {
		return true;
	}
	/** Return true if the account is not permitted to login but a forgotten password 
	 * request will re-register the user.
	 * 
	 * @return
	 */
	public boolean canReregister(){
		return false;
	}
	
	/** Re-register a retired account
	 * 
	 */
	public void reRegister(){
		
	}
	
	
	public String getRealmName(String realm){
		AppUserNameFinder finder = getFactory().getRealmFinder(realm);
		if( finder != null){
			return finder.getCanonicalName(this);
		}
		return null;
	}
	

	/** get the Email address for this AppUser if known.
	 * 
	 * 
	 * @return String
	 */
	public String getEmail() {
		return getRealmName(EmailNameFinder.EMAIL);
	}

	public boolean allowEmail(){
		if( ! record.getBooleanProperty(AppUserFactory.ALLOW_EMAIL_FIELD, true)) {
			return false;
		}
		AppUserFactory<?> f = getFactory();
		for(AllowedEmailContributor c : f.getComposites(AllowedEmailContributor.class)){
			if( ! c.allowEmail(this)) {
				return false;
			}
		}
		return true;
	}
	public void setEmailsAllowed(boolean value){
		record.setOptionalProperty(AppUserFactory.ALLOW_EMAIL_FIELD, value);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#getIdentifier()
	 */
	@Override
	public String getIdentifier(int max_length) {
		return getFactory().getCanonicalName(this);
	}

	/** get the presentation name of the user
	 * 
	 * 
	 * By default this looks for a {@link Composite} that implements {@link NameComposite}
	 * then falls back to the default realm name.
	 * This method can also be overridden to directly generate a more friendly presentation name
	 * 
	 * @return String
	 */
	public String getName(){
		for(NameComposite nc : ((AppUserFactory<?>)getFactory()).getComposites(NameComposite.class)){
			String name = nc.getPresentationName(this);
			if( name != null){
				return name;
			}
		}
		String canonicalName = getFactory().getCanonicalName(this);
		if( canonicalName != null) {
			return canonicalName;
		}
		return "Anonymous-"+getID();
	}
	
	
	/** Version of getName where the String sort order matches the 
	 * presentation order. If we change this we need to keep getIdentifier roughly consistent.
	 * 
	 * @return String
	 */
	public final String getSortName() {
		return getFactory().getSortName(this);
	}
	
	
	
    
    public void setRealmName(String realm, String name){
    	AppUserNameFinder finder = getFactory().getRealmFinder(realm);
    	if( finder !=null ){
    		finder.setName(this, name);
    	}
    }
   /** change email address and commit change.
    * 
    * @param email
    * @throws DataFault
    */
    public void setEmail(String email) throws DataFault{
    	setRealmName(EmailNameFinder.EMAIL, email);
    	commit();
    }
		
	public Date getLastTimeDetailsUpdated(){
		return record.getDateProperty(UPDATED_TIME);
	}
	
	/** Get the next date that personal details are required to be updated
	 * 
	 * @return Date or null
	 */
	public Date nextRequiredUpdate() {
		return updatePlusOffset(getFactory().requireRefreshDays());
	}
	public boolean warnRequiredUpdate() {
		Date w = updatePlusOffset(getFactory().warnRefreshDays());
		if( w == null ) {
			return false;
		}
		return getContext().getService(CurrentTimeService.class).getCurrentTime().after(w);
	}
	private Date updatePlusOffset(int offset) {
		if( AppUserFactory.REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())){
			Date last  = getLastTimeDetailsUpdated();
			if( last == null ){
				return getContext().getService(CurrentTimeService.class).getCurrentTime();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			cal.add(Calendar.DAY_OF_YEAR,  offset);
			return cal.getTime();
		}
		return null;
	}
	public void markDetailsUpdated(){
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		Date d = time.getCurrentTime();
		setDetailsUpdated(d);
	}


	public void setDetailsUpdated(Date d) {
		record.setOptionalProperty(UPDATED_TIME, d);
	}
	
	


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObject#pre_commit(boolean)
	 */
	@Override
	protected void pre_commit(boolean dirty) throws DataFault {
		for(AppUserCommitObserver c : factory.getComposites(AppUserCommitObserver.class)){
			c.pre_commit(this,dirty);
		}
	}
	protected void post_commit(boolean changed)throws DataFault{
		for(AppUserCommitObserver c : factory.getComposites(AppUserCommitObserver.class)){
			c.post_commit(this,changed);
		}
	}


	
	/**
	 * 
	 * @param role
	 * @return
	 */
	public boolean checkStateRole(String role){
		return false;
	}
	/** Extension point for when updates to person state may need to be
	 * notified to external parties.
	 * 
	 * The field string is a tag to identify the type of update being notified.
	 * This allows some degree of additional filtering in the sub-class.
	 * Use the db field triggering the update where this makes sense and
	 * pass the old/new values
	 * @param <X> value type
	 * 
	 * @param field  type of update
	 * @param extra  text of update
	 * @param prev   previous value
	 * @param curr   relacement value
	 */
	public <X> void notifyChange(String field, String extra, X prev, X curr) {
		
	}


	public void historyUpdate() {
		AppContext conn = getContext();
		if (PERSON_HISTORY_FEATURE.isEnabled(conn)) {
			try {
				PersonHistoryFactory fac = new PersonHistoryFactory(factory);
			
				fac.update(this);
			} catch (Exception e) {
				conn.error(e, "Error updating PersonHistory");
				return;
			}
		}
	}
	
	public void historyTerminate() {
		AppContext conn = getContext();
		if (PERSON_HISTORY_FEATURE.isEnabled(conn)) {
			try {
				PersonHistoryFactory fac = new PersonHistoryFactory(factory);
			
				fac.terminate(this);
			} catch (Exception e) {
				conn.error(e, "Error terminating PersonHistory");
				return;
			}
		}
	}
	
	/** Add this {@link AppUser} to an index table with rows keyed by {@link AppUser}.
	 * 
	 * @see AppUserFactory#getPersonTable(SessionService, uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter)
	 * 
	 * @param tab
	 */
	public void addIndexTable(Table tab) {
		tab.put("Email", this, getEmail());
	}
	
}