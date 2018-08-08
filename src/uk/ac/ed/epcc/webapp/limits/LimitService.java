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
package uk.ac.ed.epcc.webapp.limits;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;

/** A {@link AppContextService} that can be used to poll for high resource usage
 * in a request and throw an exception to terminate the request.
 * 
 * The application code needs to explicitly
 * @author Stephen Booth
 *
 */
public abstract class LimitService implements AppContextService<LimitService>, Contexed {

	private final AppContext conn;
	
	public LimitService(AppContext conn) {
		this.conn=conn;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public final AppContext getContext() {
		return conn;
	}

	/** Estimate the resources used by this request and throw a {@link LimitException}
	 * if these exceed a reasonable value for an interactive page.
	 * 
	 * In practice this will be the time since the start of the request and the increase of global memory consumption
	 * since the start of the request
	 * @throws LimitException
	 */
	public abstract void checkLimit() throws LimitException;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
				
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public final Class<? super LimitService> getType() {
		return LimitService.class;
	}

}
