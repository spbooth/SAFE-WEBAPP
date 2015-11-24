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
@uk.ac.ed.epcc.webapp.Version("$Id: CheckClassProperties.java,v 1.5 2014/09/15 14:30:11 spb Exp $")


public class CheckClassProperties implements Command {
    private final AppContext conn;
    public CheckClassProperties(AppContext conn){
    	this.conn=conn;
    }
	public String description() {
			return "Check class definitions in Config Properties";
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
			
				for( Enumeration e = props.propertyNames(); e.hasMoreElements() ;){
					String name = (String) e.nextElement();
					String class_name = props.getProperty(name);
					if( name.startsWith(AppContext.CLASSDEF_PROP_PREFIX)){
						// check classdef points to a valid class
						String fqn = class_name;
						try{
							Class clazz = Class.forName(fqn);
						}catch(Throwable e1){
							System.out.println("classdef:"+name.substring(AppContext.CLASSDEF_PROP_PREFIX.length())+" Not found \n\t"+fqn);
						}
					}else if( name.startsWith(AppContext.CLASS_PREFIX)){
						Object o=null;
						try{
						o = conn.getClassFromName(Object.class, null, class_name);
						}catch(Throwable t){
							
						}
						if( o == null ){
							
							System.out.println("classname :"+name+" value="+class_name+" does not resolve");
						}
					}
				}
			
		}
	}

	public AppContext getContext() {
		return conn;
	}

}