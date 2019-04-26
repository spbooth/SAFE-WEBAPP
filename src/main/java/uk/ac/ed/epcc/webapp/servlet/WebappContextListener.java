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
package uk.ac.ed.epcc.webapp.servlet;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import uk.ac.ed.epcc.webapp.config.CachedConfigService;
/** A ContextListener that tries to avoid the PermGen problem by
 * unregistering the DB drivers when an Application is unloaded.
 * 
 * Also clears the {@link CachedConfigService} though this is probably  only needed for debugging.
 * 
 * @author spb
 *
 */

@WebListener()
public class WebappContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		//System.out.println("unloading");
		try {
		    for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
		        Driver driver = (Driver) e.nextElement();
		        //System.out.println("Consider "+driver.getClass().getCanonicalName());
		        
		        /* We want the classloader for the current application to be eligible to unload
		         * The DriverManager comes from the main classloader and if it holds references to 
		         * classes loaded from the local classloader this will prevent the local classloader from being
		         * unloaded.
		         */
		        if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
		        	//System.out.println("unload");
		            DriverManager.deregisterDriver(driver);
		        }
		    }
		}
		catch (Exception e) {
		    System.out.println("Unable to clean up JDBC driver: " + e.getMessage());
		}
		CachedConfigService.invalidate();
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		//System.out.println("Starting");
	}

}