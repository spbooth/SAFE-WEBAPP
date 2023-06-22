package uk.ac.ed.epcc.webapp.forms.factory;

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public interface FormBuilder {
	/** build the form. This may return false if the 
	 * form is a multi-safe form and is not complete yet.
	 * 
	 * @param f
	 * @param fixtures
	 * @return  true
	 * @throws DataFault
	 */
	public boolean buildForm(Form f,HashMap fixtures) throws DataFault;
	
}
