//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.BackResult;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPageResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** A {@link WebFormResultVisitor} to recursively execute any direct transitions in the chain.
 * Only explicit transitions (with a non null key) are considered
 * @author Stephen Booth
 *
 */
public class DirectOperationResultVisitor implements WebFormResultVisitor {
	/**
	 * @param conn
	 */
	public DirectOperationResultVisitor(AppContext conn) {
		super();
		this.conn = conn;
	}

	private final AppContext conn;
	private FormResult final_result=null;
	
	public FormResult getFinalResult() {
		return final_result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitChainedTransitionResult(uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult)
	 */
	@Override
	public <T, K> void visitChainedTransitionResult(ChainedTransitionResult<T, K> res) throws Exception {
		if( ! res.useURL()) {
			// Only consider not url chains
			TransitionFactory<K, T> fac = res.getProvider();
			T target = res.getTarget();
			K key = res.getTransition();
			if( key != null ) {
				Transition<T> t = fac.getTransition(target, key);
				FormResult next = t.getResult(new DirectOperationTransitionVisitor<>(conn, target));
				if( next != null ) {
					next.accept(this);
					// recursive call will have set result
					return;
				}
			}
		}
		final_result=res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitConfirmTransitionResult(uk.ac.ed.epcc.webapp.forms.result.ConfirmTransitionResult)
	 */
	@Override
	public <T, K> void visitConfirmTransitionResult(ConfirmTransitionResult<T, K> res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitMessageResult(uk.ac.ed.epcc.webapp.forms.result.MessageResult)
	 */
	@Override
	public void visitMessageResult(MessageResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitServeDataResult(uk.ac.ed.epcc.webapp.forms.result.ServeDataResult)
	 */
	@Override
	public void visitServeDataResult(ServeDataResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitBackResult(uk.ac.ed.epcc.webapp.forms.result.BackResult)
	 */
	@Override
	public void visitBackResult(BackResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor#visitCustomPage(uk.ac.ed.epcc.webapp.forms.result.CustomPageResult)
	 */
	@Override
	public void visitCustomPage(CustomPageResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor#visitForwardResult(uk.ac.ed.epcc.webapp.forms.html.ForwardResult)
	 */
	@Override
	public void visitForwardResult(ForwardResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor#visitRedirectResult(uk.ac.ed.epcc.webapp.forms.html.RedirectResult)
	 */
	@Override
	public void visitRedirectResult(RedirectResult res) throws Exception {
		final_result=res;

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor#visitErrorFormResult(uk.ac.ed.epcc.webapp.forms.html.ErrorFormResult)
	 */
	@Override
	public void visitErrorFormResult(ErrorFormResult res) throws Exception {
		final_result=res;

	}

}
