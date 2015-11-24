// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.convert;

import java.util.Iterator;
import java.util.Set;


public interface EnumeratingTypeConverter<T,D> extends TypeConverter<T, D> {
	 /** Get an iterator over all supported targets.
	   * 
	   * @return Iterator
	   */
	  public Iterator<T> getValues();
	  /** Add all supported targets to a set 
	   * 
	   * @param <X>
	   * @param set Set to be modified
	   * @return reference to set
	   */
	  public <X extends Set<T>> X getValues(X set);
}