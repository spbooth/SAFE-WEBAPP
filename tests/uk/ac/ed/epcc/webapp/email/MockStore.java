//| Copyright - The University of Edinburgh 2019                            |
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

import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * @author Stephen Booth
 *
 */
public class MockStore extends Store {
	private static Map<String,MockFolder> folders=new HashMap<>();
	/**
	 * @param session
	 * @param urlname
	 */
	public MockStore(Session session, URLName urlname) {
		super(session, urlname);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.mail.Store#getDefaultFolder()
	 */
	@Override
	public Folder getDefaultFolder() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.mail.Store#getFolder(java.lang.String)
	 */
	@Override
	public Folder getFolder(String name) throws MessagingException {
		MockFolder result = folders.get(name);
		if( result == null) {
			result = new MockFolder(this, name);
			folders.put(name,result);
		}
		return result;
	}
	
	public  static int nfolders() {
		return folders.size();
	}

	/* (non-Javadoc)
	 * @see javax.mail.Store#getFolder(javax.mail.URLName)
	 */
	@Override
	public Folder getFolder(URLName url) throws MessagingException {
		// TODO Auto-generated method stub
		return getFolder(url.getFile());
	}

	@Override
	protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
		// TODO Auto-generated method stub
		return true;
	}
	
	public static void reset() {
		folders.clear();
	}

}
