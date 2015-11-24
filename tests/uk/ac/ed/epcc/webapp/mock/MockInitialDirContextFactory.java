// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.mock;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class MockInitialDirContextFactory implements InitialContextFactory {

	/* (non-Javadoc)
	 * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)
	 */
	@Override
	public Context getInitialContext(Hashtable<?, ?> environment)
			throws NamingException {
		return new MockDirContext((String)environment.get(Context.SECURITY_PRINCIPAL), (String)environment.get(Context.SECURITY_CREDENTIALS));
		
	}

}
