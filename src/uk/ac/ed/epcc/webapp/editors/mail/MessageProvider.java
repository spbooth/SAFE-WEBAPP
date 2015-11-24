// Copyright - The University of Edinburgh 2011
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
	public abstract boolean allowNewAttachments();
    public boolean commit() throws DataFault;
}