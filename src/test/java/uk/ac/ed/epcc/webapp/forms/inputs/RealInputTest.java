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

import uk.ac.ed.epcc.webapp.forms.inputs.RealInput;

public class RealInputTest extends NumberInputTestCase<Float,RealInput> {
	@Test
	public void dummy(){
		
	}
	@Override
	public Set<Float> getGoodData() {
		HashSet<Float> good = new HashSet<>();
		good.add(12.0f);
		good.add(14.0f);
		return good;
	}


	@Override
	public RealInput getInput() {
		RealInput realInput = new RealInput();
		realInput.setMin(0.0f);
		return realInput;
	}


	@Override
	public Set<Float> getBadData() {
		return new HashSet<>();
	}
	


	@Override
	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		res.add("-1.0");
		return res;

	}


	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<>();
		res.add("12.8");
		res.add("0.0");
		res.add("0");
		return res;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowBound()
	 */
	@Override
	public Float getLowBound() {
		return 10.0F;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighBound()
	 */
	@Override
	public Float getHighBound() {
		return 20.0F;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighData()
	 */
	@Override
	public Set<Float> getHighData() {
		Set<Float> high = new HashSet<>();
		high.add(25.0F);
		high.add(30.0F);
		return high;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowData()
	 */
	@Override
	public Set<Float> getLowData() {
		Set<Float> high = new HashSet<>();
		high.add(0.0F);
		high.add(4.0F);
		return high;
	}
}