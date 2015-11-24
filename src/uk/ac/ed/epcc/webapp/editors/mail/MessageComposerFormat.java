// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;

/** MessageComposerFormat generates the html edit forms
 * from a MessageComposer
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageComposerFormat.java,v 1.9 2015/10/15 11:34:34 spb Exp $")

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
		EditMessageVisitor v = new EditMessageVisitor(conn,sb,lk);
		MessageWalker mw = new MessageWalker(conn);
		MessageProvider messageProvider = composer.getMessageProvider();
		v.editRecipients(messageProvider.editRecipients());
		v.allowNewAttachments(messageProvider.allowNewAttachments());
		mw.visitMessage(messageProvider.getMessage(),v);
		}catch(Exception e){
			conn.error(e,"Error getting Edit HTML");
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
			conn.error(e,"Error getting EditPart HTML");
			sb.clean(" Internal error ");
		}
		return sb.toString();
	}
}