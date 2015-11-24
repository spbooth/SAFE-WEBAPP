/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
public class IntegerInputTest extends NumberInputTestCase<Integer,IntegerInput> {
	@Test
	public void dummy(){
		
	}
	public Set<Integer> getBadData() {
		Set<Integer> bad = new HashSet<Integer>();
		bad.add(-1);
		return bad;
	}

	public Set<Integer> getGoodData() {
		Set<Integer> good = new HashSet<Integer>();
		good.add(12);
		good.add(14);
		return good;
	}

	public IntegerInput getInput() {
		IntegerInput i = new IntegerInput();
		i.setMin(0);
		return i;
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
		res.add("12");
		res.add("1");
		res.add("0");
		return res;
	}
	
	

}
