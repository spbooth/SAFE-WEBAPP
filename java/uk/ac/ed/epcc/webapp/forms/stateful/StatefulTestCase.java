//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.stateful;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.servlet.AbstractTransitionServletTest;

/**
 * @author Stephen Booth
 *
 */
public class StatefulTestCase extends AbstractTransitionServletTest {

	/**
	 * 
	 */
	public StatefulTestCase() {
		
	}
	
	@Test
	public void testCreate() throws Exception {
		StatefulProvider provider = new StatefulProvider(ctx);
		
		setTransition(provider, StatefulProvider.CREATE_KEY, null);
		checkFormContent(null, "stage1_content.xml");
		addParam(StatefulProvider.HUNDREDS,700);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkFormContent(null, "stage2_content.xml");
		addParam(StatefulProvider.TENS,730);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkFormContent(null, "stage3_content.xml");
		addParam(StatefulProvider.UNITS,736);
		setAction("Create");
		runTransition();
		checkMessageText("736 created");
		// action should have cleared state
		setTransition(provider, StatefulProvider.CREATE_KEY, null);
		checkFormContent(null, "stage1_content.xml");
	}
	
	@Test
	public void testPhaseError() throws Exception {
		StatefulProvider provider = new StatefulProvider(ctx);
		
		setTransition(provider, StatefulProvider.CREATE_KEY, null);
		checkFormContent(null, "stage1_content.xml");
		addParam(StatefulProvider.HUNDREDS,700);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkFormContent(null, "stage2_content.xml");
		addParam(StatefulProvider.TENS,30);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkError(StatefulProvider.TENS, "Too small minimum value=700");
		addParam(StatefulProvider.TENS,730);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkFormContent(null, "stage3_content.xml");
		addParam(StatefulProvider.UNITS,736);
		setAction("Create");
		runTransition();
		checkMessageText("736 created");
	}

	@Test
	public void testReset() throws Exception {
		StatefulProvider provider = new StatefulProvider(ctx);
		
		setTransition(provider, StatefulProvider.CREATE_KEY, null);
		checkFormContent(null, "stage1_content.xml");
		addParam(StatefulProvider.HUNDREDS,700);
		setAction(FormState.NEXT_ACTION);
		runTransition();
		checkFormContent(null, "stage2_content.xml");
		addParam(StatefulProvider.TENS,30);
		setAction(FormState.RESET_ACTION);
		runTransition();
		checkFormContent(null, "stage1_content.xml"); // back to start
	}
}
