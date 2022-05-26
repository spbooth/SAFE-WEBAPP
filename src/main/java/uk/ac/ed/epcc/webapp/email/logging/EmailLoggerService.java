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
package uk.ac.ed.epcc.webapp.email.logging;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

@PreRequisiteService({LoggerService.class})


public class EmailLoggerService implements Contexed, LoggerService {
	private static final Feature EMAIL_LOGGING_FEATURE = new Feature("logging.send_email",true,"Send error reports by email");
    /**
	 * 
	 */
	public static final String VERSION_PROP_PREFIX = "version";
	private final AppContext conn;
    private LoggerService nested;
    private Logger self_logger=null;
    private boolean in_error=false;
    private Emailer mailer=null;
    public EmailLoggerService(AppContext conn){
    	this.conn=conn;
    	nested=conn.getService(LoggerService.class);
    	if( nested == this){
    		nested = null; // we got ourselves somehow.
    		emailError(LogLevels.Error, new Exception(), "EmailLoggerServer registration error, own parent");
    	}
    	while( nested != null && nested instanceof EmailLoggerService){
    		nested = ((EmailLoggerService)nested).nested;
    	}
    	if( nested != null ) {
    		self_logger = nested.getLogger(getClass());
    	}
    	
    }
	
	public Logger getLogger(String name) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(name);
		}
		if( EMAIL_LOGGING_FEATURE.isEnabled(getContext())) {
			return l;
		}
		return new EmailLogger(this, l);
	}


	public Logger getLogger(Class c) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(c);
		}
		if( ! EMAIL_LOGGING_FEATURE.isEnabled(getContext())) {
			return l;
		}
		return new EmailLogger(this, l);
	}

	
	public void cleanup() {
		nested.cleanup();
	}
	
	public AppContext getContext() {
		return conn;
	}
	@SuppressWarnings("unchecked")
	protected Hashtable getProps(){
		Hashtable props = new Hashtable();
		SessionService service = conn.getService(SessionService.class);
		//Logger l = nested.getLogger(getClass());
		if(service !=null){
			AppUser person = service.getCurrentPerson();
			if (person != null) {
				props.put("person_id", "" + person.getID());
				String name = person.getLogName();
				if(name != null ){
					props.put("person", name);
				}
				String email = person.getEmail();
				if( email != null ){
					props.put("person.email", email);
				}
			}
		}
		try{
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			props.put("host.name",localMachine.getHostName());
			
		}catch(Exception t){
			//if( nested != null ){
			//nested.getLogger(getClass()).error("Cannot get hostname",t);
			//}
			String hostname = System.getenv("HOSTNAME");
			if( hostname != null ){
				props.put("host.name",hostname);
			}
		}
		try{
			DatabaseService serv = conn.getService(DatabaseService.class);
			if( serv != null){
				props.put("database.host", serv.getSQLContext().getConnectionHost());
			}
		}catch(Exception t){
			if( nested != null ){
				nested.getLogger(getClass()).error("Cannot get database hostname");
			}
		}
		ConfigService cfg = conn.getService(ConfigService.class);
		if( cfg != null ){
			
			
			//l.debug("have config service");
			Properties p = cfg.getServiceProperties();
			Enumeration e = p.propertyNames();
			while( e.hasMoreElements()){
				Object key = e.nextElement();
				Object value = p.getProperty(key.toString());
				//l.debug("key="+key+" value="+value);
				if( value != null ){
					props.put(key.toString(), value);
				}
			}
			
			// software versions
			FilteredProperties version = new FilteredProperties(p, VERSION_PROP_PREFIX);
			Map v = new TreeMap<>(); //sorted
			for(Enumeration ve= version.propertyNames(); ve.hasMoreElements() ;) {
				Object key = ve.nextElement();
				v.put(key, version.getProperty(key.toString()));
			}
			props.put("versions", v);
		}else{
			//l.debug("no config service");
		}
		
		return props;
	}
	protected synchronized void emailError(LogLevels level,Throwable e, String text) {
		if( ! in_error ){
			try{
				in_error=true;
				Map props;
				try {
					props = getProps();
				}catch(Exception x) {
					props = new HashMap<>();
					props.put("prop_generation_error",x.getMessage());
					if( self_logger != null) {
						self_logger.error("Error making props", x);
					}
				}
				props.put("report_level", level.toString());
				getMailer().errorEmail(self_logger, e, props, text);
			}catch(Exception t){
				if( self_logger != null ){
					self_logger.error("Error reporting error by email",t);
				}
				
			}finally{
				in_error=false;
			}
		}
	}
	protected Logger getSelfLogger() {
		return self_logger;
	}

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}

	public Emailer getMailer() {
		if( mailer == null) {
			mailer = Emailer.getFactory(getContext());
		}
		return mailer;
	}
}