//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.TextInputTest;

/**
 * @author spb
 *
 */
public class ForbidSpacetTest extends TextInputTest {

	/**
	 * 
	 */
	public ForbidSpacetTest() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TextInputTest#forbidSpace()
	 */
	@Override
	public boolean forbidSpace() {
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TextInputTest#getBadData()
	 */
	@Override
	public Set getBadData() {
		Set badData = super.getBadData();
		
		badData.add("Bad wolf");
		badData.add("Bad\twolf");
		return badData;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TextInputTest#getGoodData()
	 */
	@Override
	public Set getGoodData() {
		Set goodData = new HashSet<String>();
		goodData.add("GoodWolf");
		return goodData;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TextInputTest#getGoodParseData()
	 */
	@Override
	public Set getGoodParseData() {
		Set goodParseData = new HashSet<String>();
		goodParseData.add("GoodWolfAfterTrim ");
		return goodParseData;
	}

}
