//| Copyright - The University of Edinburgh 2012                            |
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

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public class PasswordAuthAppUserTestCase extends
		AbstractPasswordAuthAppUserFactoryTestCase<AppUserFactory<AppUser>, AppUser> {

	
	
	@Before
	public void initData() throws DataFault{
		AppUserFactory<AppUser> fac = getFactory();
		
		AppUser user = fac.makeBDO();
		user.setEmail(ctx.getInitParameter("test.email"));
		user.commit();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase#getFactory()
	 */
	@Override
	public AppUserFactory<AppUser> getFactory() {
		return new AppUserFactory<>(getContext(),"TestPasswordAuthAppUser");
	}

	@Test
	@ConfigFixtures("sqlfilter.properties")
	public void testSQLFilter() throws DataException{
		testFindByEmailPassword();
	}
	@Test
	@ConfigFixtures("sqlfilter.properties")
	public void testSQLPasswordExpiry() throws DataException{
		testPasswordExpiry();
	}
}