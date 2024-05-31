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
package uk.ac.ed.epcc.webapp.content;

import java.util.Map;

/** Interface for classes that build XML
 * This interface could either be used to build a DOM tree or perform simple text formatting.
 * It should be more straight-forward to use than DOM but it does require 
 * the document to be built in a linear fashion.
 * Elements are added in order and attributes must be added before element content.
 * 
 * @author spb
 *
 */
public interface SimpleXMLBuilder {

	/** append text to the document escaping any characters that might be interpreted as XML markup.
	 * 
	 * @param s
	 * @return reference to self.
	 */
	public abstract SimpleXMLBuilder clean(CharSequence s);
	public abstract SimpleXMLBuilder clean(char c);
	/** Append text representation of the number
	 * @param i Number to format
	 * @return reference to self
	 * 
	 */
	public abstract SimpleXMLBuilder clean(Number i);
	/** open a tag
	 * 
	 * @param tag
	 * @return reference to self
	 */
	public abstract SimpleXMLBuilder open(String tag);
	/** open a tag with attributes. attribute values are escaped
	 * 
	 * @param tag tag to open
	 * @param attr array of name,value pairs
	 * @return reference to self
	 */
	public abstract SimpleXMLBuilder open(String tag, String attr[][]);
	/** Add an attribute. It is only legal to call this after a
	 * call to open and before close or clean.
	 * 
	 * @param name String attribute name
	 * @param s CharSequence attribute value or null for no value
	 * @return reference to self
	 */
	public abstract SimpleXMLBuilder attr(String name, CharSequence s);
	
	public default  SimpleXMLBuilder attr(Map<String,String> attr){
		if( attr != null ){
			for(String key : attr.keySet()){
				attr(key,attr.get(key));
			}
		}
		return this;
	}
	/** close the last opened tag.
	 * @return reference to self
	 */
	public abstract SimpleXMLBuilder close();

	/** Get a new SimpleXMLBuilder which is a child of this object.
	 * This is to build un-commited content that is appended to this
	 * object using the {@link #appendParent} method.
	 * 
	 * The calling code need only hold a reference to the current builder as the
	 * {@link #appendParent} method returns the parent.
	 * @return SimpleXMLBuilder
	 * @throws UnsupportedOperationException 
	 */
	public SimpleXMLBuilder getNested() throws UnsupportedOperationException;
	/** Append the contents of this builder to the parent (if any).
	 * 
	 * 
	 * @return the parent
	 * @throws UnsupportedOperationException 
	 */
	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException;
	/** Get the parent object.
	 * 
	 * @return parent
	 */
	public SimpleXMLBuilder getParent();

	/** Should high code-point unicode characters be escaped to
	 * avoid charset problems.
	 * This is advisory only the implementation is free to 
	 * ignore this call and return false in all cases.
	 * 
	 * @param escape_unicode
	 * @return previous setting.
	 */
	public boolean setEscapeUnicode(boolean escape_unicode);
	
	/** Add an object depending on its type. This should always map to {@link #appendParent()}
	 * on child objects.
	 * 
	 * @param target
	 */
	public default <X> void addObject(X target) {
		if( target instanceof XMLGenerator) {
			((XMLGenerator)target).addContent(this);
		}else {
			clean(target.toString());
		}
	}
	
	/** Convenience method to add elements of an array each element surrounded
	 * by a tag.
	 * 
	 * If the builder is actually building a non XML representation that 
	 * can represent arrays this may be used instead
	 * 
	 * @param tag
	 * @param data
	 */
	public default  void addArray(String tag, String data[]) {
		for(String s : data ) {
			open(tag);
			clean(s);
			close();
		}
	}
	/** Convenience method to add elements of an {@link Iterable} each element surrounded
	 * by a tag.
	 * 
	 * If the builder is actually building a non XML representation that 
	 * can represent arrays this may be used instead
	 * 
	 * @param tag
	 * @param data
	 */
	public default void addArray(String tag, Iterable<String> data) {
		for(String s : data ) {
			open(tag);
			clean(s);
			close();
		}
	}
}