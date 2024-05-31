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


/** Form result that displays a message from the message catalogue.
 * 
 * Messages that could be presented as a pop-up rather than a new page should use {@link WarningMessageResult}.
 * 
 * @author spb
 * @see WarningMessageResult
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
		   args=new LinkedList<>();
	   }
	   args.add(a);
   }
   public Object[] getArgs(){
	   if( args == null ){
		   return new Object[0];
	   }
	   return args.toArray(new Object[args.size()]);
   }
@Override
public void accept(FormResultVisitor vis) throws Exception {
	vis.visitMessageResult(this);
	
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((args == null) ? 0 : args.hashCode());
	result = prime * result + ((message == null) ? 0 : message.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	MessageResult other = (MessageResult) obj;
	if (args == null) {
		if (other.args != null)
			return false;
	} else if (!args.equals(other.args))
		return false;
	if (message == null) {
		if (other.message != null)
			return false;
	} else if (!message.equals(other.message))
		return false;
	return true;
}
@Override
public String toString() {
	return "MessageResult [message=" + message + ", args=" + args + "]";
}
}