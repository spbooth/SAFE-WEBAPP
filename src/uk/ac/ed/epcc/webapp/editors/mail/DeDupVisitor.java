//| Copyright - The University of Edinburgh 2014                            |
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
import java.util.HashSet;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;

/** Visitor that modifies the message being walked to remove 
 * duplicate parts/attachments
 * @author spb
 *
 */

public class DeDupVisitor extends AbstractVisitor {

	/** Objects already included in the mail message so can be skipped
	 * 
	 */
	Set seen = new HashSet();
	boolean modified=false;
	/**
	 * 
	 */
	public DeDupVisitor(AppContext conn) {
		super(conn);
	}
	@Override
	public void endMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		try {
			if( modified ){
				m.saveChanges();
			}
			markSeen(m);
		} catch (Exception e) {
			throw new WalkerException(e);
		}
	}
	
	
	@Override
	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		try {
			if( mp.getCount() == 0 ){
				parent.setText("Duplicate contents removed");
			}
		} catch (MessagingException e) {
			throw new WalkerException(e);
		}
		
	}
	
	
	
	protected Object read(InputStream stream) throws DataFault{
		ByteArrayStreamData data = new ByteArrayStreamData();
		data.read(stream);
		return data;
	}
	/**
	 * @param data
	 * @return
	 * @throws MessagingException 
	 */
	protected boolean markSeen(Object data) throws Exception {
		if( data == null ){
			return false;
		}
		if( data instanceof MimeMessage){
			
			String messageID = ((MimeMessage)data).getMessageID();
			return seen.add(messageID);
			
		}
		if( data instanceof InputStream){
			
			return markSeen(read((InputStream) data));
		}
		return seen.add(data);
	}
	/**
	 * @param data
	 * @return
	 * @throws MessagingException 
	 */
	protected boolean hasSeen(Object data) throws Exception {
		if( data instanceof MimeMessage){
			return seen.contains(((MimeMessage)data).getMessageID());
		}
		if( data instanceof InputStream){
			return hasSeen(read((InputStream)data));
		}
		return seen.contains(data);
	}
	@Override
	public void visit(MimePart parent, String content,
			MessageWalker messageWalker) throws WalkerException {
		try {
			if( parent != null && Part.ATTACHMENT.equalsIgnoreCase(parent.getDisposition())){
				markSeen(content);
			}
		} catch (Exception e) {
			throw new  WalkerException(e);
		}
	}
	@Override
	public void visitInputStream(MimePart parent, InputStream content,
			MessageWalker messageWalker) throws WalkerException {
		try {
			markSeen(read(content));
		} catch (Exception e) {
			throw new WalkerException(e);
		}
	}
	@Override
	public boolean startSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		try {
			if( hasSeen(mp.getBodyPart(i).getContent())){
				mp.removeBodyPart(i);
				modified=true;
				return false;
			}
		} catch (Exception e) {
			throw new WalkerException(e);
		}
		return true;
	}
}