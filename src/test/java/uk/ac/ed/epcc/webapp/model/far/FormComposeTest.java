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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.handler.TextHandler;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class FormComposeTest<D extends DynamicForm> extends AbstractTransitionServletTest {

	/**
	 * 
	 */
	public FormComposeTest() {
		
	}

	
	@Test
	public void testCreateForm() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
		manager.getChildManager().getChildManager().getChildManager();
		DynamicFormTransitionProvider<D> provider = manager.getDynamicFormProvider();
		takeBaseline();
		setTransition(provider, provider.CREATE, null);
		checkFormContent(null,"create_form_content.xml");
		addParam(DynamicFormManager.NAME_FIELD, "FirstForm");
		runTransition();
		D new_form = manager.findFromString("FirstForm");
		checkViewRedirect(provider, new_form);
		checkDiff("/cleanup.xsl", "create_form.xml");
	}
	
	@Test
	@DataBaseFixtures("create_form.xml")
	public void testDuplicateName() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
				manager.getChildManager().getChildManager().getChildManager();
		DynamicFormTransitionProvider<D> provider = manager.getDynamicFormProvider();
		takeBaseline();
		setTransition(provider, provider.CREATE, null);
		addParam(DynamicFormManager.NAME_FIELD, "FirstForm");
		runTransition();
		checkError(DynamicFormManager.NAME_FIELD, "Name FirstForm already in use");
	}
	
	@Test
	@DataBaseFixtures("create_form.xml")
	public void AddPage() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
				manager.getChildManager().getChildManager().getChildManager();
		DynamicFormTransitionProvider<D> provider = manager.getDynamicFormProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		takeBaseline();
		setTransition(provider, provider.ADD, form);
		checkFormContent(null,"add_page_content.xml");
		addParam(PartManager.NAME_FIELD, "FirstPage");
		runTransition();
		
		Page page = manager.getChildManager().findByParentAndName(form, "FirstPage");
		checkViewRedirect(manager.getPartPathProvider(), page);
		checkDiff("/cleanup.xsl", "add_page.xml");
	}
	
	@Test
	@DataBaseFixtures({"create_form.xml","add_page.xml"})
	public void AddDuplicatePage() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
				manager.getChildManager().getChildManager().getChildManager();
		DynamicFormTransitionProvider<D> provider = manager.getDynamicFormProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		takeBaseline();
		setTransition(provider, provider.ADD, form);
		addParam(PartManager.NAME_FIELD, "FirstPage");
		runTransition();
		checkError(PartManager.NAME_FIELD,"Name already in use");
		
	}
	@Test
	@DataBaseFixtures({"create_form.xml","add_page.xml"})
	public void AddSection() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
				manager.getChildManager().getChildManager().getChildManager();
		PartPathTransitionProvider provider = manager.getPartPathProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		PageManager page_manager = manager.getChildManager();
		Page page = page_manager.findByParentAndName(form, "FirstPage");
		assertNotNull(page);
		
		SectionManager section_manager = (SectionManager) page_manager.getChildManager();
		section_manager.getConfigFactory(); // create config table too
		takeBaseline();
		setTransition(provider, PartPathTransitionProvider.CREATE, page);
		addParam(PartManager.NAME_FIELD, "FirstSection");
		runTransition();
		
		Section section = section_manager.findByParentAndName(page, "FirstSection");
		checkViewRedirect(provider, section);
		checkDiff("/cleanup.xsl", "add_section.xml");
	}
	@Test
	@DataBaseFixtures({"create_form.xml","add_page.xml","add_section.xml"})
	public void AddQuestion() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
		manager.getChildManager().getChildManager().getChildManager();
		PartPathTransitionProvider provider = manager.getPartPathProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		PageManager page_manager = manager.getChildManager();
		Page page = page_manager.findByParentAndName(form, "FirstPage");
		assertNotNull(page);
		SectionManager section_manager = (SectionManager) page_manager.getChildManager();
		Section section = section_manager.findByParentAndName(page, "FirstSection");
		assertNotNull(section);
		QuestionManager question_manger = (QuestionManager) section_manager.getChildManager();
		// make the config factory as well.
		question_manger.getConfigFactory();
		takeBaseline();
		setTransition(provider, PartPathTransitionProvider.CREATE, section);
		addParam(PartManager.NAME_FIELD, "FirstQuestion");
		addParam(QuestionManager.QUESTION_TEXT_FIELD,"Are you a skunk");
		addParam(QuestionManager.HANDLER_TYPE_FIELD,"TextQuestion");
		runTransition();
		
		Question question = question_manger.findByParentAndName(section, "FirstQuestion");
		checkForwardToTransition(provider, PartPathTransitionProvider.CONFIG, question);
		addParam(TextHandler.MAX_RESULT_CONF, "80");
		runTransition();
		checkViewRedirect(provider, question);
		checkDiff("/cleanup.xsl", "add_question.xml");
	}
	
	@Test
	@DataBaseFixtures({"create_form.xml","add_page.xml","add_section.xml","add_question.xml"})
	public void AddBooleanQuestion() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
		manager.getChildManager().getChildManager().getChildManager();
		PartPathTransitionProvider provider = manager.getPartPathProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		PageManager page_manager = manager.getChildManager();
		Page page = page_manager.findByParentAndName(form, "FirstPage");
		assertNotNull(page);
		SectionManager section_manager = (SectionManager) page_manager.getChildManager();
		Section section = section_manager.findByParentAndName(page, "FirstSection");
		assertNotNull(section);
		QuestionManager question_manger = (QuestionManager) section_manager.getChildManager();
		// make the config factory as well.
		question_manger.getConfigFactory();
		takeBaseline();
		setTransition(provider, PartPathTransitionProvider.CREATE, section);
		addParam(PartManager.NAME_FIELD, "SecondQuestion");
		addParam(QuestionManager.QUESTION_TEXT_FIELD,"Are you a skunk yes/no");
		addParam(QuestionManager.HANDLER_TYPE_FIELD,"BooleanQuestion");
		runTransition();
		
		Question question = question_manger.findByParentAndName(section, "SecondQuestion");
		checkViewRedirect(provider, question);
		checkDiff("/cleanup.xsl", "add_second_question.xml");
	}
	
	@Test
	@DataBaseFixtures({"create_form.xml","add_page.xml","add_section.xml","add_question.xml","add_second_question.xml"})
	public void AddFileQuestion() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> manager = new DynamicFormManager(getContext(), "TestForms");
		// create all tables
		manager.getChildManager().getChildManager().getChildManager();
		PartPathTransitionProvider provider = manager.getPartPathProvider();
		D form = manager.findFromString("FirstForm");
		assertNotNull(form);
		PageManager page_manager = manager.getChildManager();
		Page page = page_manager.findByParentAndName(form, "FirstPage");
		assertNotNull(page);
		SectionManager section_manager = (SectionManager) page_manager.getChildManager();
		Section section = section_manager.findByParentAndName(page, "FirstSection");
		assertNotNull(section);
		QuestionManager question_manger = (QuestionManager) section_manager.getChildManager();
		// make the config factory as well.
		question_manger.getConfigFactory();
		takeBaseline();
		setTransition(provider, PartPathTransitionProvider.CREATE, section);
		addParam(PartManager.NAME_FIELD, "ThirdQuestion");
		addParam(QuestionManager.QUESTION_TEXT_FIELD,"Upload file");
		addParam(QuestionManager.HANDLER_TYPE_FIELD,"FileQuestion");
		addParam(QuestionManager.OPTIONAL_FIELD,"true");
		runTransition();
		
		Question question = question_manger.findByParentAndName(section, "ThirdQuestion");
		checkForwardToTransition(provider, PartPathTransitionProvider.CONFIG, question);
		runTransition();
		checkViewRedirect(provider, question);
		checkDiff("/cleanup.xsl", "add_third_question.xml");
	}
	/** setup a valid operator with RootManager role
	 * 
	 * @throws DataException
	 */
	public AppUser setupPerson() throws DataException{
		SessionService sess = setupPerson("fred@example.com");
		sess.setTempRole(SessionService.ADMIN_ROLE);
		sess.setToggle(SessionService.ADMIN_ROLE,Boolean.TRUE);
		return sess.getCurrentPerson();
		
		
	}
}