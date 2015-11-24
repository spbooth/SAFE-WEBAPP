/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.DoubleInput;


public class DoubleInputTest extends NumberInputTestCase<Double,DoubleInput> {
	@Test
	public void dummy(){
		
	}
	public Set<Double> getGoodData() {
		HashSet<Double> good = new HashSet<Double>();
		good.add(12.0);
		good.add(14.0);
		return good;
	}


	public DoubleInput getInput() {
		return new DoubleInput();
	}


	public Set<Double> getBadData() {
		return new HashSet<Double>();
	}
	


	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<String>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
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
