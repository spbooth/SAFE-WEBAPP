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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.TestParseDataProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampInput;
public class TimeStampInputTest extends ParseAbstractInputTestCase<Date,TimeStampInput>  implements TestParseDataProvider<Date,TimeStampInput>{

	@Test
	public void dummy(){
		
	}
	public TimeStampInput getInput() {
	
		return  new TimeStampInput(1000L);
	}

	
	public Set<Date> getBadData() {
		return new HashSet<Date>();
	}

	
	public Set<Date> getGoodData() {
		HashSet<Date> good = new HashSet<Date>();
		Date r = new Date();
		r.setTime((r.getTime()/1000)*1000);
		good.add(r);
		return good;
	}

	

	public Set<String> getBadParseData() {
		Set<String> res = new HashSet<String>();
		res.add("12-12-2008 08:00:00");
		res.add("boris the spider");
		return res;
	}


	public Set<String> getGoodParseData() {
		Set<String> res = new HashSet<String>();
		res.add("2006-12-12 08:00:00");
		res.add("2006-12-12 08:00");
		res.add("2006-12-12 08");
		res.add("2006-12-12");
		return res;
	}
}