//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;

/**
 * @author Stephen Booth
 *
 */
public class StaticServletTest extends ServletTest {
	private Path basedir;
	@Override
	public void setUp() throws Exception {
		super.setUp();
		servlet=new StaticServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "StaticServlet");
		servlet.init(config);
		req.servlet_path="/StaticServlet";
	}
	
	@Test
	public void testNoUser() throws ServletException, IOException {
		doPost();
		checkRequestAuth("/StaticServlet/");
	}
	
	public void setPerson() throws DataException {
		setupPerson("fred@example.com");
	}
	
	@Test
	public void testNoBasedir() throws DataException, ServletException, IOException {
		setPerson();
		doPost();
		checkMessage("invalid_argument");
	}
	
	@Test
	public void testBacktrack() throws DataException, ServletException, IOException {
		setPerson();
		req.path_info="../secret/file.txt";
		doPost();
		checkMessage("invalid_argument");
	}
	
	public void setupTestDir() throws IOException {
	
		basedir  = Files.createTempDirectory("base");
				
		ConfigService cfg = ctx.getService(ConfigService.class);
		cfg.setProperty("static.basedir", basedir.toString());
		Path hello = basedir.resolve("hello.txt");
		File file = hello.toFile();
		PrintStream stream = new PrintStream(file);
		stream.println("hello world");
		stream.close();
		
	}
	
	@Test
	public void testListing() throws DataException, IOException, ServletException {
		setPerson();
		setupTestDir();
		doPost();
		assertEquals(HttpServletResponse.SC_OK,res.error);
		assertTrue(res.stream.isClosed());
		assertEquals("<html lang='en'><head><title>Listing of test/StaticServlet/</title></head><body></html>\n" + 
				"<h2 align='center'>Listing of test/StaticServlet/</h2><hr>\n" + 
				"<ul>\n" + 
				"<LI> <a href=\"test/StaticServlet/hello.txt\">hello.txt</a></LI>\n" + 
				"</ul>\n" + 
				"<hr></body></html>\n" + 
				"",res.getOutputStream().toString().replaceAll("\r", ""));
		File f = basedir.toFile();
		f.delete();
	}
	@Test
	public void testDownload() throws DataException, IOException, ServletException {
		setPerson();
		setupTestDir();
		req.path_info="/hello.txt";
		doPost();
		assertEquals(HttpServletResponse.SC_OK,res.error);
		assertTrue(res.stream.isClosed());
		assertEquals("hello world",res.getOutputStream().toString().trim());
		File f = basedir.toFile();
		f.delete();
	}
}
