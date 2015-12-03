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
package uk.ac.ed.epcc.webapp.logging.commons;

import org.apache.commons.logging.Log;

import uk.ac.ed.epcc.webapp.logging.Logger;



public class CommonsWrapper implements Logger {
    private final Log log;
    CommonsWrapper(Log log){
    	this.log=log;
    }
	public void debug(Object message) {
		log.debug(message);
	}

	public void debug(Object message, Throwable t) {
		log.debug(message,t);

	}

	public void error(Object message) {
		log.error(message);

	}

	public void error(Object message, Throwable t) {
		log.error(message,t);
	}

	public void fatal(Object message) {
		log.fatal(message);

	}

	public void fatal(Object message, Throwable t) {
		log.fatal(message,t);
	}

	public void info(Object message) {
		log.info(message);

	}

	public void info(Object message, Throwable t) {
		log.info(message,t);

	}

	public void warn(Object message) {
		log.warn(message);

	}

	public void warn(Object message, Throwable t) {
		log.warn(message,t);

	}

}