//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** This interface indicates an object that stores a MimeMessage
 * In addition to get/set methods for the MimeMessage there are also
 * accessor functions for top-level metadata like subject.
 * This is to allow a 2-level implementation with a lightweight top level object 
 * only containing the metadata and a larger underlying object that is only accessed
 * if the full message is required.
 * 
 * The use of an interface also makes it easier to re-use code for different 
 * storage locations.
 * 
 * 
 * @author spb
 *
 */
public interface MessageProvider {

	/** set the {@link MimeMessage} to store
	 * 
	 * @param m
	 */
	public abstract void setMessage(MimeMessage m);

	/** get the {@link MimeMessage}
	 * 
	 * @return
	 * @throws DataFault
	 * @throws MessagingException
	 */
	public abstract MimeMessage getMessage() throws DataFault,
			MessagingException;

	/** get a hash for the message
	 * 
	 * @return
	 */
	public abstract int getMessageHash();
	public abstract String getSubject();

	public abstract String getRecipients();

	public abstract String getSender();
	/** persist message to database
	 * 
	 * @return true if changed
	 * @throws DataFault
	 */
    public boolean commit() throws DataFault;
    
    /** Check if the message is in a sendable state
     * 
     * @return
     * @throws DataFault
     * @throws MessagingException
     */
    public default boolean canSend() throws DataFault, MessagingException {
    	MimeMessage m = getMessage();
    	Address[] recip = m.getAllRecipients();
    	if( recip == null || recip.length==0) {
    		return false;
    	}
    	if( recip.length == 1 && recip[0].equals(new InternetAddress("undisclosed-recipients:;"))) {
    		// no real recipients for a mailing list
    		return false;
    	}
    	return true;
    }
}