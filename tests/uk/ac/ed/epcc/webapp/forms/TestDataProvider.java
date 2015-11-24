/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Set;

import junit.framework.TestCase;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
/** Interface for {@link TestCase}s that provide test data
 * 
 * @author spb
 *
 * @param <T>  target type of input
 * @param <I>  type of input
 */
public interface TestDataProvider<T,I extends Input<T>> {
	/** Get a set of good output data.
	 * Note that this should correspond exactly with the
	 * data the input will return. For String data that gets normalised
	 * on parse make sure this method returns the normalised form.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Set<T> getGoodData() throws Exception;
	public abstract Set<T> getBadData() throws Exception;
	public abstract I getInput() throws Exception;
	
	
}
