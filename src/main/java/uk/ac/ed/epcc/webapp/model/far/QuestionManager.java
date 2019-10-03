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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;
import uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler;

/**
 * @author spb
 *
 */

public class QuestionManager extends HandlerPartManager<SectionManager.Section,QuestionFormHandler,QuestionManager.Question> {

	/**
	 * 
	 */
	public static final String OPTIONAL_FIELD = "Optional";

	/**
	 * 
	 */
	public static final String QUESTION_TEXT_FIELD = "QuestionText";
	/**
	 * 
	 */
	public static final String QUESTION_TYPE_NAME = "Question";
	public class Question extends HandlerPartManager.HandlerPart<SectionManager.Section,QuestionFormHandler> implements UIGenerator{

		
		@Override
		public void makeConfigForm(Form f) {
			try {
				getHandler().buildConfigForm(f);
			} catch (Exception e) {
				getLogger().error("Problem building config form",e);
			}
		}

		/**
		 * @param r
		 */
		protected Question(Record r) {
			super(QuestionManager.this,r);
		}
		
		public String getQuestionText(){
			return record.getStringProperty(QUESTION_TEXT_FIELD);
		}
		void setQuestionText(String val){
			record.setProperty(QUESTION_TEXT_FIELD, val);
		}
		
		

		@Override
		public Map<String, Object> getInfo() {
			Map<String, Object> info = super.getInfo();
			info.put("Question text", getQuestionText());
			info.put("Optional", isOptional()?"Optional":"Required");
			return info;
		}

		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#visit(uk.ac.ed.epcc.webapp.model.far.PartVisitor)
		 */
		@Override
		public <X> X visit(PartVisitor<X> vis) {
			return vis.visitQuestion(this);
		}

		public boolean isOptional(){
			return record.getBooleanProperty(OPTIONAL_FIELD,false);
		}
		void setOptional(boolean optional){
			record.setProperty(OPTIONAL_FIELD, optional);
		}
		public <T> Input<T> getInput() throws Exception{
			
			QuestionFormHandler<T> handler = getHandler();
			Form f = new MapForm(getContext());
			if( handler.hasConfig()){
				handler.buildConfigForm(f);
				PartConfigFactory<Section, Question> config_fac = getConfigFactory();
				f.setContents(config_fac.getValues(this));	
			}
			return handler.parseConfiguration(f);
		}

		@Override
		public boolean hasConfig() {
			try {
				return super.hasConfig() && getHandler().hasConfig();
			} catch (Exception e) {
				getLogger().error("Error checking for config",e);
				return false;
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return QUESTION_TYPE_NAME;
		}

		
	}
	/**
	 * @param owner_fac
	 */
	public QuestionManager(SectionManager owner_fac) {
		super(owner_fac.form_manager,owner_fac, QUESTION_TYPE_NAME);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected Question makeBDO(Record res) throws DataFault {
		return new Question(res);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.HandlerPartManager.HandlerPart#getHandlerClass()
	 */
	@Override
	protected Class<QuestionFormHandler> getHandlerClass() {
		return QuestionFormHandler.class;
	}
	@Override
	public Class<Question> getTarget() {
		return Question.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory#getChildManager()
	 */
	@Override
	public PartManager makeChildManager() {
		// No child parts to questions.
		return null;
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(QUESTION_TEXT_FIELD, new StringFieldType(false, "", 4096));
		spec.setField(OPTIONAL_FIELD, new BooleanFieldType(false, false));
		return spec;
	}
	@Override
	protected PartConfigFactory<Section, Question> makeConfigFactory() {
		return new PartConfigFactory<>(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return "Unicorn";  // no valid children.
	}
	@Override
	public Question duplicate(Section new_owner, Question original) throws DataFault {
		Question duplicate = super.duplicate(new_owner, original);
		duplicate.setOptional(original.isOptional());
		duplicate.setQuestionText(original.getQuestionText());
		return duplicate;
	}

}