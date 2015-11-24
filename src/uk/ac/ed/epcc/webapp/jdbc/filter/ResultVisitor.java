// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;
/** Interface for classes that visit the results of an iterator as they are produced
 * 
 * @author spb
 *
 * @param <T> type of target
 */
public interface ResultVisitor<T> {
  public void visit(T target);
}