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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.mock.MockOutputStream;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public class QRServletTest extends ServletTest {
	@Before
	public void setConfig() throws ServletException{
		servlet=new QRServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "QRServlet");
		servlet.init(config);
		req.servlet_path="/QRCode";
	}
	
	
	@Test
	public void testMakeCode() throws ServletException, IOException, DataFault {
		req.path_info="hello_world.png";
		addParam("text", "https://en.wikipedia.org/wiki/QR_code");
		doPost();
		assertEquals("image/png",res.getContentType());
		byte content[] = ((MockOutputStream)res.getOutputStream()).getData();
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
		assertEquals(300,image.getHeight());
		assertEquals(300,image.getWidth());
		//writeFile("image.png", content);
		byte[] expected = getResourceAsBytes("image.png");
		assertEquals(expected.length, content.length);
		for(int i=0;i<content.length;i++) {
			assertEquals(expected[i],content[i]);
		}
	}
}
