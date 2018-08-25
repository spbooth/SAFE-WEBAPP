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
/** A {@link MessageEditLinker} 
 * This could also be used as a general {@link MessageLinker}
 */
package uk.ac.ed.epcc.webapp.editors.mail;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;



public class Linker extends AbstractContexed implements MessageEditLinker{

	private final MessageHandler composer;
	
	public Linker(AppContext conn,  MessageHandler h){
		super(conn);
		this.composer=h;
	}
	public void addButton(ContentBuilder cb, EditAction action, List<String> path,
			String text) {
		try {
			cb.addButton(conn, text, new MailEditResult(conn,composer, path, action));
		} catch (Exception e) {
			getLogger().error("Error making MailEditResult",e);
		}
		
	}
	
	

	public void addLink(ContentBuilder builder, List<String> args,
			String file, String text) {
		
		if( file != null ){
			args = new LinkedList<String>(args);
			args.add(file);
		}
		try {
			builder.addLink(conn, text,new MailEditResult(conn,composer, args, EditAction.Serve) );
		} catch (Exception e) {
			getLogger().error("Error making link",e);
		}

	}

	public MessageProvider getMessageProvider() throws Exception {
		return composer.getMessageProvider();
	}

}