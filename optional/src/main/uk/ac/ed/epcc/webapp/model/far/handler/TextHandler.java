//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;
import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;

/** A {@link QuestionFormHandler} for text input questions
 * @author spb
 *
 */

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
		
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<String> getTarget() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#buildConfigForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void buildConfigForm(Form f) {
		
		f.addInput(SINGLE_CONF, "Force single line", new BooleanInput());
		IntegerInput max_input = new IntegerInput();
		max_input.setMin(1);
		f.addInput(MAX_RESULT_CONF, "Maximum result length", max_input);
		f.getField(MAX_RESULT_CONF).setOptional(true);
		IntegerInput max_box = new IntegerInput();
		max_box.setMin(1);
		f.addInput(BOX_LENGTH_CONF, "Box length", max_box);
		f.getField(BOX_LENGTH_CONF).setOptional(true);
		
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
			input.addValidator(new MaxLengthValidator(max_result));
		}
		Integer max_box = (Integer) f.get(BOX_LENGTH_CONF);
		if( max_box != null ){
			input.setBoxWidth(max_box);
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