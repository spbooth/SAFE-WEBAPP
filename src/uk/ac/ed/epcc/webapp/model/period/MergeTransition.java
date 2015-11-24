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