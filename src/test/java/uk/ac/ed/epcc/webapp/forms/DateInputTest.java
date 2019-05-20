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

import uk.ac.ed.epcc.webapp.forms.inputs.DateInput;

public class DateInputTest<I extends DateInput> extends ParseAbstractInputTestCase<Date, I>  {
	@Test
	public void dummy(){
		
	}
	@Override
	public Set<String> getBadParseData() {
		HashSet<String> set=new HashSet<>();
		set.add("womble");
		set.add("2008-99-99");
		return set;
	}

	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> set=new HashSet<>();
		set.add("1965-12-12");
		set.add("2008-09-01");
		return set;
	}

	@Override
	public Set<Date> getBadData() throws Exception {
		HashSet<Date> set = new HashSet<>();
		return set;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Set<Date> getGoodData() throws Exception {
		HashSet<Date> set = new HashSet<>();
		set.add(new Date(2008,10,06));
		return set;
	}

	@Override
	@SuppressWarnings("unchecked")
	public I getInput() {
		return (I) new DateInput();
	}
   

}