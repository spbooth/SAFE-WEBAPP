//| Copyright - The University of Edinburgh 2016                            |
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

/** Simple wrapper to add a css style to text.
 * 
 * This can be used when objects are added to lists. via {@link ContentBuilder#addList}
 * @author spb
 *
 */
public class Span implements UIGenerator {

	public Span(String css_class,String text ) {
		super();
		this.text = text;
		this.css_class = css_class;
	}


	private final String text;
	private final String css_class;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.getSpan().open("span").attr("class", css_class).clean(text).close().appendParent();
		return builder;
	}
	public String toString(){
		return text;
	}

}
