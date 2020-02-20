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
public class ConstrainedTestCase extends AbstractTransitionServletTest {

	/**
	 * 
	 */
	public ConstrainedTestCase() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testCreate() throws Exception {
		takeBaseline();
		ConstraintProvider provider = new ConstraintProvider(ctx);
		setTransition(provider, ConstraintProvider.CREATE_KEY, null);
		checkFormContent(null, "constraint_form1.xml");
		addParam("Min", 1);
		addParam("Max", 100);
		runTransition();
		checkFormContent(null, "constraint_form2.xml");
		addParam("Value",12);
		runTransition();
		checkMessage("object_created");
		checkDiff("/cleanup.xsl", "constraint_created.xml");
		
	}
	
	@Test
	public void testCreateBad() throws Exception {
		takeBaseline();
		ConstraintProvider provider = new ConstraintProvider(ctx);
		setTransition(provider, ConstraintProvider.CREATE_KEY, null);
		checkFormContent(null, "constraint_form1.xml");
		addParam("Min", 1);
		addParam("Max", 100);
		runTransition();
		checkFormContent(null, "constraint_form2.xml");
		addParam("Value",1200);
		runTransition();
		checkError("Value", "Too large maximum value=100");
		
	}
	
	@Test
	public void testPhaseValidator() throws Exception {
		takeBaseline();
		ConstraintProvider provider = new ConstraintProvider(ctx);
		setTransition(provider, ConstraintProvider.CREATE_KEY, null);
		checkFormContent(null, "constraint_form1.xml");
		addParam("Min", 100);
		addParam("Max", 1);
		runTransition();
		checkGeneralError("Min must be less than Max");
		
	}

	
}
