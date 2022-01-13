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
public class MultiStageTestCase extends AbstractTransitionServletTest {

	/**
	 * 
	 */
	public MultiStageTestCase() {
		
	}
	
	@Test
	public void testCreate() throws Exception {
		MultiStageProvider provider = new MultiStageProvider(ctx);
		
		setTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content1.xml");
		addParam(MultiStageProvider.HUNDREDS,700);
		runTransition();
		checkFormContent(null, "multistage_content2.xml");
		addParam(MultiStageProvider.TENS,730);
		runTransition();
		checkFormContent(null, "multistage_content3.xml");
		addParam(MultiStageProvider.UNITS,736);
		runTransition();
		checkMessageText("736 created");
		// action should have cleared state
		setTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content1.xml");
	}
	
	@Test
	public void testCreate2() throws Exception {
		MultiStageProvider provider = new MultiStageProvider(ctx);
		
		setTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content1.xml");
		addParam(MultiStageProvider.HUNDREDS,700);
		runTransition();
		checkFormContent(null, "multistage_content2.xml");
		checkForwardToTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content2.xml");
		addParam(MultiStageProvider.TENS,730);
		runTransition();
		checkFormContent(null, "multistage_content3.xml");
		addParam(MultiStageProvider.UNITS,736);
		runTransition();
		checkMessageText("736 created");
		// action should have cleared state
		setTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content1.xml");
	}
	
	@Test
	public void testPhaseError() throws Exception {
		MultiStageProvider provider = new MultiStageProvider(ctx);
		
		setTransition(provider, MultiStageProvider.CREATE_KEY, null);
		checkFormContent(null, "multistage_content1.xml");
		addParam(MultiStageProvider.HUNDREDS,700);
		runTransition();
		checkFormContent(null, "multistage_content2.xml");
		addParam(MultiStageProvider.TENS,30);
		runTransition();
		checkError(MultiStageProvider.TENS, "Too small minimum value=700");
		addParam(MultiStageProvider.TENS,730);
		runTransition();
		checkFormContent(null, "multistage_content3.xml");
		addParam(MultiStageProvider.UNITS,736);
		runTransition();
		checkMessageText("736 created");
	}

	
}
