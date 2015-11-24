// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.exec;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.9 $")
public class ExecTest extends WebappTestBase {

	/**
	 * 
	 */
	public ExecTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testJava() throws Exception{
		ctx.setService(new DefaultExecService(ctx));
		ExecService serv = ctx.getService(ExecService.class);
		// To be cross platform test with java 
		ProcessProxy result = serv.exec(5000L, "java -version");
		System.out.println("Output is "+result.getOutput());
		System.out.println("Error is "+result.getErr());
		System.out.println(result.getExit());
		assertEquals(Integer.valueOf(0), result.getExit());
		String expect = "version";  // common to oracle and openjdk
		System.out.println("Expect: "+expect);
		assertTrue(result.getErr().contains(expect));
		
	}
	
	@Test
	public void testFinish() throws Exception{
		ctx.setService(new DefaultExecService(ctx));
		ExecService serv = ctx.getService(ExecService.class);
		// To be cross platform test with java 
		URL location = Sleep.class.getResource(Sleep.class.getSimpleName()+".class");
		System.out.println(location);
		String path =location.getPath();
		System.out.println(path);
		int start=0;
		if(path.startsWith("/") && ! File.separator.equals("/")){
			// on windows you get /C:path that does not work as classpath
			start++;
		}
		
		path=path.substring(start, path.indexOf("uk"));
		String txt = "java -classpath "+path+" "+Sleep.class.getCanonicalName()+" 1";
		System.out.println(txt);
		ProcessProxy result = serv.exec(5000L, txt);
		System.out.println("Output is "+result.getOutput());
		System.out.println("Error is "+result.getErr());
		System.out.println(result.getExit());
		assertEquals(Integer.valueOf(0), result.getExit());
		assertTrue(result.getOutput().contains("done"));
	
		
		
	}
	
	
	@Test
	public void testTimeout() throws Exception{
		ctx.setService(new DefaultExecService(ctx));
		ExecService serv = ctx.getService(ExecService.class);
		// To be cross platform test with java 
		URL location = Sleep.class.getResource(Sleep.class.getSimpleName()+".class");
		String path =location.getPath();
		int start=0;
		if(path.startsWith("/") && ! File.separator.equals("/")){
			// on windows you get /C:path that does not work as classpath
			start++;
		}
		path=path.substring(start, path.indexOf("uk"));
		String txt = "java -classpath "+path+" "+Sleep.class.getCanonicalName()+" 60";
		System.out.println(txt);
		ProcessProxy result = serv.exec(1000L, txt);
		System.out.println("Output is "+result.getOutput());
		System.out.println("Error is "+result.getErr());
		System.out.println(result.getExit());
		assertTrue(result.wasTerminated());
		assertNull(result.getExit());
		
		
	}
	
	@Test 
	public void testHandlerTimeout() throws Exception{
		URL location = Sleep.class.getResource(Sleep.class.getSimpleName()+".class");
		String path =location.getPath();
		int start=0;
		if(path.startsWith("/") && ! File.separator.equals("/")){
			// on windows you get /C:path that does not work as classpath
			start++;
		}
		path=path.substring(start, path.indexOf("uk"));
		String txt = "java -classpath "+path+" "+Sleep.class.getCanonicalName()+" 60";
		System.out.println(txt);
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec(txt);
		ProcessHandler result = new ProcessHandler(p);
		result.setTimeout(1000L);
		result.start();
		result.join();
		assertTrue(result.wasTerminated());
		assertNull(result.getExit());
		
	}
	
}
