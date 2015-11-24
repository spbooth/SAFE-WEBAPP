// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.Targetted;

/** Interface that encodes data access operation on a target object.
 * These are usually defined with a {@link uk.ac.ed.epcc.webapp.model.data.DataObject} as the target
 *<p>
 *An Accessor that can provide Filters based on the result type implements {@link FilterProvider}
 *<p>
 *An Accessor that also allows access via SQL implements {@link SQLAccessor}.
 * <p>  
 * Unless an Accessor implements one or the other it won't be able to be used to filter results. 
 *   
 * @author spb
 *
 * @param <T> type of return value
 * @param <R> target object
 */
public interface Accessor<T,R> extends Targetted<T>{
	//TODO add a getHostType method for type checking.
	/** get the value from the target object
	 * 
	 * @param r target object
	 * @return value
	 */
  public T getValue(R r);
  
  /** does this {@link Accessor} support setting values.
   * 
   * @return
   */
  public boolean canSet();
  /** Set value if supported otherwise throw a {@link UnsupportedOperationException}.
   * 
   * @param r 
   * @param value
   */
  public void setValue(R r, T value);
 
}