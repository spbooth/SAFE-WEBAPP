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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.Units;
public class StoreageUnitNumerInputTest extends NumberInputTestCase<Long,StorageUnitNumberInput> {
	@Test
	public void dummy(){
		
	}
	@Override
	public Set<Long> getBadData() {
		Set<Long> bad = new HashSet<>();
		return bad;
	}

	@Override
	public Set<Long> getGoodData() {
		Set<Long> good = new HashSet<>();
		good.add(12L*getUnits().bytes);
		good.add(14L*getUnits().bytes);
		return good;
	}

	@Override
	public StorageUnitNumberInput getInput() {
		StorageUnitNumberInput i = new StorageUnitNumberInput(getUnits());
		return i;
	}
	/**
	 * @return
	 */
	public Units getUnits() {
		return Units.GiB;
	}
	
	@Override
	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		return res;

	}


	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<>();
		res.add("12");
		res.add("15");
		res.add("70");
		return res;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowBound()
	 */
	@Override
	public Long getLowBound() {
		return 10L*getUnits().bytes;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighBound()
	 */
	@Override
	public Long getHighBound() {
		return 20L*getUnits().bytes;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighData()
	 */
	@Override
	public Set<Long> getHighData() {
		Set<Long> high = new HashSet<>();
		high.add(25L*getUnits().bytes);
		high.add(30L*getUnits().bytes);
		return high;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowData()
	 */
	@Override
	public Set<Long> getLowData() {
		Set<Long> high = new HashSet<>();
		high.add(-6L*getUnits().bytes);
		high.add(4L*getUnits().bytes);
		return high;
	}

}