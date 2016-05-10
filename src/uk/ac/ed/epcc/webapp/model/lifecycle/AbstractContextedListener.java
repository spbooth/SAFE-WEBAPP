//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.lifecycle;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A {@link AbstractListener} that implements {@link Contexed}
 * @author spb
 *
 */
public class AbstractContextedListener<R> extends AbstractListener<R> implements Contexed {

	private final AppContext conn;
	/**
	 * 
	 */
	public AbstractContextedListener(AppContext conn, Class<R> clazz) {
		super(clazz);
		this.conn=conn;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public final AppContext getContext() {
		// TODO Auto-generated method stub
		return conn;
	}
	
	public Logger getLogger(){
		return conn.getService(LoggerService.class).getLogger(getClass());
	}

}
