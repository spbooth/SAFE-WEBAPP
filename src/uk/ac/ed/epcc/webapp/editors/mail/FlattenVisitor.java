// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

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
@uk.ac.ed.epcc.webapp.Version("$Id: FlattenVisitor.java,v 1.4 2015/11/09 16:32:07 spb Exp $")

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
						messageWalker.getContext().error(e,"MessageError in FlattenVisitor");
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
			w.getContext().error(e,"MessageError in FlattenVisitor");
		}
		}
	}
	@Override
	protected MultipartMailBuilder getMailBuilder() {
		return mb;
	}
	

}