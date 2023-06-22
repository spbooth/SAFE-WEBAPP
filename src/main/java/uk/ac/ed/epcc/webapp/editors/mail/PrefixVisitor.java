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
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;

/** message visitor that implements message depth based quoting
 * 
 * @author spb
 * @param <M> type of MailBuilder
 *
 */
public abstract class PrefixVisitor<M extends TextMailBuilder> extends AbstractVisitor {

	/**
	 * @param conn
	 */
	public PrefixVisitor(AppContext conn) {
		super(conn);
	}
	static final String base_prefix=">";
	protected String seperator="\n\n-----------------------------------------\n";
	
	protected abstract M getMailBuilder();
	
	protected void setPart(MessageWalker w, MimeBodyPart p){
		
	}


	protected String makePrefix(int depth) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<depth;i++){
			sb.append(base_prefix);
		}
		if( depth > 0){
		  sb.append(" ");
		}
		return sb.toString();
	}

	private void addRecipients(MessageWalker w, String string, Address[] recipients) {
		M mb = getMailBuilder();
		mb.addText(string);
		boolean seen = false;
		for (Address i : recipients) {
			if (seen) {
				mb.addText(", ");
			}
			mb.addText(i.toString());
			seen = true;
		}
		mb.addText("\n");
	}

	private void addHeader(String header, String[] text) {
		M mb = getMailBuilder();
		for (String s : text) {
			mb.addText(header);
			mb.addText(s);
			mb.addText("\n");
		}
	}

	@Override
	public void doCC(Address[] recipients, MessageWalker w) {
		addRecipients(w,"CC: ", recipients);
	}

	@Override
	public void doFrom(String[] from, MessageWalker w) {
		addHeader("From: ",from);
	}

	@Override
	public void doIOError(MessageWalker w, IOException e) throws WalkerException {
		// do the best we can, log and continue
		getLogger().error("IO error in PrefixVisitor",e);
		getMailBuilder().addText("[Corrupt text]\n");
	}

	@Override
	public void doMessageError(MessageWalker w, MessagingException e)
			throws WalkerException {
				// do the best we can, log and continue
				getLogger().error("MessageError in PrefixVisitor",e);
			}

	@Override
	public void doSubject(String subjects, MessageWalker w) {
		addHeader("Subject: ",new String[] {subjects});
	}

	@Override
	public void doTo(Address[] recipients, MessageWalker w) {
		addRecipients(w,"To: ", recipients);
	}
	@Override
	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		String prefix = makePrefix(messageWalker.getMessageLevel());
		M mb = getMailBuilder();
		mb.setPrefix(prefix);
		seperator=prefix+"\n"+prefix+"----------------------------------------\n"+prefix+"\n";
	    return true;
	}
	@Override
	public void endMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
			M mb = getMailBuilder();
	       // Set the separator for the enclosing  message level
		   // as the separator is logically outside the message but only gets added if further text is appended.
			String prefix = makePrefix(messageWalker.getMessageLevel()-1);
			// Set the separator for the enclosing  message level
		
		    seperator=prefix+"\n"+prefix+"----------------------------------------\n"+prefix+"\n";
			// as the separator is logically outside the message but only gets added if further text is appended.
		    // we update any pending seperator on exit
		    mb.changeSeparator(seperator);
		    mb.setPrefix(prefix);
			
	}
	
	@Override
	public void visit(MimePart parent, String string, MessageWalker w) {
		M mb = getMailBuilder();
		if( w.showAsLink(parent, string) && parent instanceof MimeBodyPart){
			setPart(w, (MimeBodyPart) parent);
		}else{
		    mb.addText(string);
		    try {
				mb.separate(seperator);
			} catch (MessagingException e) {
				getLogger().error("Error adding seperator",e);
			}
		}
	}
	@Override
	public void visitInputStream(MimePart parent, InputStream stream,
			MessageWalker w) {
		setPart(w, (MimeBodyPart) parent);
	}
}