// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface IndexTransitionFactoryInterfaceTest<T,K,X extends TransitionFactoryDataProvider<K,T>> {

	@Test
	public void testIndexTransition() throws TransitionException;

}
