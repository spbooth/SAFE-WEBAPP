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

/** A custom SocketFactory that can be instantiated by reflection (As required by JNDI) but
 * generates SSL sockets cached in a thread-local 
 * @see SSLService
 * @author spb
 *
 */
public class CachedSocketFactory extends SocketFactory {
	static ThreadLocal<SocketFactory> local = new ThreadLocal<SocketFactory>();

	  public static SocketFactory getDefault()
	  {
	    SocketFactory result = local.get();
	    if ( result == null )
	      throw new IllegalStateException();
	    return result;
	  }

	  public static void set( SocketFactory factory )
	  {
	    local.set( factory );
	  }

	  public static void remove()
	  {
	    local.remove();
	  }

	/* (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int)
	 */
	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		return getDefault().createSocket(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int)
	 */
	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return getDefault().createSocket(host, port);
	}

	/* (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
	 */
	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return getDefault().createSocket(host, port, localHost, localPort);
	}

	/* (non-Javadoc)
	 * @see javax.net.SocketFactory#createSocket(java.net.InetAddress, int, java.net.InetAddress, int)
	 */
	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
			throws IOException {
		return getDefault().createSocket(address,port,localAddress,localPort);
	}
}