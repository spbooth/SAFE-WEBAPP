//| Copyright - The University of Edinburgh 2013                            |
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

import java.util.Collection;

import uk.ac.ed.epcc.webapp.forms.Identified;

/** Class to turn Collection into a HTML list.
 * There is a static method to perform one-off formatting.
 * or an object instance can be wrapped round the collection to
 * create a {@link XMLGenerator}
 * 
 * A {@link ContentBuilder} will be able to add a collection directly via {@link ContentBuilder#addList(Collection)}
 * 
 * @author spb
 *
 */

public class ListGenerator<X> implements XMLGenerator {

	private final Collection<X> data;
	/**
	 * 
	 */
	public ListGenerator(Collection<X> c) {
		this.data=c;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.XMLGenerator#addContent(uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder)
	 */
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		addList(builder,data);
		return builder;
	}

	public static <X> void addList(SimpleXMLBuilder builder, Collection<X> c){
		builder.open("ul");
		for(X dat : c){
			builder.open("li");
			addData(builder, dat);
			builder.close();
		}
		builder.close();
	}
	public static <X> void addData(SimpleXMLBuilder builder, X data){
		if( data instanceof XMLGenerator){
			((XMLGenerator)data).addContent(builder);
			return;
		}
		if( data instanceof Identified){
			builder.clean(((Identified)data).getIdentifier());
			return;
		}
		builder.clean(data.toString());
	}
}