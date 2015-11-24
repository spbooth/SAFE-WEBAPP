/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;

/** An {@link InterfaceTest} for {@link ListInput}.
 * As factory classes override their default input to
 * not be a ListInput this is written to treat this case as an
 * automatic pass
 * 
 * @author spb
 *
 * @param <T>
 * @param <D>
 * @param <I>
 * @param <X>
 */

public interface ListInputInterfaceTest<T,D,I extends Input<T> , X extends TestDataProvider<T,I> >  {

	@Test
	public void testGetItembyValue() throws Exception ;
	
	@Test
	public void testGetItems() throws Exception; 
	@Test
	public void testGetTagByItem() throws Exception ;
	@Test
	public void testGetTagByValue() throws Exception;
	@Test
	public void testGetText() throws Exception ;
	
}
