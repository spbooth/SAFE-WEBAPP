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
package uk.ac.ed.epcc.webapp.model.mail;


import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageHandler;
import uk.ac.ed.epcc.webapp.editors.mail.MessageProvider;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.BlobType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;
/** Handy common base class for DataObjects that hold MailMessages
 * This class would normally be used when the {@link MessageHandler} 
 * needs to link to the message by reference. 
 * @author spb
 *
 */
public abstract class MessageDataObject extends DataObject implements
		MessageProvider {
	private static final String MESSAGE = "Message";

	public static TableSpecification getTableSpecification(){
		TableSpecification spec = new TableSpecification("MessageID");
		spec.setField(MESSAGE, new BlobType());
		//spec.setField(MESSAGE, new StringFieldType(true, null, 4096));
		return spec;
	}
	private boolean auto_save=false;
	MimeMessage m = null;
	  int hash=-1;

	
	protected MessageDataObject(Record r) {
		super(r);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#setMessage(javax.mail.internet.MimeMessage)
	 */
	public final void setMessage(MimeMessage m) {
		this.m = m;
		auto_save=false; // implementation may be read only
		hash = -1;
	}

	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#getMessage()
	 */
	public final MimeMessage getMessage() throws DataFault, MessagingException {
		if (m == null) {
			Session session = Session.getInstance(getContext().getProperties(),
					null);
			StreamData sd = record.getStreamDataProperty(MESSAGE);
			if (sd == null) {
				return null;
			}
			m = new MimeMessage(session, sd.getInputStream());
			auto_save=true; // These we can save
		}
		return m;
	}
    public final int getMessageHash(){
    	   if( hash == -1 ){
    		   try {
    			   MimeMessage m = getMessage();
    			   if( m == null ){
    				   // for anonymised DB
    				   return 0;
    			   }
    			   InputStream in = m.getInputStream();
    			   long hash = 7;
    			   int c;
    			   while( (c = in.read()) != -1 ){
    				   hash = (hash * 13L + c)%2147483647L;

    			   }
    			   this.hash = (int) hash;
    		   } catch (Exception e) {
    			   getContext().error(e,"Error making message hash");
    			   hash=-1;
    		   }
    	   }
		return hash;
    }
	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#getSubject()
	 */
	public final String getSubject() {
		try {
			MimeMessage m = getMessage();
			if (m == null) {
				return "";
			}
			String s = m.getSubject();
			if (s != null) {
				return s;
			}
		} catch (Exception e) {
			getContext().error(e, "Error getting subject");
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#getRecipients()
	 */
	public final String getRecipients() {
		try {
			MimeMessage m = getMessage();
			if (m == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			javax.mail.Address[] recip = m.getAllRecipients();
			if (recip != null && recip.length > 0) {
				sb.append(recip[0]);
				for (int i = 1; i < recip.length; i++) {
					sb.append(", ");
					sb.append(recip[i]);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			getContext().error(e, "Error getting recipients");
		}
		return "";
	}
	
	@Override
	public final void pre_commit(boolean dirty) throws DataFault {
	
		/* All commits to this object forces a write back of the message
		 * as it is hard to tell if the message has changed.
		 * 
		 */
		hash = -1;
		try {
			if (m != null) {
				//StreamData sd = new MessageStreamData(m);
				StreamData sd = new ByteArrayStreamData();
				// can't call saveChanges on imported imap message
				if(auto_save){
					m.saveChanges();
				}
				m.writeTo(sd.getOutputStream());
				//getLogger().debug("update value to "+sd);
				record.setProperty(MESSAGE, sd);
				m=null; // force re-read 
			}
		} catch (Exception e) {
			getContext().error(e, "Error writing mail message");
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#getSender()
	 */
	public final String getSender() {
		try {
			MimeMessage m = getMessage();
			if (m != null) {
				Address from[] = m.getFrom();
				StringBuilder sb = new StringBuilder();
				if( from != null ){
					for(int i=0;i<from.length;i++){
						if( i>0){
							sb.append(", ");
						}
						sb.append(from[i]);
					}
				}
				return sb.toString();
			}
		}catch(MessagingException me){
			// bogus email probably
			getLogger().warn("Error getting sender",me);
		} catch (Exception e) {
			getLogger().error("Error getting sender",e);
		}
		return "Unknown";
	}

	public final String getMessageID(){
		try{
			MimeMessage m = getMessage();
			if( m != null){
				return m.getMessageID();
			}
		}catch(Exception e){
			getLogger().error("Error getting messageID", e);
		}
		return null;
	}
	
	public static void anonymise(AppContext c, String table) throws DataFault{
		DatabaseService db_service = c.getService(DatabaseService.class);
		try{
			if( Repository.READ_ONLY_FEATURE.isEnabled(c)){
				return;
			}
			
			SQLContext conn = db_service.getSQLContext();
			if (conn == null) {
				throw new DataFault("No connection");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE ");
			conn.quote(sb, table);
			sb.append(" SET ");
			conn.quote(sb, MESSAGE);
			sb.append("=null");

			try(Statement stmt = conn.getConnection().createStatement()){
				int results = stmt.executeUpdate(sb.toString());
			}
			}catch(SQLException e){
				db_service.handleError("Error in delete", e);
			}
	}
}