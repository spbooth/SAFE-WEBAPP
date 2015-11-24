package uk.ac.ed.epcc.webapp.forms.registry;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.forms.registry.FormOperations;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;


public class ClassifierFormTest extends AbstractTransitionServletTest {

	@Test
	public void testCreateClassifier() throws Exception{
		MockTansport.clear();
		bootstrapClassifiers();
		AppUser admin = setupPerson();
	
		TransitionFactory provider = TransitionServlet.getProviderFromName(getContext(), "Classifiers:TestClassification");
		takeBaseline();
		setTransition(provider, FormOperations.Create, null);
		addParam("Name", "Boris");
		addParam("Description", "A test resource classification");
		runTransition();
		checkMessage("object_created");
		checkDiff("/cleanup.xsl", "classification.xml");
		
	}
	
	@Test
	@DataBaseFixtures("classification.xml")
	public void testSelectClassification() throws Exception{
		MockTansport.clear();
		bootstrapClassifiers();
		AppUser admin = setupPerson();
	
		ClassificationFactory fac = new ClassificationFactory(getContext(), "TestClassification");
		Classification test_pool = fac.findFromString("Boris");
		assertNotNull(test_pool);
		TransitionFactory provider = TransitionServlet.getProviderFromName(getContext(), "Classifiers:TestClassification");
		takeBaseline();
		setTransition(provider, FormOperations.Update, null);
		addParam(Updater.TARGET, test_pool);
		runTransition();
		checkForwardToTransition(provider, FormOperations.Edit, test_pool);
		
	}
	
	@Test
	@DataBaseFixtures("classification.xml")
	public void testEditClassifier() throws Exception{
		MockTansport.clear();
		bootstrapClassifiers();
		AppUser admin = setupPerson();
	
		ClassificationFactory fac = new ClassificationFactory(getContext(), "TestClassification");
		Classification test_pool = fac.findFromString("Boris");
		assertNotNull(test_pool);
		TransitionFactory provider = TransitionServlet.getProviderFromName(getContext(), "Classifiers:TestClassification");
		takeBaseline();
		setTransition(provider, FormOperations.Edit, test_pool);
		//addParam("PrimaryRecordID", test_pool);
		//runTransition();
		addParam("Description", "Junk");
		runTransition();
		checkMessage("object_updated");
		checkDiff("/cleanup.xsl", "edited_classification.xml");
		
	}
	
	
	
	
	/** test the combined select and edit cycle.
	 * This si really more of a test of the test framework than of the model logic
	 * 
	 * @throws Exception
	 */
	@Test
	@DataBaseFixtures("classification.xml")
	public void testSelectAndEditClassifier() throws Exception{
		MockTansport.clear();
		bootstrapClassifiers();
		AppUser admin = setupPerson();
	
		ClassificationFactory fac = new ClassificationFactory(getContext(), "TestClassification");
		Classification test_pool = fac.findFromString("Boris");
		assertNotNull(test_pool);
		TransitionFactory provider = TransitionServlet.getProviderFromName(getContext(), "Classifiers:TestClassification");
		takeBaseline();
		setTransition(provider, FormOperations.Update, null);
		addParam(Updater.TARGET, test_pool);
		runTransition();
		checkForwardToTransition(provider, FormOperations.Edit, test_pool);
	
		addParam("Description", "Junk");
		runTransition();
		checkMessage("object_updated");
		checkDiff("/cleanup.xsl", "edited_classification.xml");
		
	}
	/** setup a valid operator with RootManager role
	 * 
	 * @throws DataException
	 */
	public AppUser setupPerson() throws DataException{
		SessionService sess = setupPerson(getContext().getInitParameter("test.email"));
		sess.setTempRole(SessionService.ADMIN_ROLE);
		sess.setToggle(SessionService.ADMIN_ROLE,Boolean.TRUE);
		return sess.getCurrentPerson();
		
		
	}
}
