// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email.logging;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

@PreRequisiteService({LoggerService.class})
@uk.ac.ed.epcc.webapp.Version("$Id: EmailLoggerService.java,v 1.4 2015/07/21 15:39:19 spb Exp $")

public class EmailLoggerService implements Contexed, LoggerService {
    private final AppContext conn;
    private LoggerService nested;
    private boolean in_error=false;
    public EmailLoggerService(AppContext conn){
    	this.conn=conn;
    	nested=conn.getService(LoggerService.class);
    }
	
	public Logger getLogger(String name) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(name);
		}
		return new EmailLogger(this, l);
	}


	public Logger getLogger(Class c) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(c);
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
				String name = person.getName();
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
			
		}catch(Throwable t){
			nested.getLogger(getClass()).error("Cannot get hostname",t);
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
		}else{
			//l.debug("no config service");
		}
		
		return props;
	}
	protected synchronized void emailError(Throwable e, String text) {
		if( ! in_error ){
			try{
				in_error=true;
				Emailer.errorEmail(getContext(), e, getProps(), text);
			}catch(Throwable t){
				if( nested != null ){
					Logger l = nested.getLogger(getClass());
					if( l != null ){
						l.error("Error reporting error by email",t);
					}
				}
				
			}finally{
				in_error=false;
			}
		}
	}

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
}