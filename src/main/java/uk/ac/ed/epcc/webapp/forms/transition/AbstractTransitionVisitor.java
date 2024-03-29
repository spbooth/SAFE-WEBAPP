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
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

public abstract class AbstractTransitionVisitor<K,T> extends AbstractContexed implements TransitionVisitor<T> {
	
	protected final K tag;
	protected final TransitionFactory<K, T> provider;
	protected final T target;
	public AbstractTransitionVisitor(AppContext conn, K tag,
			TransitionFactory<K, T> tp, T target) {
		super(conn);
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
	public FormResult getSelf() {
		return new ChainedTransitionResult<T, K>(provider, target, tag);
	}
}