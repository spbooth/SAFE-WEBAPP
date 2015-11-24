/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;

public class TextInputTest<I extends TextInput> extends ParseAbstractInputTestCase<String,I> {
	@Test
	public void dummy(){
		
	}

	@SuppressWarnings("unchecked")
	public I getInput() {
		return (I) new TextInput(allowNull());
	}

	
	public Set<String> getBadData() {
		return new HashSet<String>();
	}

	
	public Set<String> getGoodData() {
		HashSet<String> good = new HashSet<String>();
		good.add("Here is some text" );
		return good;
	}
	


	public Set<String> getBadParseData() {
		return null;
	}


	public Set<String> getGoodParseData() {
		return null;
	}
}
