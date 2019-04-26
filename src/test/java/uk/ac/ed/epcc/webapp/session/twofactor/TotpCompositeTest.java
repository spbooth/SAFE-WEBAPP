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
package uk.ac.ed.epcc.webapp.session.twofactor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;

/**
 * @author Stephen Booth
 *
 */
@ConfigFixtures("twofactor.properties")
public class TotpCompositeTest extends WebappTestBase {

	@Test
	public void testMakeKey() throws Exception {
		AppUserFactory fac = ctx.makeContexedObject(AppUserFactory.class,"Person");
		TotpCodeAuthComposite comp = (TotpCodeAuthComposite) fac.getComposite(FormAuthComposite.class);
		
		assertNotNull(comp);
		
		Key key = comp.makeNewKey();
		
		assertNotNull(key);
		Base32 codec = new Base32(false);
		System.out.println(codec.encodeAsString(key.getEncoded()));
		
	}
	
	@Test
	public void testImport() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
		String external = "BOUMKOPGJBA67NXX3XNM3EZ32E";
		Base32 codec = new Base32(false);
		byte[] data = codec.decode(external);
		//assertEquals(external.length()*5, data.length*8);
		SecretKeySpec key = new SecretKeySpec(data,"HmacSHA1");
		
		String string = codec.encodeAsString(key.getEncoded());
		System.out.println(string);
		assertTrue(string.startsWith(external));
	}
	
	@Test
	public void testVerify() throws ConsistencyError, Exception {
		TestTimeService serv = new TestTimeService();
		ctx.setService(serv);
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, Calendar.JULY, 2, 20, 22);
		serv.setResult(cal.getTime());
		AppUserFactory<?> fac = ctx.makeContexedObject(AppUserFactory.class,"Person");
		TotpCodeAuthComposite comp = (TotpCodeAuthComposite) fac.getComposite(FormAuthComposite.class);
		
		takeBaseline();
		AppUser user = fac.makeFromString("fred@example.com");
		String external = "UJ4SLJJPNPXVGIPLXDTQUGVKNI";
		Base32 codec = new Base32(false);
		byte[] data = codec.decode(external);
		//assertEquals(external.length()*5, data.length*8);
		SecretKeySpec key = new SecretKeySpec(data,"HmacSHA1");
		comp.setSecret(user, key);
		user.commit();
		checkDiff("/cleanup.xsl", "verify.xml");
		
		
		
		assertTrue(comp.verify(user, 278504));
		
	}
	
	@Test
	public void testgetCode() throws ConsistencyError, Exception {
			AppUserFactory<?> fac = ctx.makeContexedObject(AppUserFactory.class,"Person");
		TotpCodeAuthComposite comp = (TotpCodeAuthComposite) fac.getComposite(FormAuthComposite.class);
		
		takeBaseline();
		AppUser user = fac.makeFromString("fred@example.com");
		// These are values from the google-authenticator-libpam test case
		String external = "2SH3V3GDW7ZNMGYE";
		comp.setSecret(user, external);
		user.commit();
		long code = comp.getCode(comp.getSecret(user),10000);
		System.out.println(""+code);

		assertEquals(50548, code);
		
	}
}
