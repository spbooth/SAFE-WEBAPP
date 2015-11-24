package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

public abstract class AbstractViewTransitionFactory<T, K extends TransitionKey<T>> extends AbstractTransitionFactory<T, K> implements ViewTransitionFactory<K, T>{

	public AbstractViewTransitionFactory(AppContext c) {
		super(c);
	}


	
	public <X extends ContentBuilder> X getTopContent(X cb, T target,
			SessionService<?> sess) {
		return cb;
	}

	
	public <X extends ContentBuilder> X getLogContent(X cb, T target,
			SessionService<?> sess) {
		return cb;
	}

	
	public String getHelp(K key) {
		return key.getHelp();
	}
	
	public String getText(K key){
		return key.toString();
	}
	/** a direct transtition to view the target.
	 * 
	 * @author spb
	 *
	 */
	public class ViewTransition extends AbstractDirectTransition<T>{
		public FormResult doTransition(T target, AppContext c)
				throws TransitionException {
			return new ViewResult(target);
		}
	}
	/** A standard {@link FormResult} to view the target.
	 * 
	 * @author spb
	 *
	 */
	public class ViewResult extends ViewTransitionResult<T, K>{
		public ViewResult(T target) {
			super(AbstractViewTransitionFactory.this, target);
		}
	}
}
