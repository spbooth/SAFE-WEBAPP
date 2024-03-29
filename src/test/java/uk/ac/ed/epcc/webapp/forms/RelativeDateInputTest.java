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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.RelativeDateInput;



public class RelativeDateInputTest extends DateInputTest<RelativeDateInput> {

	
	@Test
	public void dummy(){
		
	}
	@Override
	public RelativeDateInput getInput() {
		return  new RelativeDateInput(new Date());
	}
    
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.DateInputTest#getGoodParseData()
	 */
	@Override
	public Set<String> getGoodParseData() {
		
		Set<String> res = super.getGoodParseData();
		res.add("Now+0d");
		res.add("Now-1y");
		res.add("\nNow-1m\n");
		return res;
	}

}