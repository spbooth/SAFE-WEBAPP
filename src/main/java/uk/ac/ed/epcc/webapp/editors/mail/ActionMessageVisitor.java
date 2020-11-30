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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamDataWrapper;



public class ActionMessageVisitor extends AbstractVisitor {
	boolean modified=false;
	EditAction request;
	Object data;
	public ActionMessageVisitor(AppContext conn, EditAction action,Object data){
		super(conn);
		this.request=action;
		this.data=data;
	}


	private Multipart flattenEmail(AppContext conn,MimeMessage m) throws MessagingException, WalkerException, IOException {
		FlattenVisitor fv = new FlattenVisitor(conn);
		MessageWalker mw = new MessageWalker(conn);
		mw.visitMessage(m, fv);
		return fv.getMultiPart();
	}
	private Multipart flattenMultiPart(AppContext conn,MimeMultipart mp) throws  WalkerException, MessagingException, IOException {
	  
		FlattenVisitor fv = new FlattenVisitor(conn);
		MessageWalker mw = new MessageWalker(conn);
		mw.visitMultiPart(null,mp, fv);
		return fv.getMultiPart();
	   
	}
	private String mergeMultiPart(AppContext conn,MimeMultipart mp) throws WalkerException {
		QuoteVisitor qv = new QuoteVisitor(conn);
		MessageWalker mw = new MessageWalker(conn);
		mw.visitMultiPart(null, mp, qv);
		return qv.toString();
		
	}
	private String quoteEmail(AppContext conn,MimeMessage m) throws WalkerException {
		QuoteVisitor qv = new QuoteVisitor(conn);
		MessageWalker mw = new MessageWalker(conn);
		mw.visitMessage(m,qv);
		return qv.toString();
	}		
	 private boolean empty(String s){
	    	return s==null || s.trim().length()==0;
	 }
	 protected void addRecipient(AppContext c,MimeMessage m,
			 jakarta.mail.Message.RecipientType cc,
			 String address
			 ) throws WalkerException{
		 try{
			 // works with single values or comma seperated list
			 for(InternetAddress a : Emailer.parseEmailList(address)){
				 m.addRecipient(cc, a);
			 }
          modified=true;
		
		 }catch(Exception e){
			 // need to tell operator there is a problem
			 // as input was specified
			 throw new WalkerException(e);
		 }
	 }
	 protected void deleteRecipient(MimeMessage m,
				jakarta.mail.Message.RecipientType cc, int id) throws MessagingException {
               if( m.getAllRecipients().length == 1){
            	   // always leave one
            	   return;
               }
		       Address old[] = m.getRecipients(cc);
			   Address new_recip[] = new Address[old.length-1];
			   for(int i=0;i<id;i++){
				   new_recip[i] = old[i];
			   }
			   for(int i=id+1;i<old.length;i++){
				   new_recip[i-1] = old[i];
			   }
			   m.setRecipients(cc, new_recip);
			   modified=true;
		}
	 
	 protected void addReplyTo(AppContext c,MimeMessage m,
			 String address
			 ) throws WalkerException{
		 try{
			 Set<Address> reply = new HashSet<>();
			 Address current[] = m.getReplyTo();
			 if( current != null ) {
				 for(Address a : current) {
					 reply.add(a);
				 }
			 }
			 // works with single values or comma seperated list
			 for(InternetAddress a : Emailer.parseEmailList(address)){
				 reply.add( a);
			 }
			 if( reply.isEmpty()) {
				 m.setReplyTo(null);
			 }else {
				 m.setReplyTo(reply.toArray(new Address[reply.size()]));
			 }
          modified=true;
		
		 }catch(Exception e){
			 // need to tell operator there is a problem
			 // as input was specified
			 throw new WalkerException(e);
		 }
	 } 
	 protected void deleteReplyTo(MimeMessage m, int id) throws MessagingException {
            
		       Address old[] = m.getReplyTo();
		       if( old == null || old.length == 0) {
		    	   return;
		       }
		       if( old.length == 1) {
		    	   m.setReplyTo(null);
		    	   modified=true;
		    	   return;
		       }
			   Address new_reply[] = new Address[old.length-1];
			   for(int i=0;i<id;i++){
				   new_reply[i] = old[i];
			   }
			   for(int i=id+1;i<old.length;i++){
				   new_reply[i-1] = old[i];
			   }
			   m.setReplyTo(new_reply);
			   modified=true;
		}
	@Override
	public void doIOError(MessageWalker w, IOException e)
			throws WalkerException {
		getLogger().error("Error in ActionVisitor",e);
	}

