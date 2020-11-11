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
package uk.ac.ed.epcc.webapp.email.logging;

import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.logging.Logger;



public class EmailLogger implements Logger {
	private static final Feature EMAIL_WARNING = new Feature("logger.email.mail_warning", false, "Send emails for warnings");
    private final Logger nested;
  
    private final EmailLoggerService serv;
    public EmailLogger(EmailLoggerService serv, Logger l){
    	this.serv=serv;
    	this.nested=l;
    }
    /** Send an email error report
     * 
     * @param message
     * @param t
     */
    public void email(LogLevels level,Object message, Throwable t){
    	serv.emailError(level,t, message.toString());
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
		email(LogLevels.Error,message,null);
	}

	
	public void error(Object message, Throwable t) {
		if( nested != null)
		nested.error(message,t);
		email(LogLevels.Error,message,t);
	}


	public void fatal(Object message) {
		if( nested != null)
		nested.fatal(message);
		email(LogLevels.Fatal,message, null);
	}

	
	public void fatal(Object message, Throwable t) {
		if( nested != null)
		nested.fatal(message, t);
		email(LogLevels.Fatal,message,t);
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
		if( EMAIL_WARNING.isEnabled(serv.getContext())){
			email(LogLevels.Warn,message,null);
		}
	}

	
	public void warn(Object message, Throwable t) {
		if( nested != null)
		nested.warn(message, t);
		if( EMAIL_WARNING.isEnabled(serv.getContext())){
			email(LogLevels.Warn,message,t);
		}

	}

}