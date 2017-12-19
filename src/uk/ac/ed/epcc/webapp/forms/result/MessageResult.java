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
package uk.ac.ed.epcc.webapp.forms.result;

import java.util.LinkedList;
import java.util.List;


/** Form result that displays a message from the message catalogue
 * 
 * @author spb
 *
 */


public class MessageResult implements FormResult {
   private final String message;
   private List<Object> args=null;
   public MessageResult(String mess){
	   message=mess;
   }
   public MessageResult(String mess, Object ... args){
	   this(mess);
	   for(Object a : args){
		   addArg(a);
	   }
   }
   public String getMessage(){
	   return message;
   }
   public void addArg(Object a){
	   if( args == null ){
		   args=new LinkedList<Object>();
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