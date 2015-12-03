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