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