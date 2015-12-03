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
package uk.ac.ed.epcc.webapp.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/** A custom SocketFactory that can be instantiated by reflection (As required by JNDI) but
 * generates SSL sockets configured by the default {@link SSLContext} cached in the {@link SSLService}
 * The {@link SSLService#makeDefaultContext()} method needs to have been called first to populate the cache.
 * 
 * @see SSLService
 * @author spb
 *
 */
public class DefaultSocketFactory extends SocketFactory {
	public static SocketFactory getDefault(){
		return new DefaultSocketFactory(SSLService.getCachedDefaultContext().getSocketFactory());
	}

	private SSLSocketFactory inner;
	public DefaultSocketFactory(SSLSocketFactory fac){
		inner=fac;
	}
	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException,
			UnknownHostException {
		return inner.createSocket(arg0, arg1);
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		return inner.createSocket(arg0, arg1);
	}

	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
			throws IOException, UnknownHostException {
		return inner.createSocket(arg0, arg1,arg2,arg3);
	}

	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
			int arg3) throws IOException {
		return inner.createSocket(arg0, arg1,arg2,arg3);
	}

}