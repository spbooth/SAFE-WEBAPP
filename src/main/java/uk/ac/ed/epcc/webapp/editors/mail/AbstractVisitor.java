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

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
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


public class AbstractVisitor extends AbstractContexed implements Visitor {

	/** Pattern for line breaks. Note this requires Java8
	 * remove trailing normal spaces. Can't use \\s as this would match repeated newlines
	 */
	private static final String LINE_BREAK_PATTERN = "\\u0020*\\R";
	/**
	 * 
	 */
	public static final String EMAIL_EDIT_WRAP_THRESHOLD_CFG = "email.edit.wrap_threshold";

	/**
	 * @param conn
	 */
	public AbstractVisitor(AppContext conn) {
		super(conn);
	}

	private static final int MAX_TEXT_LINE = 80;
	public static final Feature EMAIL_WRAP_FEATURE = new Feature("email.edit.wrap", false, "Automatically apply wrapping when editing email text");
	
	
	@Override
	public void doCC(Address[] cc, MessageWalker messageWalker)
			throws WalkerException {

	}

	@Override
	public void doCC(Address address, int i, int length,
			MessageWalker messageWalker) throws WalkerException {

	}
	@Override
	public void doBCC(Address[] cc, MessageWalker messageWalker)
	throws WalkerException {

}

@Override
public void doBCC(Address address, int i, int length,
	MessageWalker messageWalker) throws WalkerException {

}
	@Override
	public void doFrom(String[] from, MessageWalker messageWalker)
			throws WalkerException {

	}

	@Override
	public void doHeader(String nextElement, MessageWalker messageWalker)
			throws WalkerException {
		

	}

	@Override
	public void doIOError(MessageWalker w, IOException e)
			throws WalkerException {
		throw new WalkerException(e);
	}

	@Override
	public void doMessageError(MessageWalker w, MessagingException e)
			throws WalkerException {
		throw new WalkerException(e);

	}
	@Override
	public void doRecipients(MessageWalker walker)throws MessageWalker.WalkerException{
		
	}
	@Override
	public void doSubject(String subject, MessageWalker messageWalker)
			throws WalkerException {
		
	}

	@Override
	public void doTo(Address[] to, MessageWalker messageWalker)
			throws WalkerException {
	

	}

	@Override
	public void doTo(Address address, int i, int length,
			MessageWalker messageWalker) throws WalkerException {
		
	}

	@Override
	public void endMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {
		

	}

	@Override
	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
		
	}

	@Override
	public void endSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		

	}

	@Override
	public boolean startMessage(MimePart parent, MimeMessage m,
			MessageWalker messageWalker) throws WalkerException {

		return true;
	}

	@Override
	public boolean startMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker) throws WalkerException {
	
		return true;
	}

	@Override
	public boolean startSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)
			throws WalkerException {
		return true;
	}

	@Override
	public void visit(MimePart parent, String content,
			MessageWalker messageWalker) throws WalkerException {
	

	}

	@Override
	public void visitInputStream(MimePart parent, InputStream content,
			MessageWalker messageWalker) throws WalkerException {
		

	}
	protected String wrap(String text){
		int wrap_thresh = getContext().getIntegerParameter(EMAIL_EDIT_WRAP_THRESHOLD_CFG, MAX_TEXT_LINE);
		StringBuilder sb = new StringBuilder();
		for( String line : text.split(LINE_BREAK_PATTERN,-1)) {
			// remove trailing whitespace
    		if( line.length() > wrap_thresh){
    			// spaces are returned as tokens
    			StringTokenizer words = new StringTokenizer(line," \t",true);
    			int count=0;
    			while(words.hasMoreElements()){
    				String word = words.nextToken();
    				
    				count += word.length();
    				if( count > wrap_thresh){
    					if( words.hasMoreElements()){
    					  // don't want trailing whitespace on a line
    					  sb.append(word.trim());
    					  sb.append("\n");
    					}
    					count=0;
    				}else {
    					sb.append(word);
    				}
    			}
    		}else{
    			sb.append(line);
    		}
    		sb.append("\n");
    	}
    	return sb.toString();
	}
	/** normalise line breaks to newline
	 * 
	 * @param text
	 * @return
	 */
	protected String clean(String text) {
		return text.replaceAll(LINE_BREAK_PATTERN, "\n");
	}
	 protected String wrapForEdit(String text){
		 if( EMAIL_WRAP_FEATURE.isEnabled(conn)){
	    	return wrap(text);
		 }else{
			 return clean(text);
		 }
	    }

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.editors.mail.Visitor#visitHeaders()
	 */
	@Override
	public boolean visitHeaders() {
		return true;
	}
}