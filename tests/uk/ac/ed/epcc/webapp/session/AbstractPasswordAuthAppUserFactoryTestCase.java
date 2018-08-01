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

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Set;

import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.DebugGraphics;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
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
			hc.setCryptPassword(h.getAlgorithm(),salt,old);
			copy.commit();
			fac.findByEmail(email);
			String old2 = h.getCryptPassword();
			assertEquals(old, old2);
	}
	
	@Test
	public void testCheckMatches() throws DataException{
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
		U user =  fac.findByEmail(email);
		Handler h = comp.getHandler(user);
		h.setPassword("boris");

		assertTrue(comp.checkPassword(user, "boris"));
		assertFalse(comp.checkPassword(user, "larry"));
	}

	
	@Test
	public void testRandomisePassword() throws DataException{
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
		U user =  fac.findByEmail(email);
		String val = comp.randomisePassword(user);
		
		assertTrue(comp.mustResetPassword(user));
		
		U copy = (U) comp.findByLoginNamePassword(user.getEmail(), val);
	}
	
	@Test
	public void testPasswordFails() throws DataException, MessagingException{
		MockTansport.clear();
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
		U user =  fac.findByEmail(email);
		Handler h = comp.getHandler(user);
		comp.setPassword(user, "boris");
		assertFalse(h.passwordFailsExceeded());
		assertNull(comp.findByLoginNamePassword(email, "hilary", true));
		user =  fac.findByEmail(email); // fails update db only
		h = comp.getHandler(user);
		assertFalse(h.passwordFailsExceeded());
		assertEquals(0, MockTansport.nSent());
		
		
		assertNull(comp.findByLoginNamePassword(email, "hilary", true));
		user =  fac.findByEmail(email); // fails update db only
		h = comp.getHandler(user);
		assertFalse(h.passwordFailsExceeded());
		assertEquals(0, MockTansport.nSent());
		
		assertNull(comp.findByLoginNamePassword(email, "hilary", true));
		user =  fac.findByEmail(email); // fails update db only
		h = comp.getHandler(user);
		assertFalse(h.passwordFailsExceeded());
		assertEquals(0, MockTansport.nSent());
		
		assertNull(comp.findByLoginNamePassword(email, "hilary", true));
		user =  fac.findByEmail(email); // fails update db only
		h = comp.getHandler(user);
		assertTrue(h.passwordFailsExceeded());
		assertEquals(1, MockTansport.nSent());
		Message m = MockTansport.getMessage(0);
		assertEquals(ctx.expandText("${service.name} Password has been locked"), m.getSubject());
		
		assertNull(comp.findByLoginNamePassword(email, "boris", true));
		
		assertNull(comp.findByLoginNamePassword(email, "hilary", true));
		user =  fac.findByEmail(email); // fails update db only
		h = comp.getHandler(user);
		assertTrue(h.passwordFailsExceeded());
		assertEquals(1, MockTansport.nSent()); // no additional mails on more fails
		
		h.resetPasswordFails();
		assertFalse(h.passwordFailsExceeded());
		assertNotNull(comp.findByLoginNamePassword(email, "boris", true));
	}
	
	@Test
	public void testPasswordExpiry() throws DataException{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH,Calendar.AUGUST);
		cal.set(Calendar.DAY_OF_MONTH,18);
		cal.set(Calendar.HOUR_OF_DAY,17);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
		TestTimeService serv = new TestTimeService();
		serv.setResult(cal.getTime());
		getContext().setService(serv);
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
		assertNotNull(email);
		U user =  fac.findByEmail(email);
		Handler h = comp.getHandler(user);
		comp.setPassword(user, "boris");
		user =  fac.findByEmail(email);
		h = comp.getHandler(user);
		 
		assertFalse(comp.mustResetPassword(user));
		cal.add(Calendar.DAY_OF_YEAR, 21);
		serv.setResult(cal.getTime());
		assertTrue(comp.mustResetPassword(user));
		
		
	}
	
	@Test
	public void testNewPassword() throws Exception{
		MockTansport.clear();
		F fac = getFactory();
		DatabasePasswordComposite comp = (DatabasePasswordComposite) fac.getComposite(PasswordAuthComposite.class);
		assertNotNull(comp);
		String email = ctx.getInitParameter("test.email");
		U user =  fac.findByEmail(email);
		Handler h = comp.getHandler(user);
		comp.newPassword(user);
		assertEquals(1,MockTansport.nSent());
		Message message = MockTansport.getMessage(0);
		assertEquals(email, message.getAllRecipients()[0].toString());
		assertEquals(ctx.expandText("${service.name} Account Password Request"),message.getSubject());
		
	}
}