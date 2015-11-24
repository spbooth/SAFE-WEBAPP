// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A test to use mock ldap authentication
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class LdapAuthAppUserTestCase extends
		AppUserFactoryTestCase<AppUserFactory<AppUser>, AppUser> {

	
	
	@Before
	public void initData() throws DataFault{
		AppUserFactory<AppUser> fac = getFactory();
		
		AppUser user = fac.makeBDO();
		user.setEmail(ctx.getInitParameter("test.email"));
		user.setRealmName(WebNameFinder.WEB_NAME,"testuser");
		user.commit();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase#getFactory()
	 */
	@Override
	public AppUserFactory<AppUser> getFactory() {
		return new AppUserFactory<AppUser>(getContext(),"ldapperson");
	}
	@Test
	public void testFindByEmailPassword() throws DataException{
		AppUserFactory<AppUser> fac = getFactory();
		LdapPasswordComposite comp = (LdapPasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		String email = ctx.getInitParameter("test.email");
		AppUser user =  fac.findByEmail(email);
		assertNotNull(user.getRealmName(WebNameFinder.WEB_NAME));
		assertNotNull(user.getEmail());
		AppUser copy =  comp.findByLoginNamePassword("testuser", "testpassword");
		
		assertNotNull(copy);
		assertEquals(user.getEmail(), copy.getEmail());
		
		AppUser badcopy = comp.findByLoginNamePassword("testuser", "badpassword");
		
		assertNull(badcopy);
	}
}
