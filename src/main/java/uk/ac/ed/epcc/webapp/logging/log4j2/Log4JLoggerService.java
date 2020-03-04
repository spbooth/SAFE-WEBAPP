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

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

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

}
