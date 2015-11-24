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
