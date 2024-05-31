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
import java.io.UnsupportedEncodingException;
import java.util.*;

import jakarta.mail.Address;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;

/** Class that traverses the tree structure of a Mail message (or a MultiPart) applying a visitor class.
 * As the tree is walked it builds a path object that represents the position in the tree.
 * This can be used to identify a node in HTML forms etc.
 * 
 * This class has 2 modes. 
 * 
 * In one mode it walks the tree visiting every node. Leaf nodes are visited once 
 * but parent nodes are visited on entry and exit. Each parent node has the option of aborting further
 * exploration of its children.
 * 
 * In the second mode the class is given a target path and it traverses directly to the identified node.
 * This second mode is intended to be used to apply a modification to the target node in a Servlet after a post.
 * We use the same class for both functions to help ensure that the interpretation of the path is the same
 * in both operations.
 * 
 * There is a potential problem where the walker is used to modify the message structure as this will change the path definition.
 * This is safe in the second mode provided the {@link Visitor} returns false to abort exploration of the children.
 * If message structure is modified in the first mode then paths may not be well defined.
 * 
 * Note if you modify a nested message it has to be saved, it is not sufficient to just save the parent message.
 * 
 * @author spb
 *
 */


public class MessageWalker {
	/** parameter for the threshold after which emails are shown in line
	 * 
	 */
	private static final String MESSAGE_MAX_INLINE_CONFIG_PARAM = "message.max.inline";
	/** Path tag for Subject
	 * 
	 */
	private static final String SUBJECT_TAG = "Subject";
	/** Path tag prefix for BCC
	 * 
	 */
	private static final String BCC_TAG_PREFIX = "BCC";
	/** Path tag prefix for CC
	 * 
	 */
	private static final String CC_TAG_PREFIX = "CC";
	/** Path tag prefix for TO
	 * 
	 */
	private static final String TO_TAG_PREFIX = "TO";
	
	/** Path tag prefix for ReplyTo
	 * 
	 */
	private static final String REPLY_TAG_PREFIX = "REPLY";
	/** Path tag prefix for a Stream item
	 * 
	 */
	private static final String STREAM_TAG_S = "S";
	/** Path tag prefix for Text
	 * 
	 */
	private static final String TEXT_TAG_T = "T";
	
	/** This tag is not generated in the walk but can be used to append 
	 * additional path elements that can be stripped out.
	 * 
	 */
	private static final String EXTRA_TAG_X = "X";
	
