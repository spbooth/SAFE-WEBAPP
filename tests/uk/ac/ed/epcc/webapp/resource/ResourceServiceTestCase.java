/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.WebappTestBase;


public class ResourceServiceTestCase extends WebappTestBase {
	


	private static final String DATA_TEST_FILE = "/data/test_file.txt";
	private static final String DATA_TEST_FILE_MOD = "/data//test_file.txt";
	private static final String DATA_TEST_FILE2 = "/data/test/test_file.txt";
	
	@Test
	public void testGetData() throws Exception{
		ResourceService serv = ctx.getService(ResourceService.class);
		
		URL direct = getClass().getResource(DATA_TEST_FILE);
		System.out.println(direct);
		assertNotNull(direct);
		
		// classloader for resource sevlet does not see test classpath
		//URL classloader_direct = getClass().getClassLoader().getResource("message.properties");
		//System.out.println(classloader_direct);
		//assertNotNull(classloader_direct);
		URL url = serv.getResource(DATA_TEST_FILE);
		System.out.println(url);
		assertNotNull(url);
		InputStream stream = serv.getResourceAsStream(DATA_TEST_FILE);
		assertNotNull(stream);
		StringBuilder sb = new StringBuilder();
		int c;
		while( -1 != (c = stream.read()) ){
			sb.appendCodePoint(c);
		}
		assertEquals("In data", sb.toString());
	}
	
	@Test
	public void testGetDataMOD() throws Exception{
		ResourceService serv = ctx.getService(ResourceService.class);
		
		URL direct = getClass().getResource(DATA_TEST_FILE_MOD);
		System.out.println(direct);
		assertNotNull(direct);
		
		// classloader for resource sevlet does not see test classpath
		//URL classloader_direct = getClass().getClassLoader().getResource("message.properties");
		//System.out.println(classloader_direct);
		//assertNotNull(classloader_direct);
		URL url = serv.getResource(DATA_TEST_FILE_MOD);
		System.out.println(url);
		assertNotNull(url);
		InputStream stream = serv.getResourceAsStream(DATA_TEST_FILE_MOD);
		assertNotNull(stream);
		StringBuilder sb = new StringBuilder();
		int c;
		while( -1 != (c = stream.read()) ){
			sb.appendCodePoint(c);
		}
		assertEquals("In data", sb.toString());
	}
	
	@Test
	public void testGetData2() throws Exception{
		ResourceService serv = ctx.getService(ResourceService.class);
		
		URL direct = getClass().getResource(DATA_TEST_FILE2);
		System.out.println(direct);
		assertNotNull(direct);
		
		// classloader for resource sevlet does not see test classpath
		//URL classloader_direct = getClass().getClassLoader().getResource("message.properties");
		//System.out.println(classloader_direct);
		//assertNotNull(classloader_direct);
		URL url = serv.getResource(DATA_TEST_FILE2);
		System.out.println(url);
		assertNotNull(url);
		InputStream stream = serv.getResourceAsStream(DATA_TEST_FILE2);
		assertNotNull(stream);
		StringBuilder sb = new StringBuilder();
		int c;
		while( -1 != (c = stream.read()) ){
			sb.appendCodePoint(c);
		}
		assertEquals("In data/test", sb.toString());
	}
	
	@Test
	public void testMapForClassloader(){
		assertEquals("WEB-INF/report-templates/DART.xml", DefaultResourceService.mapForClassloader("WEB-INF/report-templates/"+"/"+"DART.xml"));
		
		assertEquals("WEB-INF/report-templates/DART.xml", DefaultResourceService.mapForClassloader("WEB-INF/report-templates"+"/"+"DART.xml"));
		assertEquals("WEB-INF/report-templates/DART.xml", DefaultResourceService.mapForClassloader("/WEB-INF/report-templates/"+"/"+"DART.xml"));
		
		assertEquals("WEB-INF/report-templates/DART.xml", DefaultResourceService.mapForClassloader("/WEB-INF/report-templates"+"/"+"DART.xml"));
	}
}
