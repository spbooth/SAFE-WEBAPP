//| Copyright - The University of Edinburgh 2015                            |
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