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

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public class MergeTransition<T extends TimePeriod,K> extends AbstractDirectTransition<T> implements TimeLocked<T>{

	protected final boolean move_up;
	private final ViewTransitionFactory<K, T> tp;
	private final SequenceManager<T> fac;
	public MergeTransition(ViewTransitionFactory<K, T> tp, SequenceManager<T> fac,boolean go_up){
		this.tp=tp;
		this.fac=fac;
		move_up=go_up;
	}
	@Override
	public boolean allow(SessionService<?> serv, T target) {
		T peer = fac.getMergeCandidate(target, move_up);
		if( peer == null){
			return false;
		}
		Date limit = fac.getEditLimit(serv);
		
		if( move_up){
			if( limit != null && target.getEnd().before(limit)) {
				return false;
			}
			return fac.canMerge(target, peer); 
		}else{
			if( limit != null && target.getStart().before(limit)) {
				return false;
			}
			return fac.canMerge(peer, target);
		}
	}
	@Override
	public boolean allowTimeBounds(SessionService<?> serv, T target) {
		Date limit = fac.getEditLimit(serv);
		if( limit == null) {
			return true;
		}
		if( move_up){
			if( limit != null && target.getEnd().before(limit)) {
				return false;
			} 
		}else{
			if( limit != null && target.getStart().before(limit)) {
				return false;
			}
		}
		return true;
	}
	@Override
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		try{
		T peer = fac.getMergeCandidate(target, move_up);
		if( move_up){
			return new ViewTransitionResult<>(tp, fac.merge(target, peer)); 
		}else{
			return new ViewTransitionResult<>(tp, fac.merge(peer,target)); 
		}
		}catch(Exception t){
			Logger.getLogger(getClass()).error("Error in merge",t);
			throw new TransitionException("Internal error");
		}
	}
	
}