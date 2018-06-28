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
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** MessageComposerFormat generates the html edit forms
 * from a MessageComposer
 * 
 * @author spb
 *
 */


public class MessageComposerFormat {
  private MessageComposer composer;
  private AppContext conn;
 
  private MessageEditLinker lk;
  public MessageComposerFormat(AppContext conn,MessageComposer mc,MessageEditLinker linker){
	  this.conn=conn;
	  composer=mc;
	  lk = linker;
  }
  
  public String getButton(String text,EditAction action){
	  HtmlBuilder sb = new HtmlBuilder();
	  lk.addButton(sb, action, null, text);
	  return sb.toString();
  }
	/** Get the HTML edit form
	 * 
	 * @return String HTML fragment
	 */
  public String getEditHTML() {
		HtmlBuilder sb = new HtmlBuilder();
		return getContent(sb).toString();
  }
  public ContentBuilder getContent(ContentBuilder sb){
		try{
		
		sb = sb.getHeading(3);
		ExtendedXMLBuilder xb= sb.getText();
		
		xb.clean(" Build ");
		xb.clean(composer.getTypeName());
		xb.appendParent();
		sb = sb.addParent();
		if( composer instanceof MessageComposerExtraFormat) {
			ContentBuilder top = sb.getPanel("top");
			if( top instanceof HtmlBuilder) {
				((HtmlBuilder)top).setNewTab(true);
			}
			((MessageComposerExtraFormat)composer).addTopContent(top);
			top.addParent();
		}
		EditMessageVisitor v = new EditMessageVisitor(conn,sb,lk);
		MessageWalker mw = new MessageWalker(conn);
		MessageProvider messageProvider = composer.getMessageProvider();
		v.editRecipients(messageProvider.editRecipients());
		v.allowNewAttachments(messageProvider.allowNewAttachments());
		mw.visitMessage(messageProvider.getMessage(),v);
		}catch(Exception e){
			getLogger().error("Error getting Edit HTML",e);
			sb.addText(" Internal error ");
		}
		return sb;
	}
	
	/** Get HTML for this message with a specified text part as a form
	 * @param path target path of part to edit
	 * 
	 * @return String HTML fragment
	 */

	
	public String getEditPartHTML(List<String> path) {
		HtmlBuilder sb = new HtmlBuilder();
		try{
			sb.open("h3");
		sb.clean(" Edit ");
		sb.clean(composer.getTypeName());
		sb.close();
		sb.clean("\n");
		Visitor v = new TextEditHtmlMessageVisitor(conn,sb,lk);
		MessageWalker mw = new MessageWalker(conn);
		mw.setTarget(path, true);

		mw.visitMessage(composer.getMessageProvider().getMessage(),v);
		}catch(Exception e){
			getLogger().error("Error getting EditPart HTML",e);
			sb.clean(" Internal error ");
		}
		return sb.toString();
	}
	protected Logger getLogger(){
		return conn.getService(LoggerService.class).getLogger(getClass());
	}
}