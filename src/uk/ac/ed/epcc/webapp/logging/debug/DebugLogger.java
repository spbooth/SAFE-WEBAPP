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
package uk.ac.ed.epcc.webapp.logging.debug;

import uk.ac.ed.epcc.webapp.logging.Logger;


/** A {@link Logger} that throws a {@link FatalError} after logging an error.
 * 
 * @author spb
 *
 */
public class DebugLogger implements Logger {
    private final Logger nested;
    
  
    public Logger getNested() {
		return nested;
	}


	public DebugLogger(Logger l){
    	this.nested=l;
    }
  
	
	public void debug(Object message) {
		if( nested != null)
		nested.debug(message);
	}

	
	public void debug(Object message, Throwable t) {
		if( nested != null)
		nested.debug(message, t);
	}

	
	public void error(Object message) {
		if( nested != null)
		nested.error(message);
		throw new FatalError(message.toString());
	}

	
	public void error(Object message, Throwable t) {
		if( nested != null)
		nested.error(message,t);
		throwError(message, t);
	}


	/**
	 * @param message
	 * @param t
	 * @throws FatalError
	 */
	private void throwError(Object message, Throwable t) throws FatalError {
		if( t == null){
			throw new FatalError(message.toString());
		}
		if(! (t instanceof FatalError)){
			// avoid loops
			throw new FatalError(message.toString(),t);
		}else {
			throw (FatalError) t;
		}
	}


	public void fatal(Object message) {
		if( nested != null)
		nested.fatal(message);
		throw new FatalError(message.toString());
	}

	
	public void fatal(Object message, Throwable t) {
		if( nested != null)
		nested.fatal(message, t);
		throwError(message.toString(),t);
	}

	
	public void info(Object message) {
		if( nested != null)
		nested.info(message);

	}

	
	public void info(Object message, Throwable t) {
		if( nested != null)
		nested.info(message,t);

	}

	
	public void warn(Object message) {
		if( nested != null)
		nested.warn(message);
	}

	
	public void warn(Object message, Throwable t) {
		if( nested != null)
		nested.warn(message, t);

	}

}