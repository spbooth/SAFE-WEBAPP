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
package uk.ac.ed.epcc.webapp.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.WebappTestBase;


public class ResourceServiceTestCase extends WebappTestBase {
	


	private static final String DATA_TEST_FILE = "/data/test_file.txt";
	private static final String DATA_TEST_FILE_MOD = "/data//test_file.txt";
	private static final String DATA_TEST_FILE2 = "/data/test/test_file.txt";
	private static final String BOGUS_TEST_FILE = "/data/bogus_file.txt";
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
	@Test
	public void testNoResource() {
		ResourceService serv = ctx.getService(ResourceService.class);
		
		URL direct = getClass().getResource(BOGUS_TEST_FILE);
		assertNull(direct);
		
		URL serv_url = serv.getResource(BOGUS_TEST_FILE);
		assertNull(serv_url);
		
	}
}