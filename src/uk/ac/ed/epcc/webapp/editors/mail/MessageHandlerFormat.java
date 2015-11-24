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

/** Generates view html for a
 * from a {@link MessageHandler}
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: MessageHandlerFormat.java,v 1.2 2015/10/15 11:34:34 spb Exp $")

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