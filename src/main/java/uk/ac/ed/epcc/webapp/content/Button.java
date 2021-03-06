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

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Wrapper object allowing us to add buttons as {@link Table} content
 * 
 * @author spb
 *
 */


public class Button extends AbstractContexed implements XMLGenerator,UIGenerator {

	private final String text;
	private final String help;
	private final FormResult result;
	public Button(AppContext conn,String text,FormResult result){
		this(conn,text,null,result);
	}
	public Button(AppContext conn,String text,String help,FormResult result){
		super(conn);
		this.text=text;
		this.help=help;
		this.result=result;
	}
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ContentBuilder){
			addContent((ContentBuilder)builder);
		}else{
			builder.clean(text);
		}
		return builder;
	}
	public String toString(){
		// makes text tables work sensibly
		return text;
	}
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.addButton(conn, text, help,result);
		return builder;
	}
}