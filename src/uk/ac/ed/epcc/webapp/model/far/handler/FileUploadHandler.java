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
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.FileInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StreamDataDataManager;

/** A {@link QuestionFormHandler} for text input questions
 * @author spb
 *
 */

public class FileUploadHandler implements QuestionFormHandler<StreamData> {



	/**
	 * 
	 */
	private static final String MAX_UPLOAD = "max_upload";

	

	/**
	 * 
	 */
	public FileUploadHandler() {
		// TODO Auto-generated constructor stub
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super StreamData> getTarget() {
		return StreamData.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#buildConfigForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void buildConfigForm(Form f) {
		
		IntegerInput input = new IntegerInput();
		input.setOptional(true);
		input.setMin(0);
		f.addInput(MAX_UPLOAD, "Maximum upload size",input);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#parseConfiguration(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public Input<StreamData> parseConfiguration(Form f) {
		FileInput input = new FileInput();
		
		Integer i = (Integer) f.get(MAX_UPLOAD);
		if( i != null && i > 0 ){
			input.setMaxUpload(i);
		}
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#getDataClass()
	 */
	@Override
	public Class<? extends ResponseDataManager> getDataClass() {

		return StreamDataDataManager.class;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#hasConfig()
	 */
	@Override
	public boolean hasConfig() {
		return true;
	}

}