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
package uk.ac.ed.epcc.webapp.mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.http.DefaultHttpService;
import uk.ac.ed.epcc.webapp.http.HttpService;

/** a mock {@link HttpService} that allows multiple connections within a test.
 * 
 * @author Stephen Booth
 *
 */
public class MockMultiConnectionHttpService extends DefaultHttpService {
	public Map<URL,LinkedList<MockHttpURLConnection>> connections = new HashMap<URL,LinkedList< MockHttpURLConnection>>();
	public LinkedList<MockHttpURLConnection> used = new LinkedList<MockHttpURLConnection>();

	@Override
	protected HttpURLConnection connect(URL url) throws IOException {
		getLogger().debug("Requesting connection to "+url);
		MockHttpURLConnection c = null;
		LinkedList<MockHttpURLConnection> list = connections.get(url);
		if( list != null && ! list.isEmpty() ) {
			c = list.removeFirst();
		}
		if( c == null ) {
			throw new IOException("No connection for "+url);
		}
		used.add(c);
		return c;
	}

	/**
	 * @param conn
	 */
	public MockMultiConnectionHttpService(AppContext conn) {
		super(conn);
	}

	/** Add a {@link MockHttpURLConnection} tot he connection list.
	 * If multiple connections are added for the same URL they will be returned in the order added.
	 * 
	 * 
	 * @param c
	 */
	public void addConnection(MockHttpURLConnection c) {
		
		URL url = c.getURL();
		LinkedList<MockHttpURLConnection> list = connections.get(url);
		if( list == null ) {
			list = new LinkedList<>();
			connections.put(url, list);
		}
		list.add(c);
	}
}