	@Override
	public void doMessageError(MessageWalker w, MessagingException e)
			throws WalkerException {
		getLogger().error("Error in ActionVisitor",e);
	}

	

	@Override
	public void doBCC(Address address, int i, int length, MessageWalker messageWalker) throws WalkerException {
		if( request == EditAction.Delete){
			try {
				deleteRecipient(messageWalker.getCurrentMessage(), jakarta.mail.Message.RecipientType.BCC, i);
                modified=true;
			} catch (MessagingException e) {
				getLogger().error("Error deleting recipient",e);
			}
		}
	}


	@Override
	public void doCC(Address address, int i, int length, MessageWalker messageWalker) throws WalkerException {
		if( request == EditAction.Delete){
			try {
				deleteRecipient(messageWalker.getCurrentMessage(), jakarta.mail.Message.RecipientType.CC, i);
                modified=true;
			} catch (MessagingException e) {
				getLogger().error("Error deleting recipient",e);
			}
		}
	}


	@Override
	public void doRecipients(MessageWalker walker) throws WalkerException {
		if( request == EditAction.AddCC){
			addRecipient(walker.getContext(), walker.getCurrentMessage(), jakarta.mail.Message.RecipientType.CC, (String)data);
		}
		if( request == EditAction.AddTo){
			addRecipient(walker.getContext(), walker.getCurrentMessage(), jakarta.mail.Message.RecipientType.TO, (String)data);
		}
		if( request == EditAction.AddBcc){
			addRecipient(walker.getContext(), walker.getCurrentMessage(), jakarta.mail.Message.RecipientType.BCC, (String)data);
		}
	}
	@Override
	public void doSenders(MessageWalker walker) throws WalkerException {
		if( request == EditAction.AddReplyTo){
			addReplyTo(walker.getContext(), walker.getCurrentMessage(),  (String)data);
		}
		
	}

	@Override
	public void doReplyTo(Address[] cc, MessageWalker messageWalker) throws WalkerException {
		// TODO Auto-generated method stub
		super.doReplyTo(cc, messageWalker);
	}


	@Override
	public void doReplyTo(Address address, int i, int length, MessageWalker messageWalker) throws WalkerException {
		if( request == EditAction.Delete){
			try {
				deleteReplyTo(messageWalker.getCurrentMessage(), i);
                modified=true;
			} catch (MessagingException e) {
				getLogger().error("Error deleting recipient",e);
			}
		}
	}


	@Override
	public void endMessage(MimePart parent,MimeMessage m, MessageWalker messageWalker)
			throws WalkerException {
		if(modified){
			try {
				m.saveChanges();
			} catch (MessagingException e) {
				throw new WalkerException("Error saving changes",e);
			}
		}

	}

	@Override
	public void doTo(Address address, int i, int length, MessageWalker messageWalker) throws WalkerException {
		if( request == EditAction.Delete){
			try {
				deleteRecipient(messageWalker.getCurrentMessage(), jakarta.mail.Message.RecipientType.TO, i);
	            modified=true;
			} catch (MessagingException e) {
				getLogger().error("Error deleting recipient",e);
			}
		}
	}


