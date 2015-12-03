//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
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

public class EmailTransitionProviderTestCase extends WebappTestBase implements ViewTransitionFactoryDataProvider<EditAction, MailTarget>,
PathTransitionProviderInterfaceTest<MailTarget, EditAction, EmailTransitionProviderTestCase>,
ViewTransitionFactoryInterfaceTest<MailTarget, EditAction, EmailTransitionProviderTestCase>
{

	public PathTransitionProviderInterfaceTest<MailTarget, EditAction, EmailTransitionProviderTestCase> path_test = new PathTransitionProviderInterfaceTestImpl<MailTarget, EditAction, EmailTransitionProviderTestCase>(this);
 

	public ViewTransitionFactoryInterfaceTest<MailTarget, EditAction, EmailTransitionProviderTestCase> view_test = new ViewTransitionFactoryInterfaceTestImpl<MailTarget, EditAction, EmailTransitionProviderTestCase>(this);
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryDataProvider#getTargets()
	 */
	public Set<MailTarget> getTargets() {
		HashSet<MailTarget> targets = new HashSet<MailTarget>();
		for(int i=0;i<getFac().names.length;i++){
			MessageHandler hand = getFac().getHandler(i, null);
			try {
				targets.add(new MailTarget(hand, hand.getMessageProvider().getMessageHash(), null));
			} catch (Exception e) {
				throw new ConsistencyError("Error making target", e);
			}
		}
		return targets;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getTransitionFactory()
	 */
	public ViewTransitionFactory<EditAction, MailTarget> getTransitionFactory() {
		return new EmailTransitionProvider(getFac());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getAllowedUser(java.lang.Object)
	 */
	public SessionService<?> getAllowedUser(MailTarget target) {
		return new SimpleSessionService(ctx);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactoryDataProvider#getForbiddenUser(java.lang.Object)
	 */
	public SessionService<?> getForbiddenUser(MailTarget target) {
		// TODO Auto-generated method stub
		return null;
	}

	

	/**
	 * @return the fac
	 */
	private TestMessageHandlerFactory getFac() {
		return new TestMessageHandlerFactory(getContext());
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

	
	

}