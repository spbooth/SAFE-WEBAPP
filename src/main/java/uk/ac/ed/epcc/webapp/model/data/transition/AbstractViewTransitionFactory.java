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
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ViewTransitionResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

public abstract class AbstractViewTransitionFactory<T, K extends TransitionKey<T>> extends AbstractTransitionFactory<T, K> implements ViewTransitionFactory<K, T>{

	public AbstractViewTransitionFactory(AppContext c) {
		super(c);
	}



	public <X extends ContentBuilder> X getLogContent(X cb, T target,
			SessionService<?> sess) {
		return cb;
	}

	
	public String getHelp(K key) {
		return getContext().expandText(key.getHelp());
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