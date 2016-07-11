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
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager;
import uk.ac.ed.epcc.webapp.model.far.PageManager;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.PartVisitor;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager.ResponseData;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;

/** visitor to check if the response to a part is complete.
 * @author spb
 *
 */

public class CompleteVisitor<D extends DynamicForm, R extends Response<D>> implements PartVisitor<Boolean> {

	/**
	 * @param response
	 */
	public CompleteVisitor(R response) {
		super();
		this.response = response;
	}

	private final R response;

	public Boolean visitForm(D f) {
		DynamicFormManager<D>fac = f.getManager();
		try {
			for(Page p : ((PageManager)fac.getChildManager()).getParts(f)){
				if( ! visitPage(p)){
					return Boolean.FALSE;
				}
			}
		} catch (DataFault e) {
			getLogger()	.error("Error checking completness", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitPage(uk.ac.ed.epcc.webapp.model.far.PageManager.Page)
	 */
	@Override
	public Boolean visitPage(Page p) {
		PageManager fac = (PageManager) p.getFactory();
		try {
			for(Section s : ((SectionManager)fac.getChildManager()).getParts(p)){
				if( ! visitSection(s)){
					return Boolean.FALSE;
				}
			}
		} catch (DataFault e) {
			getLogger()	.error("Error checking completness", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitSection(uk.ac.ed.epcc.webapp.model.far.SectionManager.Section)
	 */
	@Override
	public Boolean visitSection(Section s) {
		SectionManager fac = (SectionManager) s.getFactory();
		try {
			Form f = new BaseForm(s.getContext());
			for(Question q : ((QuestionManager)fac.getChildManager()).getParts(s)){
				if( ! visitQuestion(q)){
					return Boolean.FALSE;
				}
				Input input = q.getInput();
				if( input instanceof OptionalInput){
					((OptionalInput)input).setOptional(q.isOptional());
				}
				input.setValue(response.getData(q));
				f.addInput(q.getName(), q.getQuestionText(), input);
			}
			return f.validate();
		} catch (Exception e) {
			getLogger()	.error("Error checking completness", e);
			return Boolean.FALSE;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartVisitor#visitQuestion(uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question)
	 */
	@Override
	public Boolean visitQuestion(Question q) {
		if( q.isOptional()){
			return Boolean.TRUE;
		}
		ResponseData<Object, ? extends Response<D>,D> wrapper;
		try {
			wrapper = response.getWrapper(q);
			if( wrapper == null){
				return Boolean.FALSE;
			}
			return wrapper.hasData();
		} catch (Exception e) {
			getLogger()	.error("Error checking completness", e);
			return Boolean.FALSE;
		}
		
	}

	/**
	 * @return
	 */
	protected Logger getLogger() {
		return response.getContext().getService(LoggerService.class).getLogger(getClass());
	}
	
	
}