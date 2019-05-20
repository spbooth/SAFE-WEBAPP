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

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;


/** Interface for classes that visit the parts of a message
 * 
 * @author spb
 *
 */
public interface Visitor {
    /** invoked at the start of a message
     * The messageWalker message level is set for the message we are entering.
     * @param parent 
     * @param m
     * @param messageWalker
     * @return true if we are to recurse into the message
     * @throws MessageWalker.WalkerException
     */
public	boolean startMessage( MimePart parent, MimeMessage m, MessageWalker messageWalker)throws MessageWalker.WalkerException;
/** invoked as we leave a message
 * 
 * The messageWalker message level is set for the message we are leaving
 * @param parent 
 * 
 * @param m
 * @param messageWalker
 * @throws MessageWalker.WalkerException
 */
public	void endMessage(MimePart parent, MimeMessage m, MessageWalker messageWalker)throws MessageWalker.WalkerException;
/** method to handle exceptions when walking the message
 * @param w 
 * 
 * @param e
 * @throws MessageWalker.WalkerException
 */
	void doIOError(MessageWalker w,IOException e) throws MessageWalker.WalkerException;
	/** handle MessageExceptions while walking the message
	 * @param w 
	 * 
	 * @param e
	 * @throws MessageWalker.WalkerException
	 */
	void doMessageError(MessageWalker w,MessagingException e)throws MessageWalker.WalkerException;
	/** visit a String part
	 * 
	 * @param parent
	 * @param content
	 * @param messageWalker 
	 * @throws MessageWalker.WalkerException
	 */
	void visit(MimePart parent, String content, MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** visit a stream part
	 * 
	 * @param parent
	 * @param content
	 * @param messageWalker 
	 * @throws MessageWalker.WalkerException
	 */
	void visitInputStream(MimePart parent, InputStream content, MessageWalker messageWalker)throws MessageWalker.WalkerException;
    /** invoked at the start of a multipart
     * 
     * @param parent
     * @param mp
     * @param messageWalker
     * @return true to recurse into multipart
     * @throws MessageWalker.WalkerException
     */
	boolean startMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** invoked at end of a multipart
	 * 
	 * @param parent
	 * @param mp
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	public void endMultiPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** invoked at the start of each part in a multipart
	 * 
	 * @param parent
	 * @param mp
	 * @param messageWalker
	 * @param i
	 * @param count
	 * @return true to recurse into part
	 * @throws MessageWalker.WalkerException
	 */
	boolean startSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)throws MessageWalker.WalkerException;
    /** invoked at the end of each multipart
     * 
     * @param parent
     * @param mp
     * @param messageWalker
     * @param i
     * @param count
     * @throws MessageWalker.WalkerException
     */
	void endSubPart(MimePart parent, MimeMultipart mp,
			MessageWalker messageWalker, int i, int count)throws MessageWalker.WalkerException;
	/** process the To fields
	 * 
	 * @param to
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	void doTo(Address[] to, MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** process an individual To field
	 * 
	 * @param address
	 * @param i
	 * @param length
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	void doTo(Address address, int i, int length, MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** process the CC fields
	 * 
	 * @param cc
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	void doCC(Address[] cc, MessageWalker messageWalker)throws MessageWalker.WalkerException;
   /** process a single CC field
    * 
    * @param address
    * @param i
    * @param length
    * @param messageWalker
    * @throws MessageWalker.WalkerException
    */
	void doCC(Address address, int i, int length, MessageWalker messageWalker)throws MessageWalker.WalkerException;
	/** process all the BCC addresses
	 * This is called first then {@link #doBCC(Address, int, int, MessageWalker)}
	 * is called for each address.
	 * 
	 * @param cc
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	void doBCC(Address[] cc, MessageWalker messageWalker)throws MessageWalker.WalkerException;
   /** process a single BCC field.
    * These are called in turn after the call to {@link #doBCC(Address[], MessageWalker)}
    * 
    * @param address
    * @param i
    * @param length
    * @param messageWalker
    * @throws MessageWalker.WalkerException
    */
	void doBCC(Address address, int i, int length, MessageWalker messageWalker)throws MessageWalker.WalkerException;
	
	/** process all the ReplyTo addresses
	 * This is called first then {@link #doReplyTo(Address, int, int, MessageWalker)}
	 * is called for each address.
	 * 
	 * @param cc
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	default void doReplyTo(Address[] cc, MessageWalker messageWalker)throws MessageWalker.WalkerException{
		
	}
   /** process a single ReplyTo field.
    * These are called in turn after the call to {@link #doReplyTo(Address[], MessageWalker)}
    * 
    * @param address
    * @param i
    * @param length
    * @param messageWalker
    * @throws MessageWalker.WalkerException
    */
	default void doReplyTo(Address address, int i, int length, MessageWalker messageWalker)throws MessageWalker.WalkerException{
		
	}

	
	/** process the subject fields
	 * 
	 * @param subject
	 * @param messageWalker
	 * @throws MessageWalker.WalkerException
	 */
	void doSubject(String subject, MessageWalker messageWalker)throws MessageWalker.WalkerException;
     /** process the from fields
      * 
      * @param from
      * @param messageWalker
      * @throws MessageWalker.WalkerException
      */
	void doFrom(String[] from, MessageWalker messageWalker)throws MessageWalker.WalkerException;
    /** process an individual header field
     * 
     * @param nextElement
     * @param messageWalker
     * @throws MessageWalker.WalkerException
     */
	void doHeader(String nextElement, MessageWalker messageWalker)throws MessageWalker.WalkerException;
    /** called immediatly after recipient fields are processed
     * 
     * @param walker
     * @throws MessageWalker.WalkerException 
     */
	public void doRecipients(MessageWalker walker)throws MessageWalker.WalkerException;

	 /** called immediatly after sender fields are processed
     * 
     * @param walker
     * @throws MessageWalker.WalkerException 
     */
	default public void doSenders(MessageWalker walker)throws MessageWalker.WalkerException{
		
	}
	/** Does the visitor want to walker to visit the message headers
	 * 
	 * @return boolean
	 */
	public boolean visitHeaders();
}