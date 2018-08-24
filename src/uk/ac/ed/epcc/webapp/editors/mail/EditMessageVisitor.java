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

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
/** Generate HTML for a message with edit buttons
 * 
 * @author spb
 *
 */


public class EditMessageVisitor extends ContentMessageVisitor {
	private boolean see_bcc=true;
	private boolean edit_recipients=false;
	private boolean allow_new_attachments=false;
	

	public void editRecipients(boolean val){
		edit_recipients=val;
	}
	public void seeBcc(boolean val) {
		see_bcc=val;
	}
	public boolean showBcc() {
		return edit_recipients || see_bcc;
	}
	public void allowNewAttachments(boolean val){
		allow_new_attachments=val;
	}
	@Override
	public void doHeader(String nextElement, MessageWalker messageWalker)
			throws WalkerException {
		// Extra debug
//		sb.append("<h4>");
//		sb.clean(nextElement);
//		sb.append("</h4>\n");
	}
	/** Class to hold the edit request type and any parameters.
	 * 
	 * @author spb
	 *
	 */
	public static class Action {
		EditAction request=null;
		String text=null;
		public Action(EditAction r, String t){
			request=r;
			text=t;
		}
		public Action(EditAction r){
		   request=r;
		}
		@Override
		public String toString(){
			if( text == null ){
				return request.toString();
			}else{
				return "["+request.toString()+":"+text+"]";
			}
		}
	}
	public EditMessageVisitor(AppContext conn,ContentBuilder buff, MessageEditLinker linker) {
		super(conn,buff, linker);
	}
	private MessageEditLinker getEditLinker(){
		return (MessageEditLinker) linker;
	}
	/** add a form button to output
	 * 
	 * @param action
	 * @param text
	 */
    private void addButton(MessageWalker w,EditAction action, String text){
    	getEditLinker().addButton(sb, action, w.getPath(),text);
    }
	@Override
	public void visit(MimePart parent,String string,MessageWalker w) {
		super.visit(parent,string,w);
		addButton(w,EditAction.Edit, "Edit");
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.hpcx.model.helpdesk.MessageVisitor#addPart(javax.mail.internet.MimePart, java.lang.Object, java.util.List)
	 */
	@Override
	public boolean startSubPart(MimePart parent, MimeMultipart mp,MessageWalker w, int id, int count) {
		sb=sb.getPanel("part");
		return true;
	}
	@Override
	public void endSubPart(MimePart parent, MimeMultipart mp,MessageWalker w, int id, int count) {
		if( count > 1 ){
			// Don't want to be able to empty a multipart
		    addButton(w,EditAction.Delete, "Delete");
		}
		sb=sb.addParent();
	}
	
	@Override
	public boolean startMultiPart(MimePart parent,MimeMultipart mp,MessageWalker w) {
		sb = sb.getPanel("multipart");
		return true;
	}
	@Override
	public void endMultiPart(MimePart parent, MimeMultipart mp,MessageWalker w){
		if( ! w.isSubMessage()){
			// only allow this at the top level to avoid ending up with a nested multipart
			// which seems to be badly supported. It also simplifies the interface
		  addButton(w,EditAction.Merge,"Merge Text");
		  addButton(w,EditAction.Flatten,"Flatten Tree");
		}
		sb=sb.addParent();
	}
	
	
	
	
	@Override
	public boolean startMessage(MimePart parent,MimeMessage message,MessageWalker w) {
		//unlike super link all messages
		sb=sb.getPanel("message");
		return true;
	}
	@Override
	public void endMessage(MimePart parent,MimeMessage m , MessageWalker w){
		if( w.isSubMessage()){
		   addButton(w,EditAction.Quote, "Quote");
		}else{
			if( allow_new_attachments){
			  addButton(w,EditAction.AddAttachment,"Add Attachment");
			}
		}
		sb=sb.addParent();
	}
	
	
	 @Override
		public void doCC(Address recipients, int i, int len,MessageWalker w) throws WalkerException {
			
			if( ! w.isSubMessage() && edit_recipients){
				sb = sb.getHeading(4);
				
				  addButton(w,EditAction.Delete, "Delete recipient");
				  ExtendedXMLBuilder text = sb.getText();
				text.clean(" CC: ");
				text.clean(recipients.toString());
				text.appendParent();
			   sb=sb.addParent();
			}else{
				super.doCC(recipients, i, len,w);
			}
		}
	 @Override
		public void doBCC(Address recipients, int i, int len,MessageWalker w) throws WalkerException {
			if( showBcc()) {
			if( ! w.isSubMessage() && edit_recipients){
				sb=sb.getHeading(4);
				addButton(w,EditAction.Delete, "Delete recipient");
				ExtendedXMLBuilder text = sb.getText();
				text.clean( "BCC: ");
				text.clean(recipients.toString());
				text.appendParent();
				sb=sb.addParent();
			}else{
				super.doBCC(recipients, i, len,w);
			}
			}
		}
	 @Override
		public void doBCC(Address[] cc, MessageWalker w)
				throws WalkerException {
		 if( showBcc()) {
			 if( ! w.isSubMessage() && ! edit_recipients){
				 formatList("BCC", cc);
			 }else{
				 super.doBCC(cc, w);
			 }
		 }
		}
		@Override
		public void doCC(Address[] cc, MessageWalker w)
				throws WalkerException {
			 if( ! w.isSubMessage() && ! edit_recipients){
				 formatList("CC", cc);
			 }else{
				super.doCC(cc, w);
			 }
		}
	@Override
	public void doTo(Address recipients, int i, int len,MessageWalker w) throws WalkerException {
		
		if( len > 1 && ! w.isSubMessage() && edit_recipients){
			sb=sb.getHeading(4);
			addButton(w,EditAction.Delete, "Delete recipient");
			ExtendedXMLBuilder text = sb.getText();
			text.clean(" To: ");
			text.clean(recipients.toString());
			text.appendParent();
			sb=sb.addParent();
		}else{
			super.doTo(recipients, i, len,w);
		}
	}
	@Override
	public void doTo(Address[] recipients, MessageWalker w) {
		if( w.isSubMessage() || recipients.length == 1 || ! edit_recipients){
		   super.doTo(recipients, w);
		}
	}
	@Override
	public void doRecipients(MessageWalker w)throws MessageWalker.WalkerException{
		if( ! w.isSubMessage() && edit_recipients){
			sb=sb.getHeading(3);
			addButton(w,EditAction.AddRecipient,"Add");
			
			sb.addText(" New Recipient");
			sb=sb.addParent();
		}
	}
	@Override
	public void doSubject(String subject, MessageWalker w) {
		
		if( ! w.isSubMessage()){
			sb=sb.getHeading(4);
			
			 addButton(w, EditAction.EditSubject, "Edit Subject");
			 ExtendedXMLBuilder text = sb.getText();
			text.clean(" Subject: ");
			if( subject != null  ){
			  text.clean(subject);
			}
			text.appendParent();
			sb=sb.addParent();
		}else{
			super.doSubject(subject, w);
		}
	}
		
	
}