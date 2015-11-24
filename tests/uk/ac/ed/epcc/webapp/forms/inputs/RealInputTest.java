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
	public Set<Float> getGoodData() {
		HashSet<Float> good = new HashSet<Float>();
		good.add(12.0f);
		good.add(14.0f);
		return good;
	}


	public RealInput getInput() {
		RealInput realInput = new RealInput();
		realInput.setMin(0.0f);
		return realInput;
	}


	public Set<Float> getBadData() {
		return new HashSet<Float>();
	}
	


	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<String>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		res.add("-1.0");
		return res;

	}


	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("12.8");
		res.add("0.0");
		res.add("0");
		return res;
	}
}
