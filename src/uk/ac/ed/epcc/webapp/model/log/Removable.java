// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/** Interface implemented by the target classes of Log Entry
 * If this interface is implemeted the class provides a method to be called when the
 * parent Entry is deleted.
 * 
 * @author spb
 *
 */
public interface Removable {
  public void remove() throws DataException;
}