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

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;

/** Generates view html for a
 * from a {@link MessageHandler}
 * 
 * @author spb
 *
 */


public class MessageHandlerFormat {
  private MessageHandler composer;
  private AppContext conn;
 
  private MessageLinker lk;
  public MessageHandlerFormat(AppContext conn,MessageHandler mc,MessageLinker linker){
	  this.conn=conn;
	  composer=mc;
	  lk = linker;
  }
  
 
  public ContentBuilder getContent(ContentBuilder sb){
		try{
		
		sb = sb.getHeading(3);
		ExtendedXMLBuilder xb= sb.getText();
		
		xb.clean(" View ");
		xb.clean(composer.getTypeName());
		xb.appendParent();
		sb = sb.addParent();
		ContentMessageVisitor v = new ContentMessageVisitor(conn,sb,lk);
		MessageWalker mw = new MessageWalker(conn);
		MessageProvider messageProvider = composer.getMessageProvider();
		mw.visitMessage(messageProvider.getMessage(),v);
		}catch(Exception e){
			conn.error(e,"Error getting view HTML");
			sb.addText(" Internal error ");
		}
		return sb;
	}
	
	
}