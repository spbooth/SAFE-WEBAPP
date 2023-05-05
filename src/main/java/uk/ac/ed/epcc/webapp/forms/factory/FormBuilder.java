package uk.ac.ed.epcc.webapp.forms.factory;

import java.util.HashMap;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public interface FormBuilder {
	public boolean buildForm(Form f,HashMap fixtures) throws DataFault;
}
