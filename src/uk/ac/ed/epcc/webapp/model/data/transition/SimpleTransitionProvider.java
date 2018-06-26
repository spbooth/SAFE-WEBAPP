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
    public final IndexedProducer<? extends T> getProducer(){
		return fac;
	}
	
    @Override
    public final String getID(T target) {
		return AbstractIndexedTransitionProvider.getIndexedID(getContext(),fac,target);
	}

	
    @Override
	public final T getTarget(String id) {
		return AbstractIndexedTransitionProvider.getIndexedTarget(getContext(),fac,id);
	}

	@Override
	public final String getTargetName(){
		return target_name;
	}

	
	
}