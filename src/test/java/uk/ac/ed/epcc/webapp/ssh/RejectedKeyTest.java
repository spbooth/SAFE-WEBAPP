//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.ssh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.PublicKey;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.ssh.PublicKeyReaderUtil.PublicKeyParseException;

/** These keys were rejected by ssh-keygen as they were missing trailing = in the base64
 * @author Stephen Booth
 *
 */
public class RejectedKeyTest extends WebappTestBase {

	/**
	 * 
	 */
	public RejectedKeyTest() {
		// TODO Auto-generated constructor stub
	}

	
	@Test
	public void testRejetedKey1() throws ValidateException, PublicKeyParseException, IOException, ParseException {
		
		
		
		String key1 = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDox3YFrgjOUX+tnFxQLEi7jbFPtt0zHOaSZ+EMHCV9YrGkTHgd1YTp2kEs34DnsLuS2cWY7isI52+MAMv2a7wBYCiFLG1dT061qu+5uZKe64t0ekQk2MWI0leAgQZN6S4tULFu825Jrn2T65RPcgIJBr/WImlF1TSmoEvGJuvROQKfyM7j8sbex8HSsAUTTMM8okvNlCh3BBb3mEWofQlHQeOvOk/QS2wGLWz2m45jDKmh6n3rduHxnxut2LHnMeGIx+TatmIDZf9PhQX3tMamFybpxajWEAMzpjaPzpzvGKli99kMKIxwPRDCntkjn7LVBvzCSybEg1xMoVDOk+LNbB7QWDQ+D+O4Causwv8Gft2UTJMbOcoTybvjt4nojHAD6bkWeGefCn4qQXzvVvcb974r3CIuIVUGuZfvrIrFjOzS4ugE7dU5iXMFCe23Q/FkUrGjeEcyk0aXtY5YRg7c3v9OXwkxy/pjgOOg7PUHXaO4gi4Kv0G3vkaN2KyJ1Bc";
		
		doTest(key1);
	}
	
	@Test
	public void testRejetedKey2() throws ValidateException, PublicKeyParseException, IOException, ParseException {
		
		
		
		String key1 = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCkjuD7IjJbKkWOutSlox3c7pMYxQFuV8h7gd0wCQHOZ8poN59SnLoJ712Yeeh0KRmX1s4kx1lMudCJzCymyR9xM/jG79yJdLtgIallcG7khwijav6zp9zYdBb0Y5lQ3+7/DgpFaGJCEoRjxJxYz+3VAhD8eNGyVREDiia3pKL6oKVCGr/W68bPIeipCJLORIH0OVANIxcSaE1+2vCUfDXpeQtxjXvie17TVHpKZmq+ZBn5NU2mDIws79dRGkNYEZkBD852VwWlIAjkOerFjGTQrKGq5+e+PWbAuvhYbgVcyNwS1Ztq1rh30kf2XX9mFwZSnHaIUunSEnSTmcx0YxCqM9VeVDE/At1mrFx5jwUzX8PkRenyKAJjoDwrl1AXCXr7Y7qhZ9/A7kTEJNWSj+IqAGSIYk+bzbNcay66cSRshxF8912hKssx1V3nZR8LT74vLdYtT+9v0WZPHry6+eqPHeLUXKBgpzNmV6PKWYAR3a1ejeW385Kwaat3Xsa+aT1EnnEl7OMs6le2n6S0NQYK3Wq/g1r+kRAIp0L5yUErI3tBntaPYNWtHqxINByGuQBBClnAP+9nVGAUN/KpmA9QAhkIASwdYkhG9Zeq5yCM0FvB9bsVm+izvlgKKoMQ4cO7DCvpE3cdYAnQWCwTQeKVD8tWcM3wKMWiQwLlpQZSUQ";
		doTest(key1);
	}


	public void doTest(String key1) throws ValidateException, PublicKeyParseException, IOException, ParseException {
		AuthorizedKeyValidator val = new AuthorizedKeyValidator();
		val.validate(key1);
		PublicKey key = PublicKeyReaderUtil.load(key1);
		assertNotNull(key);
		assertEquals(val.normalise(key1), PublicKeyReaderUtil.format(key));
	}
}
