//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.jdbc.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableStructureTestFactory.TableStructureTestObject;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class TableTransitionTests extends AbstractTransitionServletTest {

	
	TableTransitionProvider provider;
	@Before
	public void setTarget() throws DataException{
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(user);
		service.toggleRole(SessionService.ADMIN_ROLE);
		
		provider=new TableTransitionProvider(ctx);
	}
	
	@Test
	public void addStdField() throws TransitionException, ServletException, IOException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField(TableStructureTestFactory.SECRET_IDENTITY));
		
		setTransition(provider, TableTransitionProvider.ADD_STD_FIELD,fac);
		addParam(AddStdFieldTransition.FIELD_FORMFIELD, TableStructureTestFactory.SECRET_IDENTITY);
		addParam("action", AddFieldTransition.ADD_ACTION);
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField(TableStructureTestFactory.SECRET_IDENTITY));
		
		
	}

	@Test
	public void addTextField() throws ServletException, IOException, TransitionException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider,TableTransitionProvider.ADD_TEXT_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addIntegerField() throws TransitionException, ServletException, IOException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider, TableTransitionProvider.ADD_INTEGER_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		//addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addDoubleField() throws TransitionException, ServletException, IOException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider, TableTransitionProvider.ADD_DOUBLE_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		//addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addDateField() throws TransitionException, ServletException, IOException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider, TableTransitionProvider.ADD_DATE_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		//addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addLongField() throws ServletException, IOException, TransitionException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider, TableTransitionProvider.ADD_LONG_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		//addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addFloatField() throws ServletException, IOException, TransitionException{
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		setTransition(provider, TableTransitionProvider.ADD_FLOAT_FIELD_KEY,fac);
		addParam(AddFieldTransition.FIELD, "Wombat");
		addParam("action", AddFieldTransition.ADD_ACTION);
		//addParam("Size", "32");
		runTransition();
		checkViewRedirect(provider, fac);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	
	@Test
	public void testIndex() throws Exception {
		setTransition(provider,null,null);
		runTransition();
		checkForwardToTransition(provider,TableTransitionProvider.INDEX, null);
		checkFormContent(null, "table_form.xml");
		
	}
	
	@Test
	public void testViewTable() throws Exception {
		
		TableStructureTestFactory target = new TableStructureTestFactory(getContext());
		setTransition(provider,null,target);
		checkViewContent(null, "view.xml");
		
		
		//runTransition();
		//checkForwardToTransition(provider,TableTransitionProvider.INDEX, null);
		//checkFormContent(null, "table_form.xml");
		
	}
	
	@Test
	public void testAddOptionalField() throws Exception {
		takeBaseline();
		TableStructureTestFactory target = new TableStructureTestFactory(getContext());
		setTransition(provider,TableTransitionProvider.ADD_STD_FIELD,target);
		TableStructureTestObject obj = target.makeBDO();
		obj.setName("florance");
		obj.setSecretIdentity("dougal");
		obj.commit();
		checkFormContent(null, "add_form.xml");
		addParam("Field","SecretIdentity");
		runTransition();
		checkRedirectToTransition(provider,null, target);
		checkDiff("/cleanup.xsl", "added.xml");
	}
}