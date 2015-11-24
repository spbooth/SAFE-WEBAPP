// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import org.junit.Test;

/**
 * @author spb
 * @param <T> 
 * @param <K> 
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public interface TransitionProviderInterfaceTest<T,K,X extends TransitionFactoryDataProvider<K,T>> extends
		TransitionFactoryInterfaceTest<T, K, X> {

	
	@Test
	public void testGetID() throws Exception;
	
	@Test
	public void testVisitor();
}
