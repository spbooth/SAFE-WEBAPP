// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;


/** Interface for classes that can generate a {@link TransitionFactory}.
 * This is intended for cases where a common set of transitions are implementeded
 * on different target objects. 
 * The {@link TransitionFactory} is parameterised by the target factory
 * so we use a 2 step creation process. 
 * 
 * @author spb
 * @param <P> type of TransitionProvider
 */
public interface TransitionFactoryCreator<P extends TransitionFactory> {
	public static final char TYPE_SEPERATOR = ':';

	/**
	 * 
	 * @param tag TargetName used for the TransitionProvider 
	 * @return TransitionProvider
	 */
   public P getTransitionProvider(String tag);
}