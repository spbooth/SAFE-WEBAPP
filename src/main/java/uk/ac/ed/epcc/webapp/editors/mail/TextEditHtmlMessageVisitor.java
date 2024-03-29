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

import jakarta.mail.internet.MimePart;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;

/** Formats an mail message as HTML with a designated text part converted to an edit box.
 * 
 * @author spb
 *
 */


public class TextEditHtmlMessageVisitor extends ContentMessageVisitor {
	public TextEditHtmlMessageVisitor(AppContext conn, HtmlBuilder buff,  MessageLinker link) {
		super(conn, buff, link);
	}
	@Override
	public void doSubject(String subject, MessageWalker w) {
		if( w.matchPath()){
			ExtendedXMLBuilder f = sb.getText();
			//f.open("form");
			f.attr("method","post");
			f.open("input");
			f.attr("type", "text");
			f.attr("name", "text");
			f.attr("size",Integer.toString(getContext().getIntegerParameter("compose.max_subject", 64)));
			if( subject != null ){
				f.attr("value",subject);
			}
			f.close();

		
			f.open("input");
			f.attr("type","submit");
			f.attr("name","action");
			f.attr("value",EditAction.Update.toString());
			f.close();

			//f.close();
			f.appendParent();
		}else{
			super.doSubject(subject, w);
		}
	}

	
    
	@Override
	public void visit(MimePart parent, String string,MessageWalker w) {
	
		if( w.matchPath()){
			ExtendedXMLBuilder f = sb.getText();
			//f.open("form");
			f.attr("method","post");
			f.open("textarea");
			f.attr("cols",Integer.toString(getContext().getIntegerParameter("compose.cols", 80)));
			f.attr("rows",Integer.toString(getContext().getIntegerParameter("compose.rows", 24)));
			f.attr("name","text");
			
			// We are going to wrap this after the edit so might as well present the initial text this way as well
			f.clean(wrapForEdit(string));
			f.close();

			f.open("input");
			f.attr("type","submit");
			f.attr("name", "action");
			f.attr("value", EditAction.Update.toString());
			f.close();
			//f.close();
			f.appendParent();
		}else{
		   super.visit(parent, string,w);
		}
	}
}