//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.forms.result;

import java.text.MessageFormat;
import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;

/** A {@link CustomPageResult} that acts as a drop in replacement for {@link MessageResult}
 * that can then be extended to show additional content
 * @author spb
 *
 */
public class CustomMessageResult extends CustomPageResult  {
	   private final String message_type;
	   private List<String> args=null;
	   private final AppContext conn;
	   public CustomMessageResult(AppContext conn,String mess){
		   this.conn=conn;
		   message_type=mess;
	   }
	   public CustomMessageResult(AppContext conn,String mess, String ... args){
		   this(conn,mess);
		   for(String a : args){
			   addArg(a);
		   }
	   }
	   public String getMessage(){
		   return message_type;
	   }
	   public void addArg(String a){
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
	   
	
	private ResourceBundle getBundle() {
		return conn.getService(MessageBundleService.class).getBundle();
	}
    private Logger getLogger() {
    	return Logger.getLogger(conn,getClass());
    }
    
    private String message_title=null;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#getTitle()
	 */
	@Override
	public String getTitle() {
		if( message_title == null) {
			try{
				  message_title = MessageFormat.format(conn.expandText(getBundle().getString(message_type + ".title")),args);
			  }catch(MissingResourceException e){
				  	getLogger().error("missing message title for "+message_type,e);
				  	message_title="Information";
			  }
		}
		return message_title;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.result.CustomPage#addContent(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(AppContext conn, ContentBuilder cb) {
		PreDefinedContent cont = new PreDefinedContent(conn, getMessage(),getArgs());
		cont.addContent(cb);
		return cb;
	}

}
