// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.RadioButtonInput;
import uk.ac.ed.epcc.webapp.forms.inputs.SetInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;

/**
 * @author michaelbareford
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision:$")
public class OptionHandler implements QuestionFormHandler<String> {

	private static final String OPTIONS = "Options";
	
	@Override
	public Class<? super String> getTarget() {
		return String.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		TextInput options = new TextInput();
		options.setOptional(false);
		options.setSingle(false);
		options.setMaxResultLength(256);
		f.addInput(OPTIONS, "Options", options);
	}

	@Override
	public RadioButtonInput<String, String> parseConfiguration(Form f) {
		String options = (String) f.get(OPTIONS);
		SetInput<String> option_list = new SetInput<String>();
		
		String option_array[] = options.split(",");
		for(String option : option_array){
		    option_list.addChoice(option, option);
		}
		
		return new RadioButtonInput<String, String>(option_list);
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
