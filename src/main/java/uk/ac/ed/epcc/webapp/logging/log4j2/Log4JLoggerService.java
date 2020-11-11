//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.logging.log4j2;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.CloseableThreadContext;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
public class Log4JLoggerService implements LoggerService {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
	

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.String)
	 */
	@Override
	public Logger getLogger(String name) {
		return new Log4JWrapper(name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.Class)
	 */
	@Override
	public Logger getLogger(Class c) {
		return new Log4JWrapper(c);
	}

	@Override
	public void securityEvent(String event, SessionService sess, Map context) {
		Logger logger = getLogger(SECURITY_LOG);
		   Map attr = new HashMap();
		   if( context != null) {
			   attr.putAll(context);
		   }
		  if( sess != null ) {
			  sess.addSecurityContext(attr);
		  }
		  // add context as Log4J thread-context rather than text
		  // as this will be better for parsability
		  try( CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(attr)){
			  logger.info(event);
		  }
	}

}
