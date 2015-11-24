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
		HashSet<String> good = new HashSet<String>();
		good.add( "spb@example.com" );
		return good;
	}
	
	@Override
	public Set<String> getBadData() {
		HashSet<String> bad = new HashSet<String>();
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
