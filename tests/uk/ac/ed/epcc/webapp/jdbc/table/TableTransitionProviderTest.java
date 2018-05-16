//| Copyright - The University of Edinburgh 2012                            |
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

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactoryInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactoryInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProviderInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProviderInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;
/**
 * @author spb
 *
 */

public class TableTransitionProviderTest extends WebappTestBase implements ViewTransitionFactoryDataProvider<TableTransitionKey, DataObjectFactory>,
TransitionProviderInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>,
IndexTransitionFactoryInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>,
ViewTransitionFactoryInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>

{
	
	public TransitionProviderInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest> transition_provider_test = new TransitionProviderInterfaceTestImpl<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>(this);
   
	public IndexTransitionFactoryInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest> index_factory_test = new IndexTransitionFactoryInterfaceTestImpl<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>(this);
    
    public ViewTransitionFactoryInterfaceTest<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest> view_factory_test = new ViewTransitionFactoryInterfaceTestImpl<DataObjectFactory, TableTransitionKey, TableTransitionProviderTest>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryDataProvider#getTransitionFactory()
	 */
	public ViewTransitionFactory<TableTransitionKey, DataObjectFactory> getTransitionFactory() {
		return new TableTransitionProvider(ctx);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryDataProvider#getTargets()
	 */
	public Set<DataObjectFactory> getTargets() {
		HashSet<DataObjectFactory> result = new HashSet<DataObjectFactory>();
		AppContext conn = getContext();
		Map<String,Class> classmap = conn.getClassMap(DataObjectFactory.class);
		for(String key : classmap.keySet()){
			result.add((DataObjectFactory) conn.makeObject(classmap.get(key), key));
		}
		return result;
	}
	@Test
	public void testData(){
		// make sure we are configured to test something.
		assertTrue(getTargets().size()>0);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getAllowedUser(java.lang.Object)
	 */
	public SessionService<?> getAllowedUser(DataObjectFactory target) {
		SessionService sess = new SimpleSessionService(getContext());
		sess.setTempRole(SessionService.ADMIN_ROLE);
		sess.setToggle(SessionService.ADMIN_ROLE, Boolean.TRUE);
		return sess;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getForbiddenUser(java.lang.Object)
	 */
	public SessionService<?> getForbiddenUser(DataObjectFactory target) {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTransitions()
	 */
	@Override
	@Test
	public final void testGetTransitions() throws Exception {
		transition_provider_test.testGetTransitions();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTransition()
	 */
	@Override
	@Test
	public final void testGetTransition() throws Exception {
		transition_provider_test.testGetTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testLookupTransition()
	 */
	@Override
	@Test
	public final void testLookupTransition() throws Exception {
		transition_provider_test.testLookupTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testAllowTransition()
	 */
	@Override
	@Test
	public final void testAllowTransition() throws Exception {
		transition_provider_test.testAllowTransition();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTargetName()
	 */
	@Override
	@Test
	public final void testGetTargetName() {
		transition_provider_test.testGetTargetName();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetSummaryContentHTML()
	 */
	@Override
	@Test
	public final void testGetSummaryContentHTML() throws Exception {
		transition_provider_test.testGetSummaryContentHTML();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testFormCreation()
	 */
	@Override
	@Test
	public final void testFormCreation() throws Exception {
		transition_provider_test.testFormCreation();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testCanView()
	 */
	@Override
	@Test
	public final void testCanView() throws Exception {
		view_factory_test.testCanView();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetTopContent()
	 */
	@Override
	@Test
	public final void testGetTopContent() throws Exception {
		view_factory_test.testGetTopContent();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetLogContent()
	 */
	@Override
	@Test
	public final void testGetLogContent() throws Exception {
		view_factory_test.testGetLogContent();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetHelp()
	 */
	@Override
	@Test
	public final void testGetHelp() throws Exception {
		view_factory_test.testGetHelp();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactoryInterfaceTest#testIndexTransition()
	 */
	@Override
	@Test
	public final void testIndexTransition() throws TransitionException {
		index_factory_test.testIndexTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProviderInterfaceTest#testGetID()
	 */
	@Override
	@Test
	public final void testGetID() throws Exception {
		transition_provider_test.testGetID();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionProviderInterfaceTest#testVisitor()
	 */
	@Override
	@Test
	public final void testVisitor() {
		transition_provider_test.testVisitor();
		
	}

}