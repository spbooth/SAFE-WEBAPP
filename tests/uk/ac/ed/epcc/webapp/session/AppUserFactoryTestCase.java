/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public abstract class AppUserFactoryTestCase<F extends AppUserFactory<U>,U extends AppUser> extends DataObjectFactoryTestCase<F,U> {

	

	@Test
	public final void testFindByEmailString() throws DataException {
		F fac = getFactory();
		for(U user : fac.all()){
			String email = user.getEmail();
			if( email != null && email.trim().length() > 0){
				AppUser tmp;
				assertEquals("cannot find email ["+email+"]",user, tmp=fac.findByEmail(email,true));
				tmp.release();
				assertEquals("uppercase match "+email,user, tmp = fac.findByEmail(email.toUpperCase(),true));
				tmp.release();
				assertEquals("lowercase match "+email,user,tmp= fac.findByEmail(email.toLowerCase(),true));
				tmp.release();
				user.release();
			}
		}
	}

	@Test
	public final void testIsRegisteredUsername() throws DataFault, DataException {
		F fac = getFactory();
		for(U user : fac.all()){
			String email = user.getEmail();
			if( email != null ){
				assertTrue(fac.isRegisteredUsername(email));
				user.release();
			}
		}
		assertFalse(fac.isRegisteredUsername("bogus@example.com"));
	}

}
