// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;




/**
 * A TransitionProvider is a {@link TransitionFactory} where the target objects can be 
 * mapped to and from a single String id. 
 * 
 * @author spb
 * @param <K> key type
 * @param <T> target type
 * 
 */
public interface TransitionProvider<K,T> extends TransitionFactory<K, T>{
	
	/** Find target type by id string
	 * 
	 * @param id
	 * @return target or null
	 */
	public T getTarget(String id);
	
	/** Get the id string for form posts from a target
	 * 
	 * @param target
	 * @return id value
	 */
	public String getID(T target);
	
}