	@Override
	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		try{
		if(mp.getCount() == 1){
			// only one part left
			// note we remove single parent multiparts on any edit operation
			// rooted below that multipart, in general they are a bad idea.
			// this does not always work if the part left is an attachment without a handler
			MimeBodyPart left = (MimeBodyPart) mp.getBodyPart(0);
			parent.setContent(left.getContent(), left.getContentType());
			modified = true;
		}
		}catch(Exception e){
			getLogger().error("Error removing single part multipart",e);
		}
	}



	@Override
	public boolean startMessage(MimePart parent,MimeMessage m, MessageWalker messageWalker)
			throws WalkerException {
		try{
			if( messageWalker.matchPath()){
				if(messageWalker.isSubMessage() ){
					if( parent == null ){
						throw new WalkerException("Null parent and action specified for here ");
					}
					if( request == EditAction.Quote){
						parent.setText(quoteEmail(messageWalker.getContext(),m));
						parent.setDisposition(Part.INLINE);
						modified=true;
						return false;
					}
					if( request == EditAction.Flatten){
						parent.setContent(flattenEmail(messageWalker.getContext(),m));
						modified=true;
						return false;
					}
				}
				if( request == EditAction.Upload){
					if( data != null){
						Object o = m.getContent();
						MimeStreamDataWrapper msd = new MimeStreamDataWrapper((MimeStreamData) data);
						MultipartMailBuilder mb;
						if( o instanceof MimeMultipart){
							mb = new MultipartMailBuilder((MimeMultipart) o);
						}else if( o instanceof String){
							mb = new MultipartMailBuilder();
							mb.addText((String)o);
						}else if( o instanceof MimeMessage) {
							// This seems to happend if all parts except an attached message
							// are deleted
							mb = new MultipartMailBuilder();
							mb.addMessage((MimeMessage)o);
						}else if( o instanceof MimeBodyPart) {
							// Never seen this but this is how we would handle it.
							mb = new MultipartMailBuilder();
							mb.addBodyPart((MimeBodyPart)o);
						}else {
							throw new WalkerException("Unexpected content type "+o.getClass().getCanonicalName());
						}
						mb.addDataSource(msd, "", msd.getName());
						m.setContent(mb.getMultipart());
						modified=true;
					}
					return false;
				}
			}
			return true;
		}catch(Exception e){
			throw new WalkerException("Error editing message",e);
		}
	}

	@Override
	public boolean startMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		if( request == EditAction.Merge && messageWalker.matchPath()){
			try {
				parent.setText(mergeMultiPart(messageWalker.getContext(),mp));
			} catch (Exception e) {
				throw new WalkerException("Error merging multipart",e);
			}
			modified=true;
			return false;
		}
		if( request == EditAction.Flatten && messageWalker.matchPath()){
			try {
				parent.setContent(flattenMultiPart(messageWalker.getContext(),mp));
			} catch (Exception e) {
				throw new WalkerException("Error flattening multipart",e);
			}
			modified=true;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean startSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		if(request == EditAction.Delete && messageWalker.matchPath() && count > 1){
			try {
				mp.removeBodyPart(i);
			} catch (MessagingException e) {
				throw new WalkerException("Error removing part",e);
			}
			modified=true;
			// once its gone don't want to recurse into it.
			return false;
		}
		return true;
	}

	@Override
	public void visit(MimePart parent, String content,
			MessageWalker messageWalker) throws WalkerException {
		try{
		if( request == EditAction.Update){
			String new_text=(String) data;
			if( empty(new_text)){
				throw new WalkerException("No update text");
			}
			// preserve the previous type.
			String type = parent.getContentType();
			String string = wrapForEdit(new_text);
			//getLogger().debug(string);
			//for(int i =0 ; i< string.length() ; i++) {
			//	char c = string.charAt(i);
			//	int val = c;
			//	getLogger().debug("char["+i+"] is "+val+" ["+c+"]\n");
			//}
			if( parent.isMimeType("text/plain")){
				if( Emailer.needsEncoding(string)){
					parent.setText(string, Emailer.DEFAULT_ENCODING);
				}else{
					parent.setText(string);
				}
			}else{
				parent.setContent(string,type);
			}
			modified= true;
		}
		}catch(MessagingException e){
			throw new WalkerException("Error updating part",e);
		}
	}

	


	@Override
	public void doSubject(String subject, MessageWalker messageWalker)
			throws WalkerException {
		try{
			if( request == EditAction.Update){
				String new_text=(String) data;
				// remove newlines
				new_text=new_text.replace('\n', ' ');
				if( empty(new_text)){
					throw new WalkerException("No update text");
				}
				MimeMessage m = messageWalker.getCurrentMessage();
				if( Emailer.needsEncoding(new_text)){
					m.setSubject(new_text, Emailer.DEFAULT_ENCODING);
				}else{
					m.setSubject(new_text);
				}
				modified= true;
			}
		}catch(MessagingException e){
				throw new WalkerException("Error updating part",e);
		}
		super.doSubject(subject, messageWalker);
	}


	public boolean modified() {
		return modified;
	}

}