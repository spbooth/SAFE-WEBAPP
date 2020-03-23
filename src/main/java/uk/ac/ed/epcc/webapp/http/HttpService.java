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
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/** A simple {@link AppContextService} to handle http client operaitons
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
	 * This is intended to post a single object rather than encoded form data.
	 * 
	 * @param url  URL to post to
	 * @param props additional request headers to set
	 * @param input {@link MimeStreamData} to post
	 * @return {@link MimeStreamData}
	 * @throws HttpException 
	 */
	MimeStreamData post(URL url, Map<String, String> props, MimeStreamData input) throws HttpException;


	@Override
	default Class<HttpService> getType() {
		return HttpService.class;
	}
}