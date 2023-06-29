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
package uk.ac.ed.epcc.webapp.logging;

import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.AppContext;

/**
 * This is the Logger interface used by the Webapp classes. Its essentially a
 * wrapper around the subset of the log4J interface that we use but by wrapping
 * all the logging we can reduce dependencies to external packages.
 * 
 * The LoggerService policy object returns the appropriate Logger
 * classes.
 * 
 * @author spb
 * 
 */
public interface Logger {
	
	public void debug(Object message);
	
	public default void debug(Supplier<String> message) {
		debug(message.get());
	}
	
	public void debug(Object message, Throwable t);
	
	public default void debug(Supplier<String> message, Throwable t) {
		debug(message.get(),t);
	}
	
	public void error(Object message);
	
	public default void error(Supplier<String> message) {
		error(message.get());
	}

	public void error(Object message, Throwable t);
	
	public default void error(Supplier<String> message, Throwable t) {
		error(message.get(),t);
	}

	public void fatal(Object message);
	
	public default void fatal(Supplier<String> message) {
		fatal(message.get());
	}

	public void fatal(Object message, Throwable t);
	
	public default void fatal(Supplier<String> message, Throwable t) {
		fatal(message.get(),t);
	}

	public void info(Object message);
	
	public default void info(Supplier<String> message) {
		info(message.get());
	}

	public void info(Object message, Throwable t);
	
	public default void info(Supplier<String> message, Throwable t) {
		info(message.get(),t);
	}

	public void warn(Object message);
	
	public default void warn(Supplier<String> message) {
		warn(message.get());
	}

	public void warn(Object message, Throwable t);
	
	public default void warn(Supplier<String> message, Throwable t) {
		warn(message.get(),t);
	}
	
	public static Logger getLogger(Class clazz) {
		return AppContext.getContext().getService(LoggerService.class).getLogger(clazz);
	}
}