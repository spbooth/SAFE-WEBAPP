// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import java.util.Date;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.DateInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;
import uk.ac.ed.epcc.webapp.model.far.response.DateDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;

/**
 * @author michaelbareford
 *
 */
public class DateHandler implements QuestionFormHandler<Date> {

	private static final String FORMAT = "Format";
	
	@Override
	public Class<? super Date> getTarget() {
		return Date.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		// We could configure min/max date here.
	}

	@Override
	public Input<Date> parseConfiguration(Form f) {
		
		DateInput input = new DateInput(); 
		input.setValue(new Date());
		return input;
	}

	@Override
	public Class<? extends ResponseDataManager> getDataClass() {
		return DateDataManager.class;
	}

	@Override
	public boolean hasConfig() {
		return false;
	}

}
