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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;


@PreRequisiteService({LoggerService.class})

/** A {@link LoggerService} where a {@link FatalError} is thrown after logging an error.
 * 
 * @author spb
 *
 */
public class DebugLoggerService implements Contexed, LoggerService {
    private final AppContext conn;
    private LoggerService nested;
    private static final Feature FATAL_FEATURE = new Feature("debug.errors_are_fatal",true,"DebugLogger throws fatal errors");
    public DebugLoggerService(AppContext conn){
    	this.conn=conn;
    	nested=conn.getService(LoggerService.class);
    }
	
    public LoggerService getNested(){
    	return nested;
    }
	public Logger getLogger(String name) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(name);
		}
		if( FATAL_FEATURE.isEnabled(getContext())) {
			return new DebugLogger(l);
		}
		return l;
	}


	public Logger getLogger(Class c) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(c);
		}
		if( FATAL_FEATURE.isEnabled(getContext())) {
			return new DebugLogger(l);
		}
		return l;
	}

	
	public void cleanup() {
		nested.cleanup();
	}
	
	public AppContext getContext() {
		return conn;
	}
	
	

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
}