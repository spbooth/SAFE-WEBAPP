// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.jdbc.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.servlet.ServletTest;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class TableTransitionTests extends ServletTest {

	TransitionServlet servlet;
	@Before
	public void setTarget() throws DataException{
		req.servlet_path=TransitionServlet.TRANSITION_SERVLET;
		req.path_info=TableTransitionProvider.TABLE_TRANSITION_TAG+"/"+TableStructureTestFactory.DEFAULT_TABLE;
		req.roles.add(SessionService.ADMIN_ROLE);
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(user);
		service.toggleRole(SessionService.ADMIN_ROLE);
		servlet = new TransitionServlet();
	}
	
	@Test
	public void addStdField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField(TableStructureTestFactory.SECRET_IDENTITY));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, TableSpecificationTransitionSource.ADD_STD_FIELD);
		req.params.put(TableSpecificationTransitionSource.FIELD_FORMFIELD, TableStructureTestFactory.SECRET_IDENTITY);
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField(TableStructureTestFactory.SECRET_IDENTITY));
		
		
	}

	@Test
	public void addTextField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_TEXT_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addIntegerField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_INTEGER_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		//req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addDoubleField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_DOUBLE_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		//req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addDateField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_DATE_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		//req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addLongField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_LONG_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		//req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
	@Test
	public void addFloatField(){
		TableStructureTestFactory fac = new TableStructureTestFactory(ctx);
		assertFalse(fac.hasField("Wombat"));
		
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, GeneralTransitionSource.ADD_FLOAT_FIELD_KEY);
		req.params.put(AddFieldTransition.FIELD, "Wombat");
		req.params.put("action", AddFieldTransition.ADD_ACTION);
		req.params.put("transition_form", "true");
		req.params.put("direct", "true");
		//req.params.put("Size", "32");
		servlet.doPost(req, res, ctx);
		assertEquals("test/TransitionServlet/Table/TableTest", res.redirect);
		
		fac = new TableStructureTestFactory(ctx);
		assertTrue(fac.hasField("Wombat"));
		
		
	}
}
