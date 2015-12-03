//| Copyright - The University of Edinburgh 2012                            |
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;


public   class TestTransitionVisitor<T> implements TransitionVisitor<T>{
	public TestTransitionVisitor(AppContext c,T target) {
		super();
		this.c=c;
		this.target=target;
	}

	private final AppContext c;
	private final T target;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doDirectTransition(uk.ac.ed.epcc.webapp.forms.transition.DirectTransition)
	 */
	public FormResult doDirectTransition(DirectTransition<T> t)
			throws TransitionException {
		assertNotNull(target);
		// TODO Auto-generated method stub
		return null;
	}
	public FormResult doDirectTargetlessTransition(DirectTargetlessTransition<T> t)
			throws TransitionException {
		assertNull(target);
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doFormTransition(uk.ac.ed.epcc.webapp.forms.transition.FormTransition)
	 */
	public FormResult doFormTransition(FormTransition<T> t)
			throws TransitionException {
		assertNotNull(target);
		Form f = new HTMLForm(c);
		t.buildForm(f, target, c);
		assertTrue("Must be at least one action",f.getActionNames().hasNext());
		return null;
	}

	public FormResult doValidatingFormTransition(ValidatingFormTransition<T> t)
			throws TransitionException {
		assertNotNull(target);
		Form f = new HTMLForm(c);
		t.buildForm(f, target, c);
		assertTrue("Must be at least one action",f.getActionNames().hasNext());
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor#doTargetLessTransition(uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition)
	 */
	public FormResult doTargetLessTransition(TargetLessTransition<T> t)
			throws TransitionException {
		assertNull(target);
		Form f = new HTMLForm(c);
		t.buildForm(f, c);
		
		return null;
	}
	
}