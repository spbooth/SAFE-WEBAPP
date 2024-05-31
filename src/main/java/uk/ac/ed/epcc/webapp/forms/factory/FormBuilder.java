package uk.ac.ed.epcc.webapp.forms.factory;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public interface FormBuilder {
	/** build the form. This may return false if the 
	 * form is a multi-safe form and is not complete yet.
	 * 
	 * @param f
	 * @param fixtures  fixed-values
	 * @param defaults  optional initial values for the form
	 * @return  true
	 * @throws DataFault
	 */
	public boolean buildForm(Form f,HashMap fixtures, Map<String,Object> defaults) throws DataFault;
	
}
