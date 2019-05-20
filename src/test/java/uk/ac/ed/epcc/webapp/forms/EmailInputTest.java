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

import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;


public class EmailInputTest extends TextInputTest<EmailInput> {
	@Test
	public void dummy(){
		
	}
	@Override
	public Set<String> getGoodData() {
		HashSet<String> good = new HashSet<>();
		good.add( "spb@example.com" );
		return good;
	}
	
	@Override
	public Set<String> getBadData() {
		HashSet<String> bad = new HashSet<>();
		bad.add( "");
		bad.add( "Some random text");
		bad.add("<Stephen Booth> spb@example.com" );
		return bad;
	}

	@Override
	public EmailInput getInput() {
		return  new EmailInput();
	}
	
	public boolean allowNull(){
		return false;
	}
}