// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;

/** A {@link QuestionFormHandler} for text input questions
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class TextHandler implements QuestionFormHandler<String> {

	/**
	 * 
	 */
	public static final String BOX_LENGTH_CONF = "box_length";
	/**
	 * 
	 */
	public static final String MAX_RESULT_CONF = "max_result";
	/**
	 * 
	 */
	public static final String SINGLE_CONF = "single";
	
	

	/**
	 * 
	 */
	public TextHandler() {
		// TODO Auto-generated constructor stub
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super String> getTarget() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#buildConfigForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void buildConfigForm(Form f) {
		
		f.addInput(SINGLE_CONF, "Force single line", new BooleanInput());
		IntegerInput max_input = new IntegerInput();
		max_input.setOptional(true);
		max_input.setMin(1);
		f.addInput(MAX_RESULT_CONF, "Maximum result length", max_input);
		IntegerInput max_box = new IntegerInput();
		max_box.setOptional(true);
		max_box.setMin(1);
		f.addInput(BOX_LENGTH_CONF, "Box length", max_box);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#parseConfiguration(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public Input<String> parseConfiguration(Form f) {
		TextInput input = new TextInput();
		
		input.setSingle((Boolean)f.get(SINGLE_CONF));
		Integer max_result = (Integer) f.get(MAX_RESULT_CONF);
		if( max_result != null ){
			input.setMaxResultLength(max_result);
		}
		Integer max_box = (Integer) f.get(BOX_LENGTH_CONF);
		if( max_box != null ){
			input.setMaxResultLength(max_box);
		}
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#getDataClass()
	 */
	@Override
	public Class<? extends ResponseDataManager> getDataClass() {

		return StringDataManager.class;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#hasConfig()
	 */
	@Override
	public boolean hasConfig() {
		return true;
	}

}
