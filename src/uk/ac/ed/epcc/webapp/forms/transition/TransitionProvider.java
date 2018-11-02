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
	
	@Override
	default public <R> R accept(TransitionFactoryVisitor<R,T,K> vis) {
		return vis.visitTransitionProvider(this);
	}
}