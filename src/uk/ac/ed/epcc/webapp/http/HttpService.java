//| Copyright - The University of Edinburgh 2016                            |
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/** A simple {@link AppContextService} to handle Http operations.
 * 
 * These are implemented as a service to allow substitute code to be inserted for mock testing
 * @author spb
 *
 */
public class HttpService implements Contexed, AppContextService<HttpService> {

	private final AppContext conn;
	/**
	 * 
	 */
	public HttpService(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	/** fetch data from a URL
	 * 
	 * @param url
	 * @return
	 * @throws HttpException 
	 */
	public MimeStreamData fetch(URL url, Map<String,String> props ) throws HttpException{
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if( props != null){
				for(String key : props.keySet()){
					connection.setRequestProperty(key, props.get(key));
				}
			}

			connection.setRequestMethod("GET");
			connection.connect();
			connection.setConnectTimeout(getContext().getIntegerParameter("http.connection_timeout", 30000));
			connection.setReadTimeout(getContext().getIntegerParameter("http.read_timeout", 30000));


			int code = connection.getResponseCode();

			if( code != HttpURLConnection.HTTP_ACCEPTED){
				HttpException e = new HttpException(connection.getResponseMessage());
				e.setError_code(code);
				throw e;
			}
			String type = connection.getContentType();

			ByteArrayMimeStreamData data = new ByteArrayMimeStreamData();
			data.setMimeType(type);
			String name = url.getFile();
			if( name != null){
				if( name.contains("/")){
					name = name.substring(name.lastIndexOf("/"));
				}
				data.setName(name);
			}
			data.read(connection.getInputStream());
			return data;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super HttpService> getType() {
		return HttpService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
	
		return conn;
	}

}
