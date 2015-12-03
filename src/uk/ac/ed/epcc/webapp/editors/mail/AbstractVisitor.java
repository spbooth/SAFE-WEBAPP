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
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.editors.mail.MessageWalker.WalkerException;
/** Implementation of the Visitor interface with default implementations of methods to
 * make it easier to produce classes that implement the interface correctly.
 * 
 * Essentially each method does nothing and all recursion is enabled
 * 
 * @author spb
 *
 */


public class AbstractVisitor implements Visitor, Contexed {

	/**
	 * 
	 */
	public static final String EMAIL_EDIT_WRAP_THRESHOLD_CFG = "email.edit.wrap_threshold";

	/**
	 * @param conn
	 */
	public AbstractVisitor(AppContext conn) {
		super();
		this.conn = conn;
	}

	private static final int MAX_TEXT_LINE = 80;
	public static final Feature EMAIL_WRAP_FEATURE = new Feature("email.edit.wrap", false, "Automatically apply wrapping when editing email text");
	
	private final AppContext conn;
	
	public void doCC(Address[] cc, MessageWalker messageWalker)
			throws WalkerException {

	}

	public void doCC(Address address, int i, int length,
			MessageWalker messageWalker) throws WalkerException {

	}
	public void doBCC(Address[] cc, MessageWalker messageWalker)
	throws WalkerException {

}

public void doBCC(Address address, int i, int length,
	MessageWalker messageWalker) throws WalkerException {

}
	public void doFrom(String[] from, MessageWalker messageWalker)
			throws WalkerException {

	}

	public void doHeader(String nextElement, MessageWalker messageWalker)
			throws WalkerException {
		

	}

	public void doIOError(MessageWalker w, IOException e)
			throws WalkerException {
		throw new WalkerException(e);
	}

	public void doMessageError(MessageWalker w, MessagingException e)
			throws WalkerException {
		throw new WalkerException(e);

	}
	public void doRecipients(MessageWalker walker)throws MessageWalker.WalkerException{
		
	}
	public void doSubject(String subject, MessageWalker messageWalker)
			throws WalkerException {
		
	}

	public void doTo(Address[] to, MessageWalker messageWalker)
			throws WalkerException {
	

	}

	public void doTo(Address address, int i, int length,
			MessageWalker messageWalker) throws WalkerException {
		
	}

	public void endMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		

	}

	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		
	}

	public void endSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		

	}

	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {

		return true;
	}

	public boolean startMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
	
		return true;
	}

	public boolean startSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		return true;
	}

	public void visit(MimePart parent, String content,
			MessageWalker messageWalker) throws WalkerException {
	

	}

	public void visitInputStream(MimePart parent, InputStream content,
			MessageWalker messageWalker) throws WalkerException {
		

	}
	protected String wrap(String text){
		int wrap_thresh = getContext().getIntegerParameter(EMAIL_EDIT_WRAP_THRESHOLD_CFG, MAX_TEXT_LINE);
		StringBuilder sb = new StringBuilder();
    	StringTokenizer st = new StringTokenizer(text,"\n",true);
    	while(st.hasMoreElements()){
    		String line=(String) st.nextElement();
    		if( line.length() > wrap_thresh){
    			StringTokenizer words = new StringTokenizer(line," \t",true);
    			int count=0;
    			while(words.hasMoreElements()){
    				String word = words.nextToken();
    				sb.append(word);
    				count += word.length();
    				if( count > wrap_thresh){
    					if( words.hasMoreElements()){
    					  sb.append("\n");
    					}
    					count=0;
    				}
    			}
    		}else{
    			sb.append(line);
    		}
    	}
    	return sb.toString();
	}
	 protected String wrapForEdit(String text){
		 if( EMAIL_WRAP_FEATURE.isEnabled(conn)){
	    	return wrap(text);
		 }else{
			 return text;
		 }
	    }

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public final AppContext getContext() {
		return conn;
	}
}