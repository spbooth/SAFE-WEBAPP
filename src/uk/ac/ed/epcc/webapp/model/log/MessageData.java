// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.log;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.mail.MessageDataObject;
/** Table to hold message data
 * Essentially this is just the serialised data from a MimeMessage object
 * This is to ensure we can recover the full message including all headers
 * 
 * We could be more memory efficient by storing the headers and content separately and 
 * recovering the message in bits but this is safer.
 * 
 * If we need to search for messages we could also store some of the header data
 * as additional DB fields
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageData.java,v 1.10 2015/11/09 16:32:09 spb Exp $")

public class MessageData extends MessageDataObject implements MessageProvider , Removable{
	public static final String DEFAULT_TABLE = "MailMessage";

	
	/** A method to bootstrap the table (for unit tests)
	 * 
	 * @param conn
	 * @throws DataFault 
	 */
	public static void checkTable(AppContext conn) throws DataFault{
		DataBaseHandlerService serv = conn.getService(DataBaseHandlerService.class);
		if( ! serv.tableExists(DEFAULT_TABLE)){
			serv.createTable(DEFAULT_TABLE, getTableSpecification());
		}
	}
	
	public MessageData(AppContext conn, int id) throws DataException {
		super(getRecord(conn,DEFAULT_TABLE, id));
	}

	public MessageData(AppContext conn, MimeMessage m) {
		super(getRecord(conn,DEFAULT_TABLE));
		setMessage(m);
	}

	
	public void remove() throws DataException {
		delete();
	}
	public MessageData copy() throws DataFault, MessagingException{
		MessageData dat = new MessageData(getContext(),getMessage());
		dat.commit();
		return dat;
	}

	public boolean editRecipients() {
		return false;
	}

	public boolean allowNewAttachments() {
		return false;
	}
}