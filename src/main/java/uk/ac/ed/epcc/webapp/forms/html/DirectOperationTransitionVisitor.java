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
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.*;

/** A {@link TransitionVisitor} that executes direct transitions and returns the {@link FormResult}
 * A null value is returned for any other kind of {@link Transition}.
 * 
 * 
 * @author Stephen Booth
 * @see DirectOperationResultVisitor
 */
public class DirectOperationTransitionVisitor<X> implements TransitionVisitor<X> {
	/**
	 * @param target
	 */
	public DirectOperationTransitionVisitor(AppContext conn,X target) {
		super();
		this.conn=conn;
		this.target = target;
	}

	private final AppContext conn;
	private final X target;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doDirectTransition(uk.ac.ed.epcc.webapp.forms.transition.DirectTransition)
	 */
	@Override
	public FormResult doDirectTransition(DirectTransition<X> t) throws TransitionException {
		return t.doTransition(target, conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doDirectTargetlessTransition(uk.ac.ed.epcc.webapp.forms.transition.DirectTargetlessTransition)
	 */
	@Override
	public FormResult doDirectTargetlessTransition(DirectTargetlessTransition<X> t) throws TransitionException {
		return t.doTransition(conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doFormTransition(uk.ac.ed.epcc.webapp.forms.transition.FormTransition)
	 */
	@Override
	public FormResult doFormTransition(FormTransition<X> t) throws TransitionException {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doValidatingFormTransition(uk.ac.ed.epcc.webapp.forms.transition.ValidatingFormTransition)
	 */
	@Override
	public FormResult doValidatingFormTransition(ValidatingFormTransition<X> t) throws TransitionException {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doTargetLessTransition(uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition)
	 */
	@Override
	public FormResult doTargetLessTransition(TargetLessTransition<X> t) throws TransitionException {
		return null;
	}
	@Override
	public FormResult doModalTransition(ModalTransition<X> t) throws TransitionException {
		if( target == null ){
			throw new TransitionException("No target specified");
		}
		if( t.useDirect(target)) {
			return doDirectTransition(t);
		}else {
			return doFormTransition(t);
		}
	}
}
