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
package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProviderInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProviderInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;

/**
 * @author spb
 *
 */

public class DomTransitionProviderTestCase extends WebappTestBase implements ViewTransitionFactoryDataProvider<XMLKey,XMLTarget>,
PathTransitionProviderInterfaceTest<XMLTarget, XMLKey, DomTransitionProviderTestCase>,
ViewTransitionFactoryInterfaceTest<XMLTarget, XMLKey, DomTransitionProviderTestCase>
{

	

	
	public PathTransitionProviderInterfaceTest<XMLTarget, XMLKey, DomTransitionProviderTestCase> path_test = new PathTransitionProviderInterfaceTestImpl<>(this);

	
	public ViewTransitionFactoryInterfaceTest<XMLTarget, XMLKey, DomTransitionProviderTestCase> view_test = new ViewTransitionFactoryInterfaceTestImpl<>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryDataProvider#getTransitionFactory()
	 */
	@Override
	public ViewTransitionFactory<XMLKey, XMLTarget> getTransitionFactory() {
		return new DomTransitionProvider(ctx);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryDataProvider#getTargets()
	 */
	@Override
	public Set<XMLTarget> getTargets() {
		Set<XMLTarget> result = new HashSet<>();
		TestXMLTargetFactory fac = new TestXMLTargetFactory(getContext(), "TestXML");
		LinkedList<String> path = new LinkedList<>();
		path.add("TestXML");
		path.add("Test1.xml");
		result.add(fac.find(path));
		path.add("E0");
		result.add(fac.find(path));
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getAllowedUser(java.lang.Object)
	 */
	@Override
	public SessionService<?> getAllowedUser(XMLTarget target) {
		return new SimpleSessionService(ctx);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getForbiddenUser(java.lang.Object)
	 */
	@Override
	public SessionService<?> getForbiddenUser(XMLTarget target) {
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTransitions()
	 */
	@Override
	@Test
	public final void testGetTransitions() throws Exception {
		path_test.testGetTransitions();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTransition()
	 */
	@Override
	@Test
	public final void testGetTransition() throws Exception {
		path_test.testGetTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testLookupTransition()
	 */
	@Override
	@Test
	public final void testLookupTransition() throws Exception {
		path_test.testLookupTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testAllowTransition()
	 */
	@Override
	@Test
	public final void testAllowTransition() throws Exception {
		path_test.testAllowTransition();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetTargetName()
	 */
	@Override
	@Test
	public final void testGetTargetName() {
		path_test.testGetTargetName();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testGetSummaryContentHTML()
	 */
	@Override
	@Test
	public final void testGetSummaryContentHTML() throws Exception {
		path_test.testGetSummaryContentHTML();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryInterfaceTest#testFormCreation()
	 */
	@Override
	@Test
	public final void testFormCreation() throws Exception {
		path_test.testFormCreation();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testCanView()
	 */
	@Override
	@Test
	public final void testCanView() throws Exception {
		view_test.testCanView();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetTopContent()
	 */
	@Override
	@Test
	public final void testGetTopContent() throws Exception {
		view_test.testGetTopContent();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetLogContent()
	 */
	@Override
	@Test
	public final void testGetLogContent() throws Exception {
		view_test.testGetLogContent();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryInterfaceTest#testGetHelp()
	 */
	@Override
	@Test
	public final void testGetHelp() throws Exception {
		view_test.testGetHelp();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProviderInterfaceTest#testGetID()
	 */
	@Override
	@Test
	public final void testGetID() throws Exception {
		path_test.testGetID();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProviderInterfaceTest#testVisitor()
	 */
	@Override
	@Test
	public final void testVisitor() {
		path_test.testVisitor();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getTopContentExpected(java.lang.Object)
	 */
	@Override
	public String getTopContentExpected(XMLTarget target) {
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getLogContentExpected(java.lang.Object)
	 */
	@Override
	public String getLogContentExpected(XMLTarget target) {
		return target.getTargetPath().size()+"_log.xml";
	}

	
}