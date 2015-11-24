package uk.ac.ed.epcc.webapp.forms;

import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.email.inputs.RestrictedEmailInput;


public class RestrictedEmailInputTest extends EmailInputTest {
	@Test
	public void dummy(){
		
	}
	
	@Override
	public EmailInput getInput() {
		return  new RestrictedEmailInput("gmail,yahoo,hotmail");
	}
	
	@Override
	public Set<String> getBadData() {
		
	
		Set<String> badData = super.getBadData();
		badData.add("fred@gmail.com");
		badData.add("fred@hotmail.com");
		badData.add("fred@yahoo.com");
		return badData;
	}
}
