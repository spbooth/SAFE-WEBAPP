package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
/** Abstract superclass for building {@link TransitionProvider}s.
 * 
 * @author spb
 *
 * @param <T>
 * @param <K>
 */
public abstract class AbstractTransitionProvider<T , K extends TransitionKey<T>> extends AbstractTransitionFactory<T,K> implements TransitionProvider<K,T>{
	
	public AbstractTransitionProvider(AppContext c) {
		super(c);
		
	}

	public <R> R accept(TransitionFactoryVisitor<R,T,K> vis) {
		return vis.visitTransitionProvider(this);
	}
	
	
}