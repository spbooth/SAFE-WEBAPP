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
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.model.serv.SettableServeDataProducer;
/**
 * @author spb
 *
 */

public class ServeDataServletTest extends ServletTest {
	@Before
	public void setConfig() throws ServletException{
		servlet=new ServeDataServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "ServeDataServlet");
		servlet.init(config);
		req.servlet_path=ServeDataServlet.DATA_PATH;
	}
	@Test
	public void testServeData() throws Exception{
		AppContext conn = getContext();
		SettableServeDataProducer producer = conn.makeObject(SettableServeDataProducer.class, ServeDataProducer.DEFAULT_SERVE_DATA_TAG);
		assertNotNull(producer);
		ByteArrayMimeStreamData data = new ByteArrayMimeStreamData();
		data.setMimeType("text/plain");
		data.setName("testdata.txt");
		PrintWriter writer = new PrintWriter(data.getOutputStream());
		writer.println("hello world");
		writer.close();
		
		List<String> args = producer.setData(data);
		req.path_info=ServeDataServlet.getURL(conn, producer, args).substring(ServeDataServlet.DATA_PATH.length());
		
		doPost();
		
		assertEquals("text/plain",res.getContentType());
		assertTrue(res.getOutputStream().toString().startsWith("hello world")); // windows CR LR ignore newline etc.
		
	}
}