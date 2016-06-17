// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.far.response.IntegerDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;

/**
 * @author michaelbareford
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision:$")
public class IntegerHandler implements QuestionFormHandler<Integer> {

	private static final String STEP = "step";
	private static final String MAXIMUM = "maximum";
	private static final String MINIMUM = "minimum";
	private static final String UNIT = "unit";

	@Override
	public Class<? super Integer> getTarget() {
		return Integer.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		IntegerInput min_input = new IntegerInput();
		min_input.setOptional(true);
		f.addInput(MINIMUM, "Minimum legal value", min_input);
		
		IntegerInput max_input = new IntegerInput();
		max_input.setOptional(true);
		f.addInput(MAXIMUM, "Maximum legal value", max_input);
		
		IntegerInput step_input = new IntegerInput();
		step_input.setMin(1);
		step_input.setOptional(true);
		f.addInput(STEP, "Step size", step_input);
		
		TextInput unit_input = new TextInput();
		unit_input.setOptional(true);
		unit_input.setSingle(true);
		unit_input.setMaxResultLength(16);
		f.addInput(UNIT, "Unit", unit_input);
		
		f.addValidator(new FormValidator() {
			
			@Override
			public void validate(Form f) throws ValidateException {
				Integer min = (Integer) f.get(MINIMUM);
				Integer max = (Integer) f.get(MAXIMUM);
				if( min != null && max != null && min.intValue() > max.intValue()){
					throw new ValidateException("Minimum must be less than maximum");
				}
				
				Integer step = (Integer) f.get(STEP);
				double rem = (max.intValue() - min.intValue()) % step.intValue();
				if( rem != 0){
					throw new ValidateException("Maximum minus Minimum must be a multiple of step");
				}
			}
			
		});
	}

	@Override
	public Input<Integer> parseConfiguration(Form f) {
		IntegerInput input = new IntegerInput();
		input.setMin((Integer) f.get(MINIMUM)); 
		input.setMax((Integer) f.get(MAXIMUM));
		input.setStep((Integer) f.get(STEP));
		input.setUnit((String)f.get(UNIT));
		return input;
	}

	@Override
	public Class<? extends ResponseDataManager> getDataClass() {
		return IntegerDataManager.class;
	}

	@Override
	public boolean hasConfig() {
		return true;
	}

}
