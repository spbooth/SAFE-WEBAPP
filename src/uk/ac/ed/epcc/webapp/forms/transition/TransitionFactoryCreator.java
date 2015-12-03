//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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