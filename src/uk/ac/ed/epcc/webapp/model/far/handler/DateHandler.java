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
@uk.ac.ed.epcc.webapp.Version("$Revision:$")
public class DateHandler implements QuestionFormHandler<Date> {

	private static final String FORMAT = "Format";
	
	@Override
	public Class<? super Date> getTarget() {
		return Date.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		SetInput<String> format_list = new SetInput<String>();
		String format_array[] = new DateInput().getFormats();
		for(String format : format_array){
			format_list.addChoice(format, format);
		}
		
		f.addInput(FORMAT, "Format", format_list);
	}

	@Override
	public Input<Date> parseConfiguration(Form f) {
		// todo: need to pass the selected format to the DateInput object
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
		return true;
	}

}
