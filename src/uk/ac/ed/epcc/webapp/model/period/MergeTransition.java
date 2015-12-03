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
package uk.ac.ed.epcc.webapp.model.period;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public class MergeTransition<T extends TimePeriod,K> extends AbstractDirectTransition<T> implements GatedTransition<T>{

	private final boolean move_up;
	private final ViewTransitionFactory<K, T> tp;
	private final SequenceManager<T> fac;
	public MergeTransition(ViewTransitionFactory<K, T> tp, SequenceManager<T> fac,boolean go_up){
		this.tp=tp;
		this.fac=fac;
		move_up=go_up;
	}
	public boolean allow(SessionService<?> serv, T target) {
		T peer = fac.getNextInSequence(target, move_up);
		if( peer == null){
			return false;
		}
		if( move_up){
			return fac.canMerge(target, peer); 
		}else{
			return fac.canMerge(peer, target);
		}
	}
	
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		try{
		T peer = fac.getNextInSequence(target, move_up);
		if( move_up){
			return new ViewTransitionResult<T, K>(tp, fac.merge(target, peer)); 
		}else{
			return new ViewTransitionResult<T, K>(tp, fac.merge(peer,target)); 
		}
		}catch(Throwable t){
			tp.getContext().error(t,"Error in merge");
			throw new TransitionException("Internal error");
		}
	}
	
}