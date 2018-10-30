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
package uk.ac.ed.epcc.webapp.model.log;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
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


public class MessageData extends MessageDataObject implements MessageProvider , Removable{
	/** A Factory for {@link MessageData}
	 * 
	 * This is not actually needed by the application but makes the
	 * tests easier to write as these rely on a handler factory
	 * 
	 * @author spb
	 *
	 * @param <M>
	 */
	public static class Factory<M extends MessageData> extends DataObjectFactory<M>{
		public Factory(AppContext c,String table){
    		setContext(c,table);
    	}
    	@Override
		protected TableSpecification getDefaultTableSpecification(AppContext c, String table){
			return MessageDataObject.getTableSpecification();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
		 */
		@Override
		protected DataObject makeBDO(Record res) throws DataFault {
			
			return new MessageData(res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getTarget()
		 */
		@Override
		public Class<M> getTarget() {
			return (Class<M>) MessageData.class;
		}
		
	}
	
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
	public MessageData(Record rec) {
		super(rec);
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

	
}