//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.stateful;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreatorTransition;
import uk.ac.ed.epcc.webapp.forms.stateful.ConstrainedFactory.ConstrainedObject;
import uk.ac.ed.epcc.webapp.forms.transition.AnonymousTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleViewTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Test transition provider with constraints
 * @author Stephen Booth
 *
 */
public class ConstraintProvider extends SimpleViewTransitionProvider<ConstrainedObject,TransitionKey<ConstrainedObject>> implements AnonymousTransitionFactory<TransitionKey<ConstrainedObject>, ConstrainedObject>, IndexTransitionFactory<TransitionKey<ConstrainedObject>, ConstrainedObject>{

	
	/**
	 * 
	 */
	public static final TransitionKey<ConstrainedObject> CREATE_KEY = new TransitionKey<ConstrainedObject>(ConstrainedObject.class, "Create");
	
	public static class CreateTransition extends FormCreatorTransition<ConstrainedObject>{

	
		public CreateTransition(AppContext conn) {
			super("ConstrainedObject", (new ConstrainedFactory(conn)).getFormCreator(conn));
		}
		
	}

	
	public ConstraintProvider(AppContext c) {
		super(c,new ConstrainedFactory(c),"Constrained");
		addTransition(CREATE_KEY, new CreateTransition(c));
	}

	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory#getIndexTransition()
	 */
	@Override
	public TransitionKey<ConstrainedObject> getIndexTransition() {
		return CREATE_KEY;
	}




	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#allowTransition(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean allowTransition(AppContext c, ConstrainedObject target, TransitionKey<ConstrainedObject> key) {
		return true;
	}




	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory#canView(java.lang.Object, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public boolean canView(ConstrainedObject target, SessionService<?> sess) {
		return true;
	}

	

	
}
