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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.NoSpaceFieldValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;

public class TextInputTest<I extends TextInput> extends ParseAbstractInputTestCase<String,I> {
	@Test
	public void dummy(){
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public I getInput() {
		I i = (I) new TextInput();
		if(forbidSpace()) {
			i.setTrim(true);
			i.addValidator(new NoSpaceFieldValidator());
		}
		i.setTrim(requireTrim());
		return i;
	}
	public boolean requireTrim(){
		return true;
	}

	public boolean forbidSpace(){
		return false;
	}
	@Override
	public Set<String> getBadData() {
		return new HashSet<>();
	}

	
	@Override
	public Set<String> getGoodData() {
		HashSet<String> good = new HashSet<>();
		good.add("Here is some text" );
		return good;
	}
	


	@Override
	public Set<String> getBadParseData() {
		return null;
	}


	@Override
	public Set<String> getGoodParseData() {
		return null;
	}
}