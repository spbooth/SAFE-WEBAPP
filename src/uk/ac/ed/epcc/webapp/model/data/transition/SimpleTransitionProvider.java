// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/** Abstract base class for building {@link TransitionProvider}s on {@link Indexed} objects.
 * that are keyed by {@link TransitionKey}
 * 
 * @author spb
 *
 * @param <T> target type
 * @param <K> 
 */
public abstract class SimpleTransitionProvider<T extends Indexed,K extends TransitionKey<T>> extends AbstractTransitionProvider<T, K> implements TransitionProvider<K, T> {

	 
    private final IndexedProducer<? extends T> fac;
    private final String target_name;
    public SimpleTransitionProvider(AppContext c,IndexedProducer<? extends T> fac,String target_name){
    	super(c);
    	this.fac=fac;
    	this.target_name=target_name;
    }
    

    public final String getID(T target) {
		return AbstractIndexedTransitionProvider.getIndexedID(getContext(),fac,target);
	}

	public final IndexedProducer<? extends T> getProducer(){
		return fac;
	}
	

	public final T getTarget(String id) {
		return AbstractIndexedTransitionProvider.getIndexedTarget(getContext(),fac,id);
	}

	
	public final String getTargetName(){
		return target_name;
	}

	
	
}