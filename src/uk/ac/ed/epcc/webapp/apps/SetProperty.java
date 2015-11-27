// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;

public class SetProperty implements Command {
    AppContext conn;
    public SetProperty(AppContext c){
    	conn=c;
    }
	public String description() {
		return "Set configuration properties";
	}

	public String help() {
		return "[name]=[value] ... ";
	}

	public void run(LinkedList<String> args) {
		ConfigService serv = conn.getService(ConfigService.class);
		if( serv == null ){
			CommandLauncher.die("No ConfigService found");
		}else{
			for( String p : args){
				int i = p.indexOf("=");
				if( i < 0 ){
					CommandLauncher.die("Malformed argument (requires [name]=[value]");
				}
				String name = p.substring(0, i);
				String value = p.substring(i+1);
				serv.setProperty(name, value);
			}
		}
	}

	public AppContext getContext() {
		return conn;
	}

}