	private LinkedList<String> args;
	private LinkedList<String> target;
	//Logger log;
	private MimeMessage current_message=null;
	private int message_level=0;
	protected AppContext conn;
	private boolean is_alternative=false;
	boolean walk_all=true;
	static int MAXLENGTH=100;
	public MessageWalker(AppContext conn){
		this.conn=conn;
		args=new LinkedList<>();
		target=null;
		//log= conn.getLogger(getClass());
	}
	/** Set the target node for the operation. Normally this makes the {@link MessageWalker} navigate directly to 
	 * the target node. If walk_all is true then all nodes will be traversed (for example to format an entire mail message
	 * with special treatment of the target node.
	 * 
	 * Do not set walk_all=true and a path if the message is going to be changed by the walker.
	 * @param src_target
	 * @param walk_all
	 * @throws WalkerException
	 */
	public void setTarget(List<String> src_target, boolean walk_all) throws WalkerException{
		if( message_level > 0){
			throw new WalkerException("Cannot set target mid-walk");
		}
		this.target = new LinkedList<>();
		if( src_target != null ){
			for( String path : src_target){
				if( path.equals(EXTRA_TAG_X)){
					// Stop processing here
					break;
				}
				target.add(path);
			}
			
		}
		this.walk_all=walk_all;
	}
	public AppContext getContext(){
		return conn;
	}
	protected Logger getLogger(){
		return Logger.getLogger(conn,getClass());
	}
	public MimeMessage getCurrentMessage(){
		return current_message;
	}
	public List<String> getPath(){
		return  new LinkedList<>(args);
	}
	public void setTarget(List<String> target){
		this.target = new LinkedList<>(target);
	}
	/** Are we in a nested message
	 * 
	 * @return boolean
	 */
	public boolean isSubMessage(){
		return message_level > 1;
	}
    protected int getMessageLevel(){
    	return message_level;
    }
	/** add an element to a path or indicate if the specified path should be recursed into.
	 * Note that when doing a targeted walk the push operation will return false for non matching paths 
	 * without adding the element so
	 * the matching pop call should always occur within the body of an if on the return code e.g.
	 * <pre>
	 * if( push("element" ){
	 *   try{
	 *        ...
	 *    }finally{
	 *       pop();
	 *     }
	 * }
	 * </pre>
	 * @param step String next part of path
	 * @return
	 */
	private boolean push(String step){
		//log.debug("push "+step);
		if( ! walk_all ){
		    if( target == null){
		    	return false;
		    }
		    // are we below target
			if( target.size()<=args.size()){
				return false;
			}
			// does common path match
			for(int i=0;i<args.size();i++){
				if( ! args.get(i).equals(target.get(i))){
					return false;
				}
			}
			// does this step match
			if( ! step.equals(target.get(args.size()))){
				return false;
			}
			// ok to recurse
		}
		//log.debug("recurse");
		args.addLast(step);
		return true;
	}
	private void pop(){
		args.removeLast();
		//log.debug("pop "+step);
	}
	/** Visit a Mail message.
	 * 
	 * This is a top level call to process a message.
	 * 
	 * @param m MimeMessage
	 * @param v Visitor
	 * @throws WalkerException 
	 */
	public final void visitMessage(MimeMessage m,Visitor v) throws WalkerException{
		visitSubMessage(null,m,v);
	}
    private final void visitSubMessage(MimePart parent,MimeMessage m,Visitor v) throws WalkerException{
    	// Be careful adding a push/pop level here
    	// we want a zero length path to match the entire message as this is how MessageServer works
    	// would need to add tagging to visitPart or wrap this
		message_level++;
		current_message=m;
		try{
			if( m == null ){
				getLogger().error("null message passed to MessageVisitor");
				return;
			}
			if( v.startMessage(parent,m, this)){
				visitMessageHeaders(m,v);
				try{
					Object content = m.getContent();
					visitPart(m,content,v);
				}catch(IOException e){
					v.doIOError(this,e);
				}catch( MessagingException e ){
					v.doMessageError(this,e);
				}
				v.endMessage(parent,m, this);
			}
		}finally{
			message_level--;
		}
	}
	private final  void visitPart(MimePart parent, Object content,Visitor v) throws WalkerException, MessagingException  {

		if( content instanceof String){
			if( push(TEXT_TAG_T)){ 
			  v.visit(parent,(String) content,this);
			  pop();
			}
		}else if( content instanceof MimeMultipart){
			MimeMultipart mp = (MimeMultipart)content;
			if(v.startMultiPart(parent,mp,this)){
				boolean old_alternative=is_alternative;
				if( ! is_alternative ){
					// alternative is sticky and stays on as we recurse down
					is_alternative = parent.isMimeType("multipart/alternative");
				}
				visitMultiPart(parent,mp,v);
				is_alternative = old_alternative;
				v.endMultiPart(parent,mp,this);
			}
		}else if( content instanceof InputStream ){
			if( push(STREAM_TAG_S)){
			   v.visitInputStream(parent, (InputStream) content,this);
			   pop();
			}
		}else if( content instanceof MimeMessage){
			visitSubMessage(parent,(MimeMessage)content,v);
		}else{
			getLogger().error("MessageVisitor: Unknown Message content "+content.getClass().getName());
		}
	}
	public final void visitMultiPart(MimePart parent, MimeMultipart mp, Visitor v) throws WalkerException{
       try{
		int count = mp.getCount();
		for(int i=0; i<count;i++){
			if( push(""+i)){
				try{
					// Potential problem here if we delete a part while doing a full walk. 
					// The API access them by number
					// so we may go off the end of the array here.
					// does not happen for a targetted operation as the push call filters to one branch
					if( v.startSubPart(parent,mp,this,i,count)){
						MimePart p = (MimePart) mp.getBodyPart(i);
						try{
						   Object o = p.getContent();
						   visitPart(p,o,v);
						}catch(UnsupportedEncodingException e){
							// this is an unsupported charset so getContent failed
							visitPart(p,p.getInputStream(),v);
						}
						v.endSubPart(parent,mp,this,i,count);
					}else{
						// check for delete in walk all. Don't do this fix for
						// a targetted operation as this will make the subsequent node match the
						// target as well
						if( walk_all && mp.getCount() == (count-1)){
							// we must have deleted the part
							// paths are no longer well defined but 
							// lets keep the loop in bounds
							i--;
							count--;
						}
					}
				}catch(IOException e){
					v.doIOError(this,e);
				}catch( MessagingException e ){
					v.doMessageError(this,e);
				}
				pop();
			}
		}
       }catch(MessagingException e){
    	   throw new WalkerException("Error getting multipart count",e);
       }
	}
@SuppressWarnings("unchecked")
private final void visitMessageHeaders(MimeMessage m,Visitor v) throws WalkerException {
		if( v.visitHeaders()) {
		try{
			Address to[] = m.getRecipients(RecipientType.TO);
			if( to != null ){
				v.doTo(to,this);

				for(int i=0; i< to.length;i++){
					if(push(TO_TAG_PREFIX+i)){
						v.doTo(to[i],i,to.length,this);
						pop();
					}
				}
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		try{
			Address cc[] = m.getRecipients(RecipientType.CC);
			if( cc != null){
				v.doCC(cc,this);


				for(int i=0; i< cc.length;i++){
					if( push(CC_TAG_PREFIX+i)){
						v.doCC(cc[i],i,cc.length,this);
						pop();
					}
				}
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		try{
			Address cc[] = m.getRecipients(RecipientType.BCC);
			if( cc != null){
				v.doBCC(cc,this);


				for(int i=0; i< cc.length;i++){
					if( push(BCC_TAG_PREFIX+i)){
						v.doBCC(cc[i],i,cc.length,this);
						pop();
					}
				}
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		
		v.doRecipients(this);
		
		try{
			String from[] = m.getHeader("From");
			if( from != null ){
				v.doFrom(from,this);
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		try{
			Address reply[] = m.getReplyTo();
			Address from[] = m.getFrom();
			// the getReplyTo method defaults to getFrom if none set.
			if( reply != null && ! Arrays.equals(reply, from)){
				v.doReplyTo(reply,this);


				for(int i=0; i< reply.length;i++){
					if( push(REPLY_TAG_PREFIX+i)){
						v.doReplyTo(reply[i],i,reply.length,this);
						pop();
					}
				}
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		v.doSenders(this);
		try{
			if( push(SUBJECT_TAG)){
				String subject = m.getSubject();
				if( subject != null ){
					v.doSubject(subject,this);
				}
			pop();
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		try{
			for(Enumeration<String> x = m.getAllHeaderLines();x.hasMoreElements();){
				v.doHeader(x.nextElement(),this);
			}
		}catch( MessagingException e ){
			v.doMessageError(this,e);
		}
		}
	}
/** Exception thrown to abort processing in the MessageWalker
 * 
 * @author spb
 *
 */
    public static class WalkerException extends Exception{

		public WalkerException() {
			super();
		}

		public WalkerException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}

		public WalkerException(String arg0) {
			super(arg0);
		}

		public WalkerException(Throwable arg0) {
			super(arg0);
		}
    	
    }
public boolean isAlternative() {
	return is_alternative;
}
public boolean matchPath() {
	if( target == null){
		return false;
	}
	return target.equals(args);
}
protected  boolean showAsLink(MimePart parent, String string) {
	String disp=null;
	AppContext conn = getContext();
	try {
		disp = parent.getDisposition();
	} catch (MessagingException e) {
		getLogger().error("Error getting disposition",e);
		// default treat as unknown
	}
	if( disp != null && disp.equalsIgnoreCase(Part.ATTACHMENT)){
		return true;
	}
	try {
		if( isAlternative() && parent.isMimeType("text/html") ){
			return true;
		}
	} catch (MessagingException e) {
		getLogger().error("Error getting mime type",e);
		// default treat as text
	}
    if( string.length() > conn.getIntegerParameter(MESSAGE_MAX_INLINE_CONFIG_PARAM, ContentMessageVisitor.MAX_INLINE_LENGTH)){
    	return true;
    }
	return false;
}
/** Append a non-significant (Ignored by the walker) extra path
 * on the end of a walker path.
 * 
 * @param path
 * @param extra
 * @return combined path
 */
public static LinkedList<String> addPath(List<String> path, String ...extra){
	LinkedList<String> result = new LinkedList<>();
	if( path != null){
		result.addAll(path);
	}
	result.add(EXTRA_TAG_X);
	for(String e : extra){
		result.add(e);
	}
	return result;
}
}