//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.http;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.mock.MockHttpService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/**
 * @author Stephen Booth
 *
 */
public class HttpServiceTest extends WebappTestBase {

	
	
	@Test
	public void testFetch() throws HttpException, DataFault, IOException {
		MockHttpService serv = new MockHttpService(ctx);
		ctx.setService(serv);
		
		URL url = new URL("http://www.example.com/message");
		
		MimeStreamData result = serv.fetch(url, null);
		
		//assertEquals("text/plain", result.getContentType());
		ByteArrayOutputStream text = new ByteArrayOutputStream();
		result.append(text);
		assertEquals("Now is the winter of our discontent", text.toString());
		
	}
}
