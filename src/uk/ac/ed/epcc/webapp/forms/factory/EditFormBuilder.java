// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** interface for classes that build edit/update forms.
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface EditFormBuilder<T> extends FormFactory {

	/**
	 * Build a form for updating an object including the action buttons.
	 * @param name String name of target type to be presented to user
	 * 
	 * @param f
	 *            Form to build
	 * @param dat
	 *            Object we are editing.
	 *            
	 * @param operator
	 *             person editing the form
	 * @throws Exception
	 */
	public abstract void buildUpdateForm(String name, Form f, T dat,SessionService<?> operator)
			throws Exception;

}