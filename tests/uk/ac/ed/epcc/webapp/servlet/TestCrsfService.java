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
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet.GetIDVisitor;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
@PreRequisiteService({SessionService.class})
public class TestCrsfService implements CrsfTokenService,Contexed {
	/**
	 * @param conn
	 */
	public TestCrsfService(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;
	private String tag="12345";

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
	public <K, T, P extends TransitionFactory<K, T>> String getCrsfToken(P provider,T target) {
		StringBuilder sb = new StringBuilder();
		sb.append(tag);
		sb.append(provider.getTargetName());
		if( target != null) {
			GetIDVisitor<T, K> vis = new GetIDVisitor<T, K>(target);
			sb.append(provider.accept(vis));
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
