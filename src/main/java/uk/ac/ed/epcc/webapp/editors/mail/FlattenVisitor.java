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
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
/** Flatten a MimeMessage/MultiPart  into a single level MultiPart with no nested messages
 * Nested messages are quoted to show that they are nested.
 * 
 * This can be used to visit a message flattening it to a multipart or to visit a multipart reducing it to a single
 * level. 
 * 
 * @author spb
 *
 */


public class FlattenVisitor extends PrefixVisitor<MultipartMailBuilder> {

	MultipartMailBuilder mb;
	boolean include_attachments=true;
	boolean auto_flush=false;
	
	public FlattenVisitor(AppContext conn) throws MessagingException, IOException{
		this(conn,true);
	}
	public FlattenVisitor(AppContext conn,boolean include) throws MessagingException,
			IOException {
		super(conn);
		this.include_attachments=include;
		this.mb = new MultipartMailBuilder();
	}
	/** Force a flush between level-0 parts rather than a merge.
	 * This only works when visiting a multi-part as everything is at least level-1 if you
	 * visit a message.
	 * @param val
	 */
	public void setAutoFlush(boolean val){
		auto_flush=val;
		mb.setAutoFlush(val);
	}
	@Override
	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		boolean res = super.startMessage(parent, m, messageWalker);
			// don't autoflush within a nested message
		mb.setAutoFlush(false);
		return res;
	}
	@Override
	public void endMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		    super.endMessage(parent, m, messageWalker);
	      	if(messageWalker.getMessageLevel()==1){
				//exiting last level
				if(auto_flush){
					// restore requested auto_flush
					mb.setAutoFlush(true);
					try {
						mb.flushText();
					} catch (MessagingException e) {
						getLogger().error("MessageError in FlattenVisitor",e);
					}
				}
			}

	}

	
    public MimeMultipart getMultiPart() throws MessagingException{
    	return mb.getMultipart();
    }
	@Override
	protected void setPart(MessageWalker w, MimeBodyPart p) {
		if( include_attachments ){
		try {
			mb.addBodyPart(p);
		} catch (MessagingException e) {
			getLogger().error("MessageError in FlattenVisitor",e);
		}
		}
	}
	@Override
	protected MultipartMailBuilder getMailBuilder() {
		return mb;
	}
	@Override
	public void visit(MimePart parent, String string, MessageWalker w) {
		try {
			if( parent.isMimeType("text/html")) {
				MimeBodyPart part = new MimeBodyPart();
				part.setContent(string, parent.getContentType());
				part.setDisposition(Part.INLINE);
				mb.addBodyPart(part);
				return;
			}
		} catch (MessagingException e) {
			getLogger().error("Error checking content type", e);
		}
		super.visit(parent, string, w);
	}
	

}