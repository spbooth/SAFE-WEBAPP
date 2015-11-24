/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.RelativeDateInput;



public class RelativeDateInputTest extends DateInputTest<RelativeDateInput> {

	
	@Test
	public void dummy(){
		
	}
	@Override
	public RelativeDateInput getInput() {
		return  new RelativeDateInput();
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
