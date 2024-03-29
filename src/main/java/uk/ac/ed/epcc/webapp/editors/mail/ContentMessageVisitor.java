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
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLContentBuilder;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** visitor to translate a MimeMessage into a HTML fragment.
 * 
 * @author spb
 *
 */


public class ContentMessageVisitor extends AbstractVisitor {
	
	  static final int MAX_INLINE_LENGTH = 262144;
	protected ContentBuilder sb;
	  protected  MessageLinker linker;
  public ContentMessageVisitor(AppContext conn,ContentBuilder buff,  MessageLinker linker){
	  super(conn);
	  this.sb=buff;
	  this.linker=linker;
  }
	/** Output a link to the current part this item to the {@link ContentBuilder}
	 * @param args path for current part
	 * 
	 * @param file filename to use in link
	 * @param text Text for the link
	 */
	public final void addLink( List<String> args, String file, String text){
		linker.addLink(sb,  args, file, text);
	}
    @Override
	public void visit(MimePart parent, String string,MessageWalker w) {
		try {
			if(linker !=null && w.showAsLink(parent,string)){
				linkPart(w,parent);
				return;
			}
		} catch (MessagingException e) {
			getLogger().error("Error showing part as link",e);
			// just default to standard text
		}
		try {
			if( parent.isMimeType("text/html")){
				HtmlStripper stripper = new HtmlStripper(sb,w.getContext());
				stripper.clean(string);
				return;
			}
		} catch (Exception e) {
			getLogger().error("Error checking for html",e);

		}	
		sb.cleanFormatted(getContext().getIntegerParameter(HtmlStripper.EMAIL_FORMAT_WRAP_THRESHOLD_CFG,MessageWalker.MAXLENGTH), string);
		
	}

	public final void linkPart(MessageWalker w,MimePart parent) throws MessagingException {
		String filename = parent.getFileName();
		
		String desc = parent.getDescription();
		if( filename != null ){
			Pattern p = Pattern.compile("([^\\s/\\\\:]+)\\s*\\z");
			Matcher m = p.matcher(filename);
			if( m.find()){
				filename=m.group(1);
			}else{
				filename=null;
			}
			
		}
		if( filename == null ){
			if( w.isAlternative() ){
				filename="alternative";
			}else{
			    filename="noname";
			}
		}
		if( desc == null ){
			desc = "attachment ("+parent.getContentType()+")";
		}
		if( parent.isMimeType("message/rfc822")){
			// Don't add name to an email attachment 
			// because there is a  valid path under this point in the tree
			// that does not contain the name
			filename = null;
		}
		// link to html alternatives
		sb=sb.getPanel("link");
		if( filename == null ) {
			addLink(w.getPath(),  null, desc);
		}else {
			addLink(w.getPath(),  filename, filename+": "+desc);
		}
		sb=sb.addParent();
	}

	@Override
	public final void visitInputStream(MimePart parent, InputStream stream,MessageWalker w) throws MessageWalker.WalkerException {
	boolean old =false;  // set new tab if we can
	if( sb instanceof XMLContentBuilder) {
		XMLContentBuilder x = (XMLContentBuilder) sb;
		old = x.setNewTab(true);
	}
	try {
		linkPart(w,parent);
	} catch (MessagingException e) {
		doMessageError(w,e);
	}finally {
		if( sb instanceof XMLContentBuilder) {
			XMLContentBuilder x = (XMLContentBuilder) sb;
			x.setNewTab(old);
		}
	}
}
  
	
	
	
	
	@Override
	public void doFrom(String[] from,MessageWalker w) {
		formatList("From" ,from);
	}

	
	@Override
	public void doCC(Address recipients, int i, int len,MessageWalker w) throws WalkerException {
		doHeader("CC",recipients.toString());
	}
	
	@Override
	public void doReplyTo(Address address, int i, int length, MessageWalker messageWalker) throws WalkerException {
		doHeader("ReplyTo",address.toString());
	}
	@Override
	public void doSubject(String subject,MessageWalker w) {
		formatList("Subject",new String[] {subject});
	}
	protected final void doHeaderList(String header[]) {
		for(int i=0;i<header.length;i++){
		  sb.addHeading(4, header[i]);
		}
	}

	
	@Override
	public void doTo(Address[] recipients,MessageWalker w) {
		formatList("To", recipients);
	}
	 protected final void formatList(String name, Object recip[]) {
		 if( recip != null && recip.length > 0){
			 sb=sb.getHeading(4);
	    	ExtendedXMLBuilder h = sb.getText();
	    	h.clean(name);
	    	h.clean(": ");
	    	
	    	if( recip.length > 0){
	    		h.clean(recip[0].toString());
	    		for(int i=1;i<recip.length; i++){
	    			h.clean(", ");
	    			h.clean(recip[i].toString());
	    		}
	    	}
	    	h.appendParent();
	    	sb=sb.addParent();
		 }
	    }
	 protected final void doHeader(String name,String value){
		 sb.addHeading(4, name+": "+value);
	 }

	
	@Override
	public void doIOError(MessageWalker w,IOException e) {
		// IO errors ususally unsupported text format or currupt text.
		// for HTML formatting just trap the error and go on.
		w.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Error parsing message "+e.getMessage());
		ExtendedXMLBuilder text = sb.getText();
		
		text.attr("class", "warn");
		text.clean("Corrupt Email");
		text.appendParent();
	}
	
	@Override
	public void doMessageError(MessageWalker w,MessagingException e) {
		// Messaging error implies a corrupt message part which is more likely to be our fault
		// for HTML formatting just trap the error and go on.
		w.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Error parsing message "+e.getMessage());
		ExtendedXMLBuilder text = sb.getText();
		
		text.attr("class", "warn");
		text.clean("Corrupt Email");
		text.appendParent();
	}

	
	

	
	@Override
	public void endMessage(MimePart parent, MimeMessage m, MessageWalker messageWalker)
			throws WalkerException {
		if( ! messageWalker.isSubMessage()){
			  sb=sb.addParent();
	    }
	}

	
	@Override
	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		sb=sb.addParent();
	}

	


	
	@Override
	public boolean startMessage(MimePart parent,MimeMessage m, MessageWalker messageWalker)
			throws WalkerException {
        if( ! messageWalker.isSubMessage()){
        	sb=sb.getPanel("message");
        }
		return true;
	}

	
	@Override
	public boolean startMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		sb=sb.getPanel("multipart");
		return true;
	}

	
}