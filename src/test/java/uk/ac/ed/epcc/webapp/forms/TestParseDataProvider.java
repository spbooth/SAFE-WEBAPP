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

import uk.ac.ed.epcc.webapp.forms.inputs.Input;

/** Data provider that can test parse info
 * usually extends TestDataProvider
 * 
 * @author spb
 * @param <T> type returned by input
 * @param <I> type of input
 *
 */
public interface TestParseDataProvider<T,I extends Input<T>> extends TestDataProvider<T,I> {
  /** Get strings that should parse
   * 	
   * @return
   */
  public Set<String> getGoodParseData();
  /** Get strings that should not parse
   * 
   * @return
   */
  public Set<String> getBadParseData();
  /** it it ok to parse a null value (should set input value to null)
   * 
   * @return
   */
  public boolean allowNull();
}