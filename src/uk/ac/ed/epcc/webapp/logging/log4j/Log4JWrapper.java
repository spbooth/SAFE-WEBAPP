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
package uk.ac.ed.epcc.webapp.logging.log4j;

import org.apache.log4j.Logger;

import uk.ac.ed.epcc.webapp.Version;

/**
 * Implementation of out Logger interface using Log4J
 * 
 * @author spb
 * 
 */


public class Log4JWrapper implements uk.ac.ed.epcc.webapp.logging.Logger {
	private Logger log;
	private Class<?> target;
	private boolean tagged=false;

	public Log4JWrapper(String name){
		log=Logger.getLogger(name);
	}
	private void tag(){
		if( log != null && target != null && tagged == false){
			tagged = true;
			if(target.isAnnotationPresent(Version.class)){
				Version v=target.getAnnotation(Version.class);
				log.debug(target.getCanonicalName()+" "+v.value());
			}
		}
	}
	public Log4JWrapper(Class<?> c) {
		target=c;
		log = Logger.getLogger(c);
	}

	public void debug(Object message) {
		tag();
		if( log != null ) log.debug(message);
	}

	public void debug(Object message, Throwable t) {
		tag();
		if( log != null )log.debug(message,t);
	}

	public void error(Object message) {
		tag();
		if( log != null )log.error(message);
	}

	public void error(Object message, Throwable t) {
		tag();
		if( log != null )log.error(message, t);
	}

	public void fatal(Object message) {
		tag();
		if( log != null )log.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		tag();
		if( log != null )log.fatal(message, t);
	}

	public void info(Object message) {
		tag();
		if( log != null )log.info(message);
	}

	public void info(Object message, Throwable t) {
		tag();
		if( log != null )log.info(message, t);
	}

	public void warn(Object message) {
		tag();
		if( log != null )log.warn(message);
	}

	public void warn(Object message, Throwable t) {
		tag();
		if( log != null )log.warn(message, t);
	}
}