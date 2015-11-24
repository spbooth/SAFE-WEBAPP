/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.session.DatabasePasswordComposite.Handler;

public abstract class AbstractPasswordAuthAppUserFactoryTestCase<F extends AppUserFactory<U>,U extends AppUser> extends AppUserFactoryTestCase<F,U> {

	
	@Test
	public void testFindByEmailPassword() throws DataException{
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
			U user =  fac.findByEmail(email);
			Handler h = comp.getHandler(user);
			
			String salt = h.getSalt();
			
			h.setPassword("boris");
			user.commit();String old = h.getCryptPassword();
			System.out.println(" old is "+old);
			U copy = (U) comp.findByLoginNamePassword(user.getEmail(), "boris");
			Handler hc = comp.getHandler(copy);
			assertNotNull(copy);
			assertEquals(user.getEmail(), copy.getEmail());
			// Note we HAVE to reset on copy as user still sees the original value as clean
			hc.setCryptPassword(salt,old);
			copy.commit();
			fac.findByEmail(email);
			String old2 = h.getCryptPassword();
			assertEquals(old, old2);
	}
}
