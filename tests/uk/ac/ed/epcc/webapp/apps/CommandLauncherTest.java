// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.apps;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;
/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.6 $")
public class CommandLauncherTest extends WebappTestBase {
	/**
	 * 
	 */
	public CommandLauncherTest() {
		
	}
	
	@Test
	public void testHelp() throws Exception{
		CommandLauncher launcher = new CommandLauncher(ctx);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		launcher.setOut(new PrintStream(output));
		
		launcher.run(new String[]{"--help","TestCommand"});
		String string = output.toString();
		System.out.println(string);
		assertTrue(string.contains("A test helpmessage"));
	}
	
	@Test
	public void testList() throws Exception{
		CommandLauncher launcher = new CommandLauncher(ctx);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		launcher.setOut(new PrintStream(output));
		
		launcher.run(new String[]{"--help"});
		String string = output.toString();
		System.out.println(string);
		assertFalse(string.contains("A test helpmessage"));
		assertTrue(string.contains("Commands:"));
		assertTrue(string.contains("TestCommand- A test command"));
	}
	@Test
	public void testThrow() throws Exception{
		
		/**************************************************
		 * Important. The CommandLauncher ususally catches
		 * exceptions and calls exit. This will abort
		 * futher junit tests as well as failing this test
		 * this behaviour is supressed by having the property 
		 * "testing" set. Check the test property setup 
		 * especially the AppContextFixture if testing aborts here
		 * 
		 * *************************************************
		 */
		
		CommandLauncher launcher = new CommandLauncher(ctx);
		boolean ok = true;
		try{
			launcher.run(new String[]{"TestCommand","-T"});
			System.out.println("Returned");
			ok = false;
		}catch(Throwable e){
			System.out.println("Got excpetion");
		}
		assertTrue(ok);
	}
	
	@Test
	public void testUser() throws Exception{
		CommandLauncher launcher = new CommandLauncher(ctx);
		
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();
		AppUser user = fac.makeBDO();
		user.setRealmName(WebNameFinder.WEB_NAME,"fred");
		user.setEmail("fred@example.com");
		user.commit();
		launcher.run(new String[]{"-U","fred@example.com","TestCommand","-E","fred"});
		
	}
}
