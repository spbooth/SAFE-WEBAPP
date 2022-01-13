package uk.ac.ed.epcc.webapp.ssl;

import static org.junit.Assert.assertNotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

public class SSLContextTest extends WebappTestBase {

	public SSLContextTest() {
		
	}

	@Test
	public void testGetContext() throws Exception {
		SSLService serv = ctx.getService(SSLService.class);
		
		SSLContext ssl = serv.getSSLContext("SSL", "Testing");
		
		assertNotNull(ssl);
		
		SSLSocketFactory fac = ssl.getSocketFactory();
		assertNotNull(fac);
	}
}
