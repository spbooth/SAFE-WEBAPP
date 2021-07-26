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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/** A simple {@link AppContextService} to handle http client operations
 * 
 * This is implemented as a service to allow substitution for mock testing.
 * 
 * @author Stephen Booth
 *
 */
public interface HttpService extends AppContextService<HttpService>{

	/** fetch data from a URL
	 * 
	 * @param url  URL to fetch from
	 * @param props additional request headers to set
	 * @return {@link MimeStreamData}
	 * @throws HttpException 
	 */
	MimeStreamData fetch(URL url, Map<String, String> props) throws HttpException;

	/** post data to a URL
	 * 
	 * This is intended to post a single object rather than encoded form data, though
	 * a conventional form POST can be implemented by passing a {@link MimeStreamData}
	 * of type application/x-www-form-urlencoded
	 * 
	 * @param url  URL to post to
	 * @param props additional request headers to set
	 * @param input {@link MimeStreamData} to post
	 * @return {@link MimeStreamData}
	 * @throws HttpException 
	 */
	MimeStreamData post(URL url, Map<String, String> props, MimeStreamData input) throws HttpException;

	/** put data to a URL
	 * 
	 * 
	 * @param url  URL to put to
	 * @param props additional request headers to set
	 * @param input {@link MimeStreamData} to put
	 * @return {@link MimeStreamData}
	 * @throws HttpException 
	 */
	void put(URL url, Map<String, String> props, MimeStreamData input) throws HttpException;

	/** Add an encoded Basic Authorization header
	 * 
	 * @param props
	 * @param username
	 * @param password
	 */
	default void addBasicAuth(Map<String, String> props,String username,String password) {
		String encoded = Base64.getEncoder().encodeToString((username+":"+password).trim().getBytes(StandardCharsets.UTF_8));  //Java 8
		props.put("Authorization", "Basic "+encoded);
	}
	default void addBearerAuth(Map<String, String> props,String token) {
		props.put("Authorization", "Bearer "+token);
	}
	@Override
	default Class<HttpService> getType() {
		return HttpService.class;
	}
}