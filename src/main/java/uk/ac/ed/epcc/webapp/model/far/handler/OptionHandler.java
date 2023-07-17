// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;

/**
 * @author michaelbareford
 *
 */
public class OptionHandler implements QuestionFormHandler<String> {

	private static final String OPTIONS = "Options";
	
	@Override
	public Class<String> getTarget() {
		return String.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		TextInput options = new TextInput();
		options.setSingle(false);
		options.addValidator(new MaxLengthValidator(256));
		f.addInput(OPTIONS, "Options", options);
	}

	@Override
	public RadioButtonInput<String, String> parseConfiguration(Form f) {
		String options = (String) f.get(OPTIONS);
		SetInput<String> option_list = new SetInput<>();
		
		String option_array[] = options.split(",");
		for(String option : option_array){
		    option_list.addChoice(option, option);
		}
		
		return new RadioButtonInput<>(option_list);
	}

	@Override
	public Class<? extends ResponseDataManager> getDataClass() {
		return StringDataManager.class;
	}

	@Override
	public boolean hasConfig() {
		return true;
	}

}
