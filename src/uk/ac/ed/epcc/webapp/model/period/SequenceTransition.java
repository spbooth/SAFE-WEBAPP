package uk.ac.ed.epcc.webapp.model.period;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
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
	
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		T result = getNext(target);
		if( result == null ){
			return new MessageResult("no_next_rate");
		}
		return new ViewTransitionResult<T, K>(tp, result);
	}
	protected T getNext(T target) {
		return fac.getNextInSequence(target, move_up);
	}
	
	public boolean allow(SessionService<?> serv, T target) {
		return getNext(target) != null;
	}
	
}