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
package uk.ac.ed.epcc.webapp.logging.log4j2;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Implementation of out Logger interface using Log4J
 * 
 * @author spb
 * 
 */


public class Log4JWrapper implements uk.ac.ed.epcc.webapp.logging.Logger {
	private Logger log;
	

	public Log4JWrapper(String name){
		log=LogManager.getLogger(name);
	}
	
	public Log4JWrapper(Class<?> c) {
		log = LogManager.getLogger(c);
	}

	@Override
	public void debug(Object message) {
		if( log != null ) log.debug(message);
	}

	@Override
	public void debug(Object message, Throwable t) {
		if( log != null )log.debug(message,t);
	}

	@Override
	public void error(Object message) {
		if( log != null )log.error(message);
	}

	@Override
	public void error(Object message, Throwable t) {
		if( log != null )log.error(message, t);
	}

	@Override
	public void fatal(Object message) {
		if( log != null )log.fatal(message);
	}

	@Override
	public void fatal(Object message, Throwable t) {
		if( log != null )log.fatal(message, t);
	}

	@Override
	public void info(Object message) {
		if( log != null )log.info(message);
	}

	@Override
	public void info(Object message, Throwable t) {
		if( log != null )log.info(message, t);
	}

	@Override
	public void warn(Object message) {
		if( log != null )log.warn(message);
	}

	@Override
	public void warn(Object message, Throwable t) {
		if( log != null )log.warn(message, t);
	}

	@Override
	public void debug(Supplier<String> message) {
		if( log != null) log.debug(message);
	}

	@Override
	public void debug(Supplier<String> message, Throwable t) {
		if( log != null) log.debug(message,t);
	}

	@Override
	public void error(Supplier<String> message) {
		if( log != null) log.error(message);
	}

	@Override
	public void error(Supplier<String> message, Throwable t) {
		if( log != null) log.error(message,t);
	}

	@Override
	public void fatal(Supplier<String> message) {
		if( log != null) log.fatal(message);
	}

	@Override
	public void fatal(Supplier<String> message, Throwable t) {
		if( log != null) log.fatal(message,t);
	}

	@Override
	public void info(Supplier<String> message) {
		if( log != null) log.info(message);
	}

	@Override
	public void info(Supplier<String> message, Throwable t) {
		if( log != null) log.info(message,t);
	}

	@Override
	public void warn(Supplier<String> message) {
		if( log != null) log.warn(message);
	}

	@Override
	public void warn(Supplier<String> message, Throwable t) {
		if( log != null) log.warn(message,t);
	}
}