//| Copyright - The University of Edinburgh 2016                            |
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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.ConstructedObjectInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.NameInputProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInputWrapper;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;

/** A {@link QuestionFormHandler} that selects names from a {@link NameInputProvider}
 * @author spb
 *
 */
public class ClassificationHandler extends AbstractContexed implements QuestionFormHandler<String> {

	/**
	 * 
	 */
	private static final String TABLE_FIELD = "table";
	public ClassificationHandler(AppContext conn) {
		super(conn);
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
		f.addInput(TABLE_FIELD, "Classifier", new ConstructedObjectInput<>(conn, NameInputProvider.class));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler#parseConfiguration(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public Input<String> parseConfiguration(Form f) {
		NameInputProvider fac = (NameInputProvider) f.getItem(TABLE_FIELD);
		Input<String> input = (Input<String>) fac.getNameInput();
		if( input instanceof OptionalListInput){
			return input;
		}
		if( input instanceof ListInput){
			return new OptionalListInputWrapper((ListInput)input);
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
