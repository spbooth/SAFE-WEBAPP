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
package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** A {@link CrsfTokenService} that disables the check.
 * @author Stephen Booth
 *
 */
public class NullCrsfTokenService implements CrsfTokenService,Contexed {
	private static final String CRSF_TAG_ATTR="CrsfTag";
	/**
	 * @param conn
	 */
	public NullCrsfTokenService(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super CrsfTokenService> getType() {
		return CrsfTokenService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.CrsfTokenService#getCrsfToken(uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <K, T, P extends TransitionFactory<K, T>> String getCrsfToken(P provider, T target) {
		return null;
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}


}
