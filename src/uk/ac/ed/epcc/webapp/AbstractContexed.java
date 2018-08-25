//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A simple implementation of {@link Contexed}
 * 
 * This can be used as abase class by objects that would otherwise 
 * extend Object slightly reducing the boiler plate needed to implement
 * {@link Contexed}. 
 * @author Stephen Booth
 *
 */
public abstract class AbstractContexed implements Contexed {
	/**
	 * @param conn
	 */
	public AbstractContexed(AppContext conn) {
		super();
		this.conn = conn;
	}

	protected final AppContext conn;
	private Logger log;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public final AppContext getContext() {
		return conn;
	}
	
	public final Logger getLogger() {
		if( log == null) {
		  log =  getContext().getService(LoggerService.class).getLogger(getClass());
		}
		return log;
	}

}
