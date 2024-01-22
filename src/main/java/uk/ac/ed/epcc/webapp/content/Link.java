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
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Wrapper object allowing us to add links as {@link Table} content
 * 
 * @author spb
 *
 */


public class Link extends FormResultWrapper implements XMLGenerator,UIGenerator {

	
	private boolean new_window=false;
	public Link(AppContext conn,String text,FormResult result){
		super(conn,text,null,result);
	}
	public Link(AppContext conn,String text,String help,FormResult result){
		super(conn,text,help,result);
	}
	

	
	public ContentBuilder addContent(ContentBuilder builder) {
		
		if(builder instanceof XMLContentBuilder && new_window) {
			XMLContentBuilder xb = (XMLContentBuilder)builder;
			boolean prev = xb.setNewTab(true);
			xb.addLink(conn, text, help,result);
			xb.setNewTab(prev);
		}else {
			builder.addLink(conn, text, help,result);
		}
		return builder;
	}
	public boolean isNewWindow() {
		return new_window;
	}
	public void setNewWindow(boolean new_window) {
		this.new_window = new_window;
	}
	@Override
	public boolean equals(Object arg0) {
		if( arg0 instanceof Link){
			return ((Link)arg0).text.equals(text);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return text.hashCode();
	}
}