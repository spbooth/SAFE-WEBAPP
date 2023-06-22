//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.mail;

import java.util.LinkedList;
import java.util.List;

import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.editors.mail.MessageComposer;
import uk.ac.ed.epcc.webapp.editors.mail.MessageHandler;
import uk.ac.ed.epcc.webapp.editors.mail.MessageHandlerFactory;
import uk.ac.ed.epcc.webapp.editors.mail.MessageProvider;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.mail.AbstractMessageHandlerFactory.Status.Value;
import uk.ac.ed.epcc.webapp.session.SessionService;


/** A superclass for {@link MessageHandlerFactory}s where the
 * message data is stored directly in the table not via a reference to
 * a remote table. This is primarily an example class and for use in
 * unit tests but may be useful as a base class for some use-cases.
 * It will need to be extended to add additional fields nedded to specify the access control logic and
 * how to populate the initial message
 * 
 * Each 
 * @author spb
 * @param <H> 
 *
 */

public abstract class AbstractMessageHandlerFactory<H extends AbstractMessageHandlerFactory.Provider> extends DataObjectFactory<H>
		implements MessageHandlerFactory {

	public AbstractMessageHandlerFactory(AppContext conn, String table){
		setContext(conn, table);
	}
	public static class Status extends BasicType<Status.Value>{
		
		protected Status() {
			super("Status");
		}

		public class Value extends BasicType.Value{

			/**
			 * @param tag
			 * @param name
			 */
			protected Value(String tag, String name) {
				super(Status.this, tag, name);
			}
			
		}
	}
	private static final Status status = new Status();
	protected static final Status.Value COMPOSE = status.new Value("C","Compose");
	protected static final Status.Value SENT = status.new Value("S","Sent");
	protected static final Status.Value ABANDONED = status.new Value("A","Abandoned");
	
	public static abstract class Provider extends MessageDataObject implements MessageProvider{

		/**
		 * @param r
		 */
		protected Provider(Record r) {
			super(r);
		}

		public Status.Value getStatus(){
			return record.getProperty(status);
		}
		


		
		/**
		 * @param val
		 */
		public void setStatus(Value val) {
			record.setProperty(status, val);
			
		}
		public abstract String[] getdefaultRecipients();
		
		
	}
	public class Handler implements MessageHandler, Contexed{
		public Handler(H provider) {
			super();
			this.provider = provider;
		}

		protected final H provider;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Indexed#getID()
		 */
		@Override
		public List<String> getPath() {
			LinkedList<String> path = new LinkedList<>();
			path.add(Integer.toString(provider.getID()));
			return path;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getMessageProvider()
		 */
		@Override
		public MessageProvider getMessageProvider() throws Exception {
			return provider;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#canView(java.util.List, uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean canView(List<String> path, SessionService<?> operator) {
			return canRead(provider, operator);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return getTag();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getFactory(uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public MessageHandlerFactory getFactory(AppContext conn) {
			return AbstractMessageHandlerFactory.this;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
		 */
		@Override
		public AppContext getContext() {
			return AbstractMessageHandlerFactory.this.getContext();
		}
		
	}
	public class Composer extends Handler implements MessageComposer{

		/**
	
		 * @param provider
		 */
		public Composer(H provider) {
			super(provider);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#send(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public FormResult send(SessionService<?> operator) throws Exception {
			MimeMessage m = getMessageProvider().getMessage();
			m.saveChanges();
			Emailer es = Emailer.getFactory(getContext());
			es.doSendNow(m);
			provider.setStatus(SENT);
			provider.commit();
			return new MessageResult("message_sent");
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#repopulate(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public void repopulate(SessionService<?> operator) throws Exception {
			Emailer es = Emailer.getFactory(getContext());
			InternetAddress from = null;
			if( operator.haveCurrentUser()){
				String email = operator.getCurrentPerson().getEmail();
				if( email != null){
					from = new InternetAddress(email);
				}
			}
			MimeMessage m = es.makeBlankEmail(getContext(), provider.getdefaultRecipients(), from,true, null);
			MimeMultipart mp = new MimeMultipart("mixed");
			  MimeBodyPart mbp = new MimeBodyPart();
			  mbp.setText("");
			  mbp.setDisposition(Part.INLINE);
			  mp.addBodyPart(mbp);
			  m.setContent(mp);
			provider.setMessage(m);
			provider.commit();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#populate(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public void populate(SessionService<?> operator) throws Exception {
			if( provider.getMessage() == null ){
				repopulate(operator);
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#abort()
		 */
		@Override
		public FormResult abort() throws DataFault {
			provider.setStatus(ABANDONED);
			provider.commit();
			return new MessageResult("message_abandoned");
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#canEdit(java.util.List, uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean canEdit(List<String> path, SessionService<?> operator) {
			return AbstractMessageHandlerFactory.this.canEdit(provider, operator);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#getEmailInput()
		 */
		@Override
		public Input<String> getEmailInput() {
			return new EmailInput();
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#editRecipients()
		 */
		@Override
		public boolean editRecipients() {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#bccOnly()
		 */
		@Override
		public boolean bccOnly() {
			return false;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#allowNewAttachments()
		 */
		@Override
		public boolean allowNewAttachments() {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#showBcc()
		 */
		@Override
		public boolean showBcc() {
			return true;
		}

	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandlerFactory#getHandler(int, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public MessageHandler getHandler(LinkedList<String> path, SessionService<?> user) {
		try {
			if( path.size() > 0) {
				H h = find(Integer.parseInt(path.get(0)));
				return getHandler(user, h);
			}
		} catch (Exception e) {
			getLogger().error("Error getting MessageHander", e);
			
		}
		return null;
	}

	/**
	 * @param user
	 * @param h
	 */
	public MessageHandler getHandler(SessionService<?> user, H h) {
		if( canEdit(h, user)){
			return new Composer(h);
		}
		if( canRead(h, user)){
			return new Handler(h); 
		}
		return null;
	}

	public boolean canRead(H h, SessionService<?> user){
		return true;
	}
	public boolean canEdit(H h, SessionService<?> user){
		return canRead(h, user) && (h.getStatus() == COMPOSE);
	}
	

	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = MessageDataObject.getTableSpecification();
		spec.setField(status.getField(), status.getFieldType(COMPOSE));
		return spec;
	}

}