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

import java.util.Iterator;
import java.util.LinkedList;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

/**
 * @author Stephen Booth
 *
 */
public class MockFolder extends Folder {
	
	private String name;
	
	private LinkedList<Message> messages = new LinkedList<>();
	private int flags=0;
	private int open=0;
	/**
	 * @param store
	 */
	public MockFolder(Store store,String name) {
		super(store);
		this.name=name;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getFullName()
	 */
	@Override
	public String getFullName() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getParent()
	 */
	@Override
	public Folder getParent() throws MessagingException {
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#exists()
	 */
	@Override
	public boolean exists() throws MessagingException {
		return flags != 0;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#list(java.lang.String)
	 */
	@Override
	public Folder[] list(String pattern) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getSeparator()
	 */
	@Override
	public char getSeparator() throws MessagingException {
		return '/';
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getType()
	 */
	@Override
	public int getType() throws MessagingException {
		return flags;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#create(int)
	 */
	@Override
	public boolean create(int type) throws MessagingException {
		flags=type;
		return true;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#hasNewMessages()
	 */
	@Override
	public boolean hasNewMessages() throws MessagingException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getFolder(java.lang.String)
	 */
	@Override
	public Folder getFolder(String name) throws MessagingException {
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#delete(boolean)
	 */
	@Override
	public boolean delete(boolean recurse) throws MessagingException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#renameTo(jakarta.mail.Folder)
	 */
	@Override
	public boolean renameTo(Folder f) throws MessagingException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#open(int)
	 */
	@Override
	public void open(int mode) throws MessagingException {
		open=mode;

	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#close(boolean)
	 */
	@Override
	public void close(boolean expunge) throws MessagingException {
		if( expunge) {
			expunge();
		}
		open=0;

	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return open != 0;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getPermanentFlags()
	 */
	@Override
	public Flags getPermanentFlags() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getMessageCount()
	 */
	@Override
	public int getMessageCount() throws MessagingException {
		return messages.size();
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#getMessage(int)
	 */
	@Override
	public Message getMessage(int msgnum) throws MessagingException {
		return messages.get(msgnum-1);
	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#appendMessages(jakarta.mail.Message[])
	 */
	@Override
	public void appendMessages(Message[] msgs) throws MessagingException {
		for(Message m : msgs) {
			messages.add(new MimeMessage((MimeMessage)m)); // make a copy
		}

	}

	/* (non-Javadoc)
	 * @see jakarta.mail.Folder#expunge()
	 */
	@Override
	public Message[] expunge() throws MessagingException {
		LinkedList<Message> removed = new LinkedList<>();
		for(Iterator<Message> it = messages.iterator(); it.hasNext();) {
			Message m = it.next();
			if( m.getFlags().contains(Flags.Flag.DELETED)) {
				it.remove();
				removed.add(m);
			}
		}
		return removed.toArray(new Message[removed.size()]);
	}

}
