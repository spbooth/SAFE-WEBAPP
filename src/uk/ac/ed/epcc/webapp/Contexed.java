// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;
/** Interface for objects that can provide an AppContext.
 * 
 * When creating objects that implement this interface
 * an {@link AppContext} will look for 
 * constructor of the form
 * <code>Constructor(AppContext conn)</code>
 * and/or 
 * <code>Constructor(AppContext conn, String tag)</code>
 * 
 * so these constructor signatures should be provided if possible.
 * @author spb
 *
 */
public interface Contexed {
   public AppContext getContext();
}