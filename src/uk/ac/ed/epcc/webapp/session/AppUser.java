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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUserFactory.UpdatePersonRequiredPage;

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
public class AppUser extends DataObject implements java.security.Principal{

	public static final String UPDATED_TIME="Updated";
	
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
	 * Is this user allowed to login applications should overide this method to
	 * implement the necessary logic for this.
	 * 
	 * This will also disable forgotten password requests.
	 * 
	 * @return boolean true for permitted.
	 */
	public boolean canLogin() {
		return true;
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
		return record.getBooleanProperty(AppUserFactory.ALLOW_EMAIL_FIELD, true);
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

	/**
	 * get the name of the user
	 * 
	 * @return String
	 */
	public String getName(){
		return getFactory().getCanonicalName(this);
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
	public void markDetailsUpdated(){
		record.setOptionalProperty(UPDATED_TIME, new Date());
	}
	/** do the persons details need updating.
	 * This is only used by  {@link UpdatePersonRequiredPage} and the jsp pages that
	 * update the personal details so if the corresponding feature is disabled it always return false.
	 * @return boolean
	 */
	public boolean needDetailsUpdate(){
		if( AppUserFactory.REQUIRE_PERSON_UPDATE_FEATURE.isEnabled(getContext())){
			Form f = new BaseForm(getContext());
			try {
				// Check if the update form would show errors as well.
				// bring this into the person method as this makes it easier
				// to call from within a jsp
				((AppUserFactory<AppUser>)factory).buildUpdateForm(f, this);
				if( f.validate()){
					return false;
				}
				return true;
			} catch (Exception e) {
				getContext().error(e,"Error checking for person update");
			}

			if( record.getRepository().hasField(UPDATED_TIME)){
				Date last  = getLastTimeDetailsUpdated();
				if( last == null ){
					return true;
				}
				Calendar point = Calendar.getInstance();
				point.add(Calendar.DAY_OF_YEAR, -1 * getContext().getIntegerParameter("person_details.refresh_days", 365));

				Date target_time = point.getTime();
				try{
					String force = getContext().getInitParameter("force_details_update_time");
					if( force != null){
						Date d = DateFormat.getInstance().parse(force);
						if( d.after(target_time)){
							target_time=d;
						}
					}
				}catch(Throwable t){
					getContext().error(t,"Error checking force_time");
				}
				return last.before(target_time);
			}
		}
		return false;
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
	 * return name and SU identity if it exists.
	 * 
	 * We need to query the session to do this. This is used when logging
	 * resource allocations so we can tell the difference between something done
	 * by a user or and admin SU'd as that user.
	 * 
	 * @param req
	 *            Request object
	 * @return String
	 * 
	 */
	public String getLogName() {
		String result = getName();
		if (logname == null ) {
			SessionService serv = (SessionService) getContext().getService(SessionService.class);
			if( serv instanceof ServletSessionService){
				@SuppressWarnings("unchecked")
				ServletSessionService<?> sss = (ServletSessionService) serv;
				if( sss.isSU()){
					AppUser p = sss.getSuperPerson();
					logname = "(" + p.getName() + ")";
				}
			}
		}
		if( logname != null){
			result = result + logname;
		}
		return result;
	}
	
	/**
	 * 
	 * @param role
	 * @return
	 */
	public boolean checkStateRole(String role){
		return false;
	}
}