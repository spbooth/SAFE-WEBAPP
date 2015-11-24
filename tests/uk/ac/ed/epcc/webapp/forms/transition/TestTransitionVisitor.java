// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
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