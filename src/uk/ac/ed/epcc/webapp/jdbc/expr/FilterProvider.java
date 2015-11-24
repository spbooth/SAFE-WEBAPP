// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** An object that can attempt provide an explicit SQL filter for its property.
 * 
 * Normally implemented by a {@link Accessor} or {@link SQLValue}
 * This allows the object to be used to construct complex SQL filters without 
 * implementing SQLExpression. Some filters may still be impossible in which case
 * an exception is thrown. Higher levels of the code might still be able to make a filter by
 * introducing a non SQL filter.
 * 
 * Note that a {@link CannotFilterException} implies no filtering is possible
 * A {@link NoSQLFilterException} implies that only an SQL filter is impossible. Throwing a {@link NoSQLFilterException}
 * should give the same behaviour as not implementing the interface at all.
 * 
 * @author spb
 *
 * @param <T> type of target
 * @param <D> type of data to compare against
 */
public interface FilterProvider<T, D> extends Targetted<D> {
	/** Create a {@link SQLFilter} comparing against the target value
	 * 
	 * @param match
	 * @param val
	 * @return {@link SQLFilter}
	 * @throws CannotFilterException
	 * @throws NoSQLFilterException
	 */
  public SQLFilter<T> getFilter(MatchCondition match, D val) throws CannotFilterException, NoSQLFilterException;
 
  /** create a {@link SQLFilter} checking if the target value is or is not null.
 * @param is_null 
 * @return {@link SQLFilter}
 * @throws CannotFilterException 
 * @throws NoSQLFilterException 
   * 
   */
  public SQLFilter<T> getNullFilter(boolean is_null) throws CannotFilterException, NoSQLFilterException;
  
  /** create a {@link SQLFilter} that orders results by the target value. This may involve joins so 
   * may not be just an {@link OrderFilter} though it will contain one.
   * 
   * @param descending
   * @return {@link SQLFilter}
   * @throws CannotFilterException
   * @throws NoSQLFilterException
   */
  public SQLFilter<T> getOrderFilter(boolean descending) throws CannotFilterException, NoSQLFilterException;
  
  /** get the type bounds on the produced {@link SQLFilter}.
   * 
   * @return Class
   */
  public Class<? super T> getFilterType();
}