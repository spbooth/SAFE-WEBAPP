//| Copyright - The University of Edinburgh 2015                            |
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