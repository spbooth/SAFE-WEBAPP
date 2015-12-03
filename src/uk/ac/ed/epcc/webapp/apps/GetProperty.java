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
package uk.ac.ed.epcc.webapp.apps;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;


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