// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.messages;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class MessageServiceTest extends WebappTestBase {

	
	
	@Test
	public void testMessageService(){
		MessageBundleService serv = getContext().getService(MessageBundleService.class);
		
		ResourceBundle messages = serv.getBundle();
		assertEquals("This is A",messages.getString("a.title"));
		assertEquals("This is B",messages.getString("b.title"));
		assertEquals("I am A",messages.getString("a.text"));
		// check localisation still works
		assertEquals("Hello I am called B",messages.getString("b.text"));
	}
	

}
