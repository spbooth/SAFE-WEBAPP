// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import org.junit.Test;
/**
 * @author spb
 * @param <T> target type
 * @param <K> key type 
 * @param <X> type of {@link ViewTransitionProvider}
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public interface ViewTransitionFactoryInterfaceTest<T,K,X extends ViewTransitionFactoryDataProvider<K,T>> {

	
	
	@Test
	public void testCanView() throws Exception;
	
	@Test
	public void testGetTopContent() throws Exception;
	@Test
	public void testGetLogContent() throws Exception;
	
	@Test
	public void testGetHelp() throws Exception;
}
