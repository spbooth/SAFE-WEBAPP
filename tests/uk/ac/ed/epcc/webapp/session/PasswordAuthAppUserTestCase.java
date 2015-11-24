// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.session;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
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
		return new AppUserFactory<AppUser>(getContext(),"TestPasswordAuthAppUser");
	}

}
