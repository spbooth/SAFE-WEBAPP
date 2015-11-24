// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.result;

import java.util.LinkedList;
import java.util.List;


/** Form result that displays a message from the message catalogue
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageResult.java,v 1.2 2014/09/15 14:30:21 spb Exp $")

public class MessageResult implements FormResult {
   private final String message;
   private List<String> args=null;
   public MessageResult(String mess){
	   message=mess;
   }
   public MessageResult(String mess, String ... args){
	   this(mess);
	   for(String a : args){
		   addArg(a);
	   }
   }
   public String getMessage(){
	   return message;
   }
   public void addArg(String a){
	   if( args == null ){
		   args=new LinkedList<String>();
	   }
	   args.add(a);
   }
   public Object[] getArgs(){
	   if( args == null ){
		   return new Object[0];
	   }
	   return args.toArray(new Object[args.size()]);
   }
public void accept(FormResultVisitor vis) throws Exception {
	vis.visitMessageResult(this);
	
}
}