// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import java.util.LinkedList;




/**
 * A PathTransitionProvider is a {@link TransitionFactory} where the target objects 
 * are represented as a location path. 
 * 
 * 
 * 
 * @author spb
 * @param <K> key type
 * @param <T> target type
 * 
 */
public interface PathTransitionProvider<K,T> extends TransitionFactory<K, T>{
	
	/** Find target type by id string
	 * 
	 * @param id
	 * @return target or null
	 */
	public T getTarget(LinkedList<String> id);
	
	/** Get the id string for form posts from a target
	 * 
	 * @param target
	 * @return id value
	 */
	public LinkedList<String> getID(T target);
	
}