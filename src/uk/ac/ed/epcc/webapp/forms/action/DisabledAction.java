// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;

/** A {@link FormAction} used to denote a disabled (but present input).
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class DisabledAction extends FormAction {

	/**
	 * 
	 */
	public DisabledAction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public FormResult action(Form f) throws ActionException {
		return new MessageResult("invalid_input");
	}

}
