// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 * @param <T> 
 * @param <K> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public abstract class AbstractViewIndexedTransitionProvider<T extends Indexed, K> extends
		AbstractIndexedTransitionProvider<T, K> implements ViewTransitionProvider<K, T>{

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getTopContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, T target,
			SessionService<?> sess) {
		return cb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#getLogContent(uk.ac.ed.epcc.webapp.content.ContentBuilder, java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public <X extends ContentBuilder> X getLogContent(X cb, T target,
			SessionService<?> sess) {
		return cb;
	}

	/**
	 * @param c
	 * @param fac
	 * @param target_name
	 */
	public AbstractViewIndexedTransitionProvider(AppContext c,
			IndexedProducer<? extends T> fac, String target_name) {
		super(c, fac, target_name);
	}

	public final String getText(K key){
		return key.toString();
	}
}
