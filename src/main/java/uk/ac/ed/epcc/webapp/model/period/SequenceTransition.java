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
import uk.ac.ed.epcc.webapp.forms.result.*;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.time.TimePeriod;

public class SequenceTransition<T extends TimePeriod,K> extends AbstractDirectTransition<T> implements GatedTransition<T>{
	private final boolean move_up;
	private final ViewTransitionFactory<K, T> tp;
	private final SequenceManager<T> fac;
	public SequenceTransition(ViewTransitionFactory<K, T>tp, SequenceManager<T> fac,boolean move_up){
		this.tp=tp;
		this.fac=fac;
		this.move_up=move_up;
	}
	
	@Override
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		T result = getNext(target);
		if( result == null ){
			return new WarningMessageResult("no_next_rate");
		}
		return new ViewTransitionResult<>(tp, result);
	}
	protected T getNext(T target) {
		return fac.getNextInSequence(target, move_up);
	}
	
	@Override
	public boolean allow(SessionService<?> serv, T target) {
		return getNext(target) != null;
	}
	
}