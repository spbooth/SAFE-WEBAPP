//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.logging.buffer;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * @author spb
 *
 */

public class BufferLoggerService implements LoggerService, Contexed{
    private final AppContext conn;
    private final LoggerService nested;
    private final StringBuffer buffer;
    private int max_length=0;
	/**
	 * 
	 */
	public BufferLoggerService(AppContext c) {
		this.conn=c;
		nested = c.getService(LoggerService.class);
		buffer=new StringBuffer();
	}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
 */
public void cleanup() {
	nested.cleanup();
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
 */
public Class<? super LoggerService> getType() {
	return LoggerService.class;
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.String)
 */
public Logger getLogger(String name) {
	return new BufferLogger(buffer, max_length,nested.getLogger(name));
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.Class)
 */
public Logger getLogger(Class c) {
	return new BufferLogger(buffer,max_length, nested.getLogger(c));
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
 */
	public AppContext getContext() {
		return conn;
	}
	
	public StringBuffer getBuffer(){
		return buffer;
	}
	/**
	 * @return the max_length
	 */
	public int getMaxLength() {
		return max_length;
	}

	/**
	 * @param max_length the max_length to set
	 */
	public void setMaxLength(int max_length) {
		this.max_length = max_length;
	}
}
	