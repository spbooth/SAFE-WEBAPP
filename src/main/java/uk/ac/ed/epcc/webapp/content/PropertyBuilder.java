//| Copyright - The University of Edinburgh 2020                            |
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

import java.util.LinkedList;
import java.util.Properties;

/** A {@link SimpleXMLBuilder} that maps the document onto a set of Properties
 * @author Stephen Booth
 *
 */
public class PropertyBuilder implements SimpleXMLBuilder {
	private Properties prop;
	private PropertyBuilder parent;
	LinkedList<String> path = new LinkedList<>();
	StringBuilder content=new StringBuilder();
	public PropertyBuilder(Properties prop) {
		this.prop=prop;
		this.parent=null;
	}
    public PropertyBuilder(PropertyBuilder parent) {
    	this(new Properties());
    	this.parent=parent;
    	path.addAll(parent.path);
    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.CharSequence)
	 */
	@Override
	public SimpleXMLBuilder clean(CharSequence s) {
		content.append(s);
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(char)
	 */
	@Override
	public SimpleXMLBuilder clean(char c) {
		content.append(c);
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.Number)
	 */
	@Override
	public SimpleXMLBuilder clean(Number i) {
		clean(i.toString());
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String)
	 */
	@Override
	public SimpleXMLBuilder open(String tag) {
		path.add(tag);
		content.setLength(0);
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String, java.lang.String[][])
	 */
	@Override
	public SimpleXMLBuilder open(String tag, String[][] a) {
		open(tag);
		for(int i=0; i< a.length ; i++){
			attr(a[i][0],a[i][1]);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#attr(java.lang.String, java.lang.CharSequence)
	 */
	@Override
	public SimpleXMLBuilder attr(String name, CharSequence s) {
		open("@"+name);
		clean(s);
		close();
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#close()
	 */
	@Override
	public SimpleXMLBuilder close() {
		prop.setProperty(String.join(".", path),content.toString());
		path.removeLast();
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getNested()
	 */
	@Override
	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		
		return new PropertyBuilder(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#appendParent()
	 */
	@Override
	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
		if( parent != null) {
			parent.prop.putAll(prop);
		}
		return parent;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getParent()
	 */
	@Override
	public SimpleXMLBuilder getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#setEscapeUnicode(boolean)
	 */
	@Override
	public boolean setEscapeUnicode(boolean escape_unicode) {
		return false;
	}

}
