// Copyright - The University of Edinburgh 2015
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
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
