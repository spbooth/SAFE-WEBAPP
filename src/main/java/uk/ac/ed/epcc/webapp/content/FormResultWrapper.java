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
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Wrapper object allowing us to add {@link FormResult}s as {@link Table} content
 * 
 * @author spb
 *
 */


public abstract class FormResultWrapper extends AbstractContexed implements XMLGenerator,UIGenerator {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((help == null) ? 0 : help.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormResultWrapper other = (FormResultWrapper) obj;
		if (help == null) {
			if (other.help != null)
				return false;
		} else if (!help.equals(other.help))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	protected final String text;
	protected final String help;
	protected final FormResult result;
	
	public FormResultWrapper(AppContext conn,String text,String help,FormResult result){
		super(conn);
		this.text=text;
		this.help=help;
		this.result=result;
	}
	public final SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ContentBuilder){
			addContent((ContentBuilder)builder);
		}else{
			builder.clean(text);
		}
		return builder;
	}
	public final String toString(){
		// makes text tables work sensibly
		return text;
	}
	
}