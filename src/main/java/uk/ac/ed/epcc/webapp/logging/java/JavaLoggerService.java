//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.logging.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.LogManager;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A {@link LoggerService} that maps onto the standard java logging api.
 * 
 * Note that when running under tomcat this api is provided by a tomcat specific
 * implementation to allow per-application configuration so consult the tomcat documentation for
 * configuration instructions.
 * @author spb
 *
 */
public class JavaLoggerService implements Contexed, LoggerService {

	private final AppContext conn;
	/**
	 * 
	 */
	public JavaLoggerService(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.String)
	 */
	@Override
	public Logger getLogger(String name) {
		return new JavaLoggerWrapper(name,null,java.util.logging.Logger.getLogger(name));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.Class)
	 */
	@Override
	public Logger getLogger(Class c) {
		return new JavaLoggerWrapper(c.getCanonicalName(), null, java.util.logging.Logger.getLogger(c.getCanonicalName()));
	}

	@Override
	public void initialiseLogging() {
		ConfigService config = conn.getService(ConfigService.class);
		if( config != null ) {
			FilteredProperties props = new FilteredProperties(config.getServiceProperties(), "logconfig");
			if( ! props.isEmpty()) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					props.store(out, "# config props");
					System.out.println(out.toString());
					LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(out.toByteArray()));
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}
//
//	@Override
//	public void shutdownLogging() {
//	
//	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

}
