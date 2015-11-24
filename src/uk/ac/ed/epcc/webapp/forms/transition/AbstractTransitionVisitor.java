// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

public abstract class AbstractTransitionVisitor<K,T> implements TransitionVisitor<T>, Contexed {
	protected final AppContext conn;
	protected final K tag;
	protected final TransitionFactory<K, T> provider;
	protected final T target;
	public AbstractTransitionVisitor(AppContext conn, K tag,
			TransitionFactory<K, T> tp, T target) {
		this.conn=conn;
		this.tag=tag;
		this.provider=tp;
		this.target=target;
	}
	public FormResult doDirectTransition(DirectTransition<T> t) throws TransitionException {
		if( target == null ){
			throw new TransitionException("No target specified");
		}
		return t.doTransition(target, conn);
	}
	public FormResult doDirectTargetlessTransition(DirectTargetlessTransition<T> t) throws TransitionException {
		return t.doTransition(conn);
	}
	public AppContext getContext() {
		return conn;
	}
}