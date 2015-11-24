// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** We want to show the confirm page for the current transition.
 * 
 * This is used to implements confirm requests made by the action.
 * @author spb
 * @param <T> 
 * @param <K> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ConfirmTransitionResult.java,v 1.2 2014/09/15 14:30:21 spb Exp $")

public class ConfirmTransitionResult<T,K> extends ChainedTransitionResult<T, K> {
	
	 private final String type;
	 private final String args[];
	 public ConfirmTransitionResult(TransitionFactory<K, T> provider, T target, K key,String type, String args[]){
		 super(provider,target,key);
		 this.type=type;
		 this.args=args;
	 }
	 public String getType(){
		 return type;
	 }
	 public String[] getArgs(){
		 return args;
	 }
	 @Override
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitConfirmTransitionResult(this);
	}

}