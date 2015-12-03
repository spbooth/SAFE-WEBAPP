//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.editors.mail;

import java.io.InputStream;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link MessageHandler} for unit tests. it actually just
 * presents a pre-defined set of mail messages read from files.
 * so can only test non modifying operations.
 * @author spb
 *
 */

public class TestMessageHandlerFactory implements MessageHandlerFactory{

	public static final String names[] = {"fake_html","fake_mime", "fake_pdf_mime"};
	private AppContext conn;
	/**
	 * 
	 */
	public TestMessageHandlerFactory(AppContext c) {
		this.conn=c;
	}
	
	private class TestMessageProvider implements MessageProvider{
		private MimeMessage m;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#setMessage(javax.mail.internet.MimeMessage)
		 */
		public void setMessage(MimeMessage m) {
			this.m=m;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#getMessage()
		 */
		public MimeMessage getMessage() throws DataFault, MessagingException {
			return m;
		}
		 public int getMessageHash(){
	    	  
	    		   try {
	    			   MimeMessage m = getMessage();
	    			   InputStream in = m.getInputStream();
	    			   long hash = 7;
	    			   int c;
	    			   while( (c = in.read()) != -1 ){
	    				   hash = (hash * 13L + c)%2147483647L;

	    			   }
	    			   return   (int) hash;
	    		   } catch (Exception e) {
	    			   getContext().error(e,"Error making message hash");
	    			    return -1;
	    		   }
	    	   
	    }
		/* (non-Javadoc)
		 * @see uk.ac.hpcx.model.helpdesk.MessageProvider#getSubject()
		 */
		public String getSubject() {
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
		public String getRecipients() {
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
		public String getSender() {
			try {
				MimeMessage m = getMessage();
				if (m != null) {
					Address from[] = m.getFrom();
					StringBuilder sb = new StringBuilder();
					for(int i=0;i<from.length;i++){
						if( i>0){
							sb.append(", ");
						}
						sb.append(from[i]);
					}
					return sb.toString();
				}
			} catch (Exception e) {
				getContext().error(e, "Error getting sender");
			}
			return "Unknown";
		}
		

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#editRecipients()
		 */
		public boolean editRecipients() {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#allowNewAttachments()
		 */
		public boolean allowNewAttachments() {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageProvider#commit()
		 */
		public boolean commit() throws DataFault {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	private class TestComposer implements MessageComposer{
		private String name;
		TestMessageProvider prov=null;
		public TestComposer(String name){
			this.name=name;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getMessageProvider()
		 */
		public MessageProvider getMessageProvider() throws Exception {
			if( prov == null){
				Session session = Session.getInstance(getContext().getProperties(),
					null);
				prov = new TestMessageProvider();
				prov.setMessage(new MimeMessage(session, getClass().getResourceAsStream(name)));
			}
			return prov;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#canView(java.util.List, uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public boolean canView(List<String> path, SessionService<?> operator) {
			return true;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getTypeName()
		 */
		public String getTypeName() {
			return getTag();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandler#getFactory(uk.ac.ed.epcc.webapp.AppContext)
		 */
		public MessageHandlerFactory getFactory(AppContext conn) {
			return TestMessageHandlerFactory.this;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Indexed#getID()
		 */
		public int getID() {
			int i=0;
			for(String s : names){
				if( name.equals(s)){
					return i;
				}
				i++;
			}
			return -1;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#send(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public FormResult send(SessionService<?> operator) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#repopulate(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public void repopulate(SessionService<?> operator) throws Exception {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#populate(uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public void populate(SessionService<?> operator) throws Exception {
			
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#abort()
		 */
		public FormResult abort() throws DataFault {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#canEdit(java.util.List, uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		public boolean canEdit(List<String> path, SessionService<?> operator) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestComposer other = (TestComposer) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		private TestMessageHandlerFactory getOuterType() {
			return TestMessageHandlerFactory.this;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageComposer#getEmailInput()
		 */
		@Override
		public Input<String> getEmailInput() {
			return new EmailInput();
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Tagged#getTag()
	 */
	public String getTag() {
		return "TestMail";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.editors.mail.MessageHandlerFactory#getHandler(int, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public MessageHandler getHandler(int id, SessionService<?> user) {
		return new TestComposer(names[id]);
	}
    public boolean equals(Object other){
    	return other != null && other.getClass()==getClass();
    }
}