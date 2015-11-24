package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;

public abstract class AbstractViewTransitionProvider<T, K extends TransitionKey<T>> extends AbstractViewTransitionFactory<T, K> implements ViewTransitionProvider<K, T>{

	
	public AbstractViewTransitionProvider(AppContext c) {
		super(c);
	}

	public <R> R accept(TransitionFactoryVisitor<R,T, K> vis) {
		return vis.visitTransitionProvider(this);
	}

}
