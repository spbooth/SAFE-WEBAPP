// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
@uk.ac.ed.epcc.webapp.Version("$Id: GetProperty.java,v 1.6 2014/09/15 14:30:11 spb Exp $")


public class GetProperty implements Command {
    private final AppContext conn;
    public GetProperty(AppContext conn){
    	this.conn=conn;
    }
	public String description() {
			return "Query Config Properties";
	}

	public String help() {
		return "name ...";
	}

	public void run(LinkedList<String> args) {
		ConfigService serv = conn.getService(ConfigService.class);
		if( serv == null ){
			CommandLauncher.die("No ConfigService found");
		}else{
			Properties props = serv.getServiceProperties();
			if( args.size() > 0 ){
				for(String p : args ){
					System.out.println(props.getProperty(p, ""));
				}
			}else{
				for( Enumeration e = props.propertyNames(); e.hasMoreElements() ;){
					String name = (String) e.nextElement();
					System.out.println(name+"="+props.getProperty(name));
				}
			}
		}
	}

	public AppContext getContext() {
		return conn;
	}

}