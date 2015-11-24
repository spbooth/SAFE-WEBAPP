// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Targetted;
/** A Labeller is a class that maps values used to classify plot/table 
 * data into groups onto s string that should be used to label 
 * the group.
 * These can support many to one mappings so classes that use this should
 * combine data that maps to the same label.
 * 
 * @see Transform
 * 
 * @author spb
 *
 * @param <T> type of input object
 * @param <R> type of return object.
 */
public interface Labeller<T,R> extends Targetted<R>{
	/** generate the label.
	 * Normally this just returns a String but if a non-string object is returned then
	 * the toString method is used to generate the label. 
	 * This is to allow labels with a different sort-order from their string representation
	 * 
	 * @param conn AppContext
	 * @param key  value to be labelled
	 * @return label object.
	 */
   R getLabel(AppContext conn, T key);
   
   /** run-time type-check if this labeller can handle an object as input
    * 
    * @param o
    * @return boolean
    */
   public boolean accepts(Object o);
}