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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

	public abstract void setMessage(MimeMessage m);

	public abstract MimeMessage getMessage() throws DataFault,
			MessagingException;

	public abstract int getMessageHash();
	public abstract String getSubject();

	public abstract String getRecipients();

	public abstract String getSender();
	public abstract boolean editRecipients();
	public abstract boolean bccOnly();
	public abstract boolean allowNewAttachments();
    public boolean commit() throws DataFault;
}