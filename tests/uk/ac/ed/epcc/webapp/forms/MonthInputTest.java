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
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.forms.inputs.MonthInput;


public class MonthInputTest<I extends MonthInput> extends ParseAbstractInputTestCase<Date, I>  {
	@Test
	public void dummy(){
		
	}
	public Set<String> getBadParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("womble");
		set.add("01-01-2008");
		return set;
	}

	public Set<String> getGoodParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("12-1965");
		set.add("09-2008");
		return set;
	}

	public Set<Date> getBadData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		return set;
	}

	@SuppressWarnings("deprecation")
	public Set<Date> getGoodData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		set.add(new Date(2008,10,01));
		return set;
	}

	@SuppressWarnings("unchecked")
	public I getInput() {
		return (I) new MonthInput();
	}
   

}