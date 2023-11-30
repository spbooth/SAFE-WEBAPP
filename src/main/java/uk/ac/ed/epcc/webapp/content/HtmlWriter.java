//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.content;

import java.io.Writer;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder.*;
import uk.ac.ed.epcc.webapp.logging.Logger;

/** A streaming version of the {@link XMLContentBuilder}
 * @see HtmlBuilder
 * @author Stephen Booth
 *
 */
public class HtmlWriter extends XMLWriter implements Contexed ,XMLContentBuilder {

	private final AppContext conn;
	private final HtmlFormPolicy policy= new HtmlFormPolicy();
	private boolean new_tab=false;
	/**
	 * @param w
	 */
	public HtmlWriter(AppContext c,Writer w) {
		super(w);
		this.conn=c;
	}
	@Override
	public ExtendedXMLBuilder getText() {
		return new Text(this);
	}
	@Override
	public ExtendedXMLBuilder getSpan() {
		return new SpanText(this);
	}

	@Override
	public ContentBuilder getHeading(int level) {
		return new Heading(this,level);
	}


	@Override
	public ContentBuilder getPanel(String ... type)
			throws UnsupportedOperationException {
		Panel panel = new Panel("div",this,false,null);
		for(String class_name : type){
			panel.addClass(class_name);
		}
		return panel;
	}

	public ContentBuilder getPanel(String type)
			throws UnsupportedOperationException {
		
		return new Panel("div",this,false,type);
	}
	@Override
	public ContentBuilder addParent() throws UnsupportedOperationException {
		return (ContentBuilder) appendParent();
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLContentBuilder#getLogger(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public Logger getLogger(AppContext conn) {
		return Logger.getLogger(conn,getClass());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLContentBuilder#useNewTab()
	 */
	@Override
	public boolean useNewTab() {
		return new_tab;
	}
	public boolean setNewTab(boolean new_tab) {
		boolean old = this.new_tab;
		this.new_tab = new_tab;
		return old;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLContentBuilder#getFormPolicy()
	 */
	@Override
	public HtmlFormPolicy getFormPolicy() {

		return policy;
	}

}
