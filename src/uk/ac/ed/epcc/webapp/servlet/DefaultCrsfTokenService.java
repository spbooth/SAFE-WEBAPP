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

import java.security.MessageDigest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet.GetIDVisitor;
import uk.ac.ed.epcc.webapp.session.Hash;
import uk.ac.ed.epcc.webapp.session.RandomService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author Stephen Booth
 *
 */
@PreRequisiteService({SessionService.class,RandomService.class})
public class DefaultCrsfTokenService implements CrsfTokenService,Contexed {
	private static final String CRSF_TAG_ATTR="CrsfTag";
	/**
	 * @param conn
	 */
	public DefaultCrsfTokenService(AppContext conn) {
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
		SessionService sess = getContext().getService(SessionService.class);
		if( sess == null) {
			return null;
		}
		String tag = (String) sess.getAttribute(CRSF_TAG_ATTR);
		if( tag == null) {
			RandomService rnd = getContext().getService(RandomService.class);
			tag = rnd.randomString(getContext().getIntegerParameter("crsf.taglen", 16));
			sess.setAttribute(CRSF_TAG_ATTR, tag);
		}
		Hash h = getContext().getEnumParameter(Hash.class, "crsf.hash", Hash.SHA1);
		MessageDigest md;
		try {
			md = h.getDigest();
			
			md.update(provider.getTargetName().getBytes());
			if( target != null) {
				GetIDVisitor<T, K> vis = new GetIDVisitor<T, K>(target);
				md.update(provider.accept(vis).getBytes());
			}
			md.update(tag.getBytes());
			return Hash.getHex(md.digest());
		} catch (Throwable e) {
			getContext().error(e, "Error making crsf value");
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}


}
