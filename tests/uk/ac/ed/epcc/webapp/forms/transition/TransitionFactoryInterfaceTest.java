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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.6 $")
public interface TransitionFactoryInterfaceTest<T,K,X extends TransitionFactoryDataProvider<K,T>> {

	
	
	@Test
	public void testGetTransitions() throws Exception;
	@Test
	public void testGetTransition() throws Exception;
	@Test
	public void testLookupTransition() throws Exception;
	
	@Test
	public void testAllowTransition() throws Exception;
	@Test
	public void testGetTargetName();
	
	@Test
	public void testGetSummaryContentHTML() throws Exception;
	
	
	@Test
	public void testFormCreation() throws Exception;
}
