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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.PageManager.Page;
import uk.ac.ed.epcc.webapp.model.far.QuestionManager.Question;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;
import uk.ac.ed.epcc.webapp.model.far.response.CompleteVisitor;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseTarget;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseTransitionProvider;
import uk.ac.ed.epcc.webapp.model.far.response.personal.PersonalResponseManager;
import uk.ac.ed.epcc.webapp.model.far.response.personal.PersonalResponseTransitionProvider;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Tests of form completion
 * @author spb
 * @param <D> 
 * @param <R> 
 *
 */

@DataBaseFixtures({"create_form.xml","add_page.xml","add_section.xml","add_question.xml"})
public class FormCompleteTest<D extends DynamicForm,R extends PersonalResponseManager.PersonalResponse<D>> extends AbstractTransitionServletTest {

	/**
	 * 
	 */
	public FormCompleteTest() {
		
	}

	
	@Test
	public void testCreateResponse() throws Exception{
		setTime(2023, Calendar.NOVEMBER, 24, 21, 20);
		takeBaseline();
		AppUser p = setupPerson();
		DynamicFormManager<D> form_manager = new DynamicFormManager(getContext(), "TestForms");
		D form = form_manager.findFromString("FirstForm");
		
		PersonalResponseManager<R, D> response_manager = new PersonalResponseManager<>(getContext(),"TestResponse" );
		PersonalResponseTransitionProvider provider = response_manager.getPersonalResponseTransitionProvider();
		
		
		setTransition(provider, PersonalResponseTransitionProvider.CREATE, null);
		checkFormContent(null,"create_response_content.xml");
		addParam(PersonalResponseTransitionProvider.CreateTransition.FORM_INPUT_FIELD, form);
		runTransition();
	
		R response = response_manager.getResponse(form);
		assertNotNull(response);
		// should go to view of first page
		Page first = form_manager.getChildManager().getFirst(form);
		checkViewRedirect(response_manager.getPathResponseProvider(), new ResponseTarget(response, first));
		checkDiff("/cleanup.xsl", "create_response.xml");
	}
	@Test
	@DataBaseFixtures("create_response.xml")
	public void testEditSection() throws Exception{
		AppUser p = setupPerson();
		DynamicFormManager<D> form_manager = new DynamicFormManager(getContext(), "TestForms");
		D form = form_manager.findFromString("FirstForm");
		
		PersonalResponseManager<R, D> response_manager = new PersonalResponseManager<>(getContext(),"TestResponse" );
		
		R response = response_manager.getResponse(form);
		assertNotNull(response);
		
		
		
		// should go to view of first page
		PageManager page_manager = form_manager.getChildManager();
		Page first = page_manager.getFirst(form);
		SectionManager section_manger = (SectionManager) page_manager.getChildManager();
		Section first_section = section_manger.getFirst(first);
		ResponseTransitionProvider provider = response_manager.getPathResponseProvider();
		
		
		Question q = (Question) section_manger.getChildManager().getFirst(first_section);
		// ensure tables are created before baseline
		response_manager.getWrapper(q, response);
		
		takeBaseline();
		setTransition(provider, provider.EDIT, new ResponseTarget(response, first_section));
		
		addParam(q.getName(), "Yes I stink");
		setAction(ResponseTransitionProvider.EditSectionTransition.SAVE_ACTION);
		runTransition();
        // this should also be last section so will go to view page
		checkViewRedirect(provider, new ResponseTarget(response, first));
		checkDiff("/cleanup.xsl", "answer_question.xml");
	}
	
	
	
	/** setup a valid operator 
	 * 
	 * @throws DataException
	 */
	public AppUser setupPerson() throws DataException{
		SessionService sess = setupPerson("fred@example.com");
		return sess.getCurrentPerson();
		
		
	}


	@Test
		@DataBaseFixtures({"create_response.xml","add_second_question.xml","add_third_question.xml"})
		public void testEditCompleteSection() throws Exception{
			AppUser p = setupPerson();
			DynamicFormManager<D> form_manager = new DynamicFormManager(getContext(), "TestForms");
			D form = form_manager.findFromString("FirstForm");
			
			PersonalResponseManager<R, D> response_manager = new PersonalResponseManager<>(getContext(),"TestResponse" );
			
			R response = response_manager.getResponse(form);
			assertNotNull(response);
			
			
			
			// should go to view of first page
			PageManager page_manager = form_manager.getChildManager();
			Page first = page_manager.getFirst(form);
			SectionManager section_manger = (SectionManager) page_manager.getChildManager();
			Section first_section = section_manger.getFirst(first);
			ResponseTransitionProvider provider = response_manager.getPathResponseProvider();
			
			
			QuestionManager question_manager = (QuestionManager) section_manger.getChildManager();
			Question q1 = (Question) question_manager.getFirst(first_section);
			// ensure tables are created before baseline
			response_manager.getWrapper(q1, response);
			Question q2 = question_manager.getSibling(q1, true);
			assertNotNull(q2);
			response_manager.getWrapper(q2, response);
			Question q3 = question_manager.getSibling(q2, true);
			assertNotNull(q3);
			response_manager.getWrapper(q3, response);
			
			takeBaseline();
			setTransition(provider, provider.EDIT, new ResponseTarget(response, first_section));
			
			addParam(q1.getName(), "Yes I stink");
			addParam(q2.getName(), "true");
			setAction(ResponseTransitionProvider.EditSectionTransition.SAVE_ACTION);
			ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
			
// Can't mock file-uploads yet
//			PrintWriter writer = new PrintWriter(msd.getOutputStream());
//			writer.print("hello world");
//			writer.close();
//			msd.setMimeType("test/plain");
//			msd.setName("greeting.txt");
//			addParam(q3.getName(), msd);
			runTransition();
	        // this should also be last section so will go to view page
			checkViewRedirect(provider, new ResponseTarget(response, first));
			
			
			checkDiff("/cleanup.xsl", "answer_all_questions.xml");
			
			
			CompleteVisitor<D, R> vis = new CompleteVisitor<>(response);
			assertTrue("form should be complete",first_section.visit(vis));
		}
		
}