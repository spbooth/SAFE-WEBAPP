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

import org.apache.commons.logging.LogFactory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;


/** A {@link LoggerService} that uses apache commons logging.
 * 
 * @author spb
 *
 */
public class CommonsLoggerService implements LoggerService, Contexed {
    LogFactory fac;
    private final AppContext c;
    public CommonsLoggerService(AppContext c){
    	fac = LogFactory.getFactory();
    	this.c=c;
    }
	public Logger getLogger(String name) {
		return new CommonsWrapper(fac.getInstance(name));
	}

	public Logger getLogger(Class c) {
		return new CommonsWrapper(fac.getInstance(c));
	}
	public void cleanup() {
		
	}
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
	public AppContext getContext() {
		return c;
	}
	


}