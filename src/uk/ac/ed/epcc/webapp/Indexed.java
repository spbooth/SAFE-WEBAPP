// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;
/** Interface for objects that can provide a unique positive id number.
 * Valid ID numbers should always be greater than zero.
 * 
 * @author spb
 *
 */
public interface Indexed {
  public int getID();
}