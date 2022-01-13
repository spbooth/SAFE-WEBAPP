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
package uk.ac.ed.epcc.webapp.session;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;

/**
 * @author Stephen Booth
 *
 */
@ConfigFixtures("avatar.properties")
public class AvatarTestCase extends AbstractTransitionServletTest {

	AppUserTransitionProvider provider;
	AppUser target;
	@Before
	public void setup() throws DataException {
		provider = AppUserTransitionProvider.getInstance(ctx);
		
	}
	@Test
	public void addAvatarTest() throws ConsistencyError, Exception {
		takeBaseline();
		SessionService  sess = setupPerson("sw@example.com");
		target = sess.getCurrentPerson();
		setTransition(provider,AvatarComposite.SET_AVATAR,target);
		
		addUploadParam(AvatarComposite.AVATAR,"image/png","avatar.png");
		setAction(AvatarComposite.AddAvatarTransition.ATTACH_ACTION);
		runTransition();
		checkViewRedirect(provider, target);
		checkDiff("/normalize.xsl", "avatar.xml");
		checkViewContent(null, "view_avatar.xml");
	}
	
	@Test
	@DataBaseFixtures("avatar.xml")
	public void removeAvatarTest() throws ConsistencyError, Exception {
		takeBaseline();
		SessionService  sess = setupPerson("sw@example.com");
		target = sess.getCurrentPerson();
		setTransition(provider,AvatarComposite.SET_AVATAR,target);
		setAction(AvatarComposite.AddAvatarTransition.REMOVE_ACTION);
		runTransition();
		checkViewRedirect(provider, target);
		checkDiff("/normalize.xsl", "remove_avatar.xml");
		checkViewContent(null, "view_removed.xml");
	}
}
