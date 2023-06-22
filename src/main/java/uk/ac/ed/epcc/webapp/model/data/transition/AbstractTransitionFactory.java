//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.data.transition;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextCached;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
/** Abstract superclass for building {@link TransitionFactory}s.
 * 
 * This implementation stores the keys and transitions in maps generated when the class is constructed
 * so dynamic transitions are not supported. 
 * 
 * @author spb
 *
 * @param <T> type of transition
 * @param <K> type of transition key
 */

public abstract class AbstractTransitionFactory<T , K extends TransitionKey<T>> extends AbstractContexed implements ContextCached, TransitionFactory<K,T>{
	private Map<String,K> key_map=null;
	private Map<K,Transition<T>> transition_map=null;
	public AbstractTransitionFactory(AppContext c) {
		super(c);
		
    	
	}
	
	private final void checkTransitions() {
		if( key_map == null || transition_map == null) {
			key_map = new LinkedHashMap<>();
	    	if( sortByKey()) {
	    		transition_map = new TreeMap<>();
	    	}else {
	    		transition_map= new LinkedHashMap<>();
	    	}
	    	setupTransitions();
		}
	}
	
	/** Extension point to allow transition setup to be lazy and deferred until
	 * required. This method is called by the first call to {@link #checkTransitions()} 
	 * and can contain calls to {@link #addTransition(TransitionKey, Transition)}.
	 * 
	 * Transitions will be added lazily only if all calls to {@link #addTransition(TransitionKey, Transition)}
	 * are from this method call and not from the constructor.
	 */
	protected void setupTransitions() {
		
	}
	
	protected boolean sortByKey() {
		return false;
	}
	
	/** Register a new transition with the provider. 
	 * 
     * This is intended to be called either from the constructor of the sub-classes
     * so that all transitions are registered by the time the constructor returns or from
     * an overidden {@link #setupTransitions()} so that transitions are generated lazily the first time they are needed.
     * As this method calls {@link #checkTransitions()} itself if transitions are set in both locations all transitions will be added in
     * the constructor with the setup transitions added first.
     * @param key
     * @param t
     */
    protected final void addTransition(K key, Transition<T> t){
    	checkTransitions();
    	//getLogger().debug("adding transition "+key.toString());
    	key_map.put(key.getName(),key);
    	transition_map.put(key, t);
    }
    @Override
	public final K lookupTransition(T target, String name) {
    	checkTransitions();
		return key_map.get(name);
	}
    @Override
	public final Transition<T> getTransition(T target, K name) {
		return getTransition(name);
	}
    public final Transition<T> getTransition(K name) {
    	checkTransitions();
		return transition_map.get(name);
	}
	@Override
	public final Set<K> getTransitions(T target) {
		checkTransitions();
		LinkedHashSet<K> result = new LinkedHashSet<>();
		for( K key : transition_map.keySet()){
			Transition<T> t = transition_map.get(key);
			if( target == null ){
				if( t instanceof TargetLessTransition){
					result.add(key);
				}
			}else{
				if( ! (t instanceof TargetLessTransition)){
					result.add(key);
				}
			}
		}
		return result;
	}

}