//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;

/** A Form result generating a View Transition.
 * 
 * This is essentially a {@link ChainedTransitionResult} with a null key but with the destination 
 * a "bookmarkable" location so in HTML it uses a redirect not a forward. The {@link TransitionServlet}
 * will convert this to a plain {@link ChainedTransitionResult} before processing. In non web contexts the
 * two representations are identical. 
 * @author spb
 *
 * @param <T> type of transition
 * @param <K> type of transition key
 */


public class ViewTransitionResult<T,K> extends ChainedTransitionResult<T, K> {

	@Override
	public boolean useURL() {
		return true;
	}

	public ViewTransitionResult(ViewTransitionFactory<K, T> provider, T target) {
		super(provider, target, null);
	}

	/** Alternate constructor that performs a lookup of the {@link ViewTransitionFactory}
	 * 
	 * This is preferable if an instance is not already available as it can utilise
	 * any cached copy.
	 * 
	 * @param conn
	 * @param template
	 * @param tag
	 * @param target
	 */
	public ViewTransitionResult(AppContext conn,Class<? extends ViewTransitionFactory> template,String tag, T target) {
		super(TransitionFactoryFinder.getTransitionFactory(conn, template, tag),target,null);
	}
}