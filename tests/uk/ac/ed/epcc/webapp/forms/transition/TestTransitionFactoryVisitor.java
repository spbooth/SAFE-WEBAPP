// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.*;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TestTransitionFactoryVisitor<R,T,K> implements
		TransitionFactoryVisitor<R, T, K> {

	public TestTransitionFactoryVisitor(boolean expect_provider) {
		super();
		this.expect_provider = expect_provider;
	}

	private final boolean expect_provider;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider)
	 */
	public R visitTransitionProvider(TransitionProvider<K, T> prov) {
		assertNotNull(prov);
		assertTrue(expect_provider);
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitPathTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider)
	 */
	public R visitPathTransitionProvider(PathTransitionProvider<K, T> prov) {
		assertNotNull(prov);
		assertFalse(expect_provider);
		return null;
	}

}
