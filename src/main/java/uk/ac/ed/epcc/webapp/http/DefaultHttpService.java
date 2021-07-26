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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

/** A simple {@link HttpService} 
 * 
 * This uses the most basic functionality provided in the base java classes and requires no new dependencies.
 * More advanced implementations using dedicated client libraries can be substituted if these are not sufficient
 * 
 *
 */
public class DefaultHttpService extends AbstractContexed implements AppContextService<HttpService>, HttpService {

	/**
	 * 
	 */
	public DefaultHttpService(AppContext conn) {
		super(conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.http.HttpService#fetch(java.net.URL, java.util.Map)
	 */
	@Override
	public MimeStreamData fetch(URL url, Map<String,String> props ) throws HttpException{
		HttpURLConnection connection=null;
		try {
			connection = connect(url);
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

			if( code != HttpURLConnection.HTTP_OK){
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
			throw new HttpException("Error GET from "+url,e);
		}finally {
			if( connection != null) {
				connection.disconnect();
			}
		}
	}

	/** extension point to allow mock insertion
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	protected HttpURLConnection connect(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.http.HttpService#post(java.net.URL, java.util.Map, uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData)
	 */
	@Override
	public MimeStreamData post(URL url, Map<String,String> props, MimeStreamData input ) throws HttpException{
		return upload(url,"POST",props,input);
	}
	@Override
	public void put(URL url, Map<String,String> props, MimeStreamData input ) throws HttpException{
		upload(url,"PUT",props,input);
	}
	private MimeStreamData upload(URL url,String method, Map<String,String> props, MimeStreamData input ) throws HttpException{
		HttpURLConnection connection=null;
		try {
			connection = connect(url);
			if( props != null){
				for(String key : props.keySet()){
					connection.setRequestProperty(key, props.get(key));
				}
			}

			Logger log = getLogger();
			log.debug(method+" to "+url);
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type", input.getContentType());
			log.debug("Content-Type: "+input.getContentType());
			connection.setRequestProperty("Content-Length", Long.toString(input.getLength()));
			log.debug("Content-Length: "+input.getLength());
			connection.setDoOutput(true);
		    
			
			connection.setConnectTimeout(getContext().getIntegerParameter("http.connection_timeout", 30000));
			connection.setReadTimeout(getContext().getIntegerParameter("http.read_timeout", 30000));
			connection.connect();
			input.append(connection.getOutputStream());

			int code = connection.getResponseCode();

			if( code < HttpURLConnection.HTTP_OK || code > HttpURLConnection.HTTP_NO_CONTENT){
				HttpException e = new HttpException(connection.getResponseMessage());
				e.setError_code(code);
				throw e;
			}
			if( method.equals("PUT") || code == HttpURLConnection.HTTP_NO_CONTENT) {
				// No content returned from PUT
				return null;
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
			throw new HttpException("Exception "+method+" to "+url,e);
		}finally {
			if( connection!= null) {
				connection.disconnect();
			}
		}
	}


	
}
