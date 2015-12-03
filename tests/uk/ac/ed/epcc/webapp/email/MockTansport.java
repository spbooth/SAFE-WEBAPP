//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.email;

import java.util.Vector;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;

/** A Mock {@link Transport} for testing
 * 
 * Note you need to add the test_settings directory to the classpath 
 * to use this in a test because the Transport is invoked from static methods
 * that search META-INF for config files. 
 * @author spb
 *
 */

public class MockTansport extends Transport {

	private static Vector<Message> messages=new Vector<Message>();
	private static Vector<Address[]> addresses=new Vector<Address[]>();
	
	public static void clear(){
		messages.clear();
		addresses.clear();
	}
	
	public static int nSent(){
		return messages.size();
	}
	public static Address[] getAddress(int i){
		return addresses.get(i);
	}
	
	public static boolean containsAddress(int i, Address addr){
		for( Address a : addresses.get(i)){
			if(a.equals(addr)){
				return true;
			}
		}
		
		return false;
	}
	
	public static Message getMessage(int i){
		return messages.get(i);
	}
	/**
	 * @param session
	 * @param urlname
	 */
	public MockTansport(Session session, URLName urlname) {
		super(session, urlname);
		System.out.println("MockTransport created");
	}

	/* (non-Javadoc)
	 * @see javax.mail.Transport#sendMessage(javax.mail.Message, javax.mail.Address[])
	 */
	@Override
	public void sendMessage(Message arg0, Address[] arg1)
			throws MessagingException {
		System.out.println("Send message called");
		messages.add(arg0);
		addresses.add(arg1);
	}

	@Override
	public synchronized void close() throws MessagingException {
		System.out.println("close");
	}

	@Override
	public void connect() throws MessagingException {
		System.out.println("connect()");
	}

	@Override
	public synchronized void connect(String arg0, int arg1, String arg2,
			String arg3) throws MessagingException {
		System.out.println("Connect("+arg0+","+arg1+","+arg2+","+arg3+")");
	}

	@Override
	public void connect(String host, String user, String password)
			throws MessagingException {
		System.out.println("connect("+host+","+user+","+password+")");
	}

	@Override
	public void connect(String user, String password) throws MessagingException {
		// TODO Auto-generated method stub
		super.connect(user, password);
	}

}