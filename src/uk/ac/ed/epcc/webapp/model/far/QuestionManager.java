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
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ClassInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.jdbc.table.BooleanFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;
import uk.ac.ed.epcc.webapp.model.far.handler.QuestionFormHandler;

/**
 * @author spb
 *
 */

public class QuestionManager extends PartManager<SectionManager.Section,QuestionManager.Question> {

	/**
	 * 
	 */
	public static final String OPTIONAL_FIELD = "Optional";
	/**
	 * 
	 */
	public static final String HANDLER_TYPE_FIELD = "HandlerType";
	/**
	 * 
	 */
	public static final String QUESTION_TEXT_FIELD = "QuestionText";
	public class Question extends PartManager.Part<SectionManager.Section> implements UIGenerator{

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
		private QuestionFormHandler handler = null;
		@SuppressWarnings("unchecked")
		public QuestionFormHandler getHandler() throws Exception{
			if( handler == null ){
				AppContext conn = getContext();
				Class<? extends QuestionFormHandler> clazz = conn.getClassDef(QuestionFormHandler.class, record.getStringProperty(HANDLER_TYPE_FIELD));
				handler =  conn.makeObject(clazz);
			}
			return handler;
		}

		@Override
		public Map<String, Object> getInfo() {
			Map<String, Object> info = super.getInfo();
			info.put("Handler", record.getStringProperty(HANDLER_TYPE_FIELD));
			info.put("Question text", getQuestionText());
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
			return "Question";
		}
	}
	/**
	 * @param owner_fac
	 */
	public QuestionManager(SectionManager owner_fac) {
		super(owner_fac.form_manager,owner_fac, "Question");
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Question(res);
	}
	@Override
	public Class<? super Question> getTarget() {
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
		spec.setField(HANDLER_TYPE_FIELD, new StringFieldType(false, "", 128));
		spec.setField(OPTIONAL_FIELD, new BooleanFieldType(false, false));
		return spec;
	}
	@Override
	protected PartConfigFactory<Section, Question> makeConfigFactory() {
		return new PartConfigFactory<SectionManager.Section, QuestionManager.Question>(this);
	}
	@Override
	protected Map<String, Object> getSelectors() {
		Map<String, Object> selectors = super.getSelectors();
		selectors.put(HANDLER_TYPE_FIELD, new ClassInput<QuestionFormHandler>(getContext(), QuestionFormHandler.class));
		return selectors;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return "Unicorns";  // no valid children.
	}

}