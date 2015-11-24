/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;

/** Data provider that can test parse info
 * usually extends TestDataPtovider
 * 
 * @author spb
 * @param <T> type returned by input
 * @param <I> type of input
 *
 */
public interface TestParseDataProvider<T,I extends Input<T>> extends TestDataProvider<T,I> {
  public Set<String> getGoodParseData();
  public Set<String> getBadParseData();
  public boolean allowNull();
}
