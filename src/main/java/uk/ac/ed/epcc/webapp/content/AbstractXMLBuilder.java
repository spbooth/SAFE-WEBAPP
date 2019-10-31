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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** Base class for creating SimpleXMLBuilder implementations.
 * This class tracks the sax type elements of the SimpleXMLBuilder to 
 * accumulate the attributes.
 * 
 * @author spb
 *
 */
public abstract class AbstractXMLBuilder implements ExtendedXMLBuilder {
	private Stack<String> tags;
	private boolean in_open = false;
    private final Map<String,CharSequence> attributes;
    private boolean escape_unicode =true;
    private boolean valid_xml=false;
    private char high_surrogate=0;
	public AbstractXMLBuilder() {
		tags = new Stack<>();
		attributes = new LinkedHashMap<>();
	}

	
	public final SimpleXMLBuilder clean(char c) {
		endOpen();
		doClean(c);
		return this;
	}

	public final SimpleXMLBuilder clean(CharSequence s) {
		endOpen();
		doClean(s);
		return this;
	}

	public SimpleXMLBuilder clean(Number i) {
		return clean(i.toString());
	}

	public final SimpleXMLBuilder close() {
		if (tags.isEmpty()) {
			throw new ConsistencyError("no matching open tag");
		}
		if( in_open){
			openElement(tags.pop(), attributes, true);
			attributes.clear();
			in_open = false;
		}else{
			closeElement(tags.pop());
		}
		return this;
	}

	/** action called for open element
	 * 
	 * @param tag element name
	 * @param attr map of attributes or null
	 * @param empty is this an  empty element (closeElement will not be called).
	 */
	public final void openElement(String tag,Map<String,CharSequence> attr,boolean empty) {
		  append('<');
		  append(tag);
		  if( attr != null ){
			  for(String key : attr.keySet()){
				  CharSequence s = attr.get(key);
				  append(' ');
				  append(key);
				  if( s != null ){
					  append("='");
					  doClean(s);
					  append("'");
				  }else{
					  if( valid_xml ){
						  append("='");
						  doClean(key);
						  append("'");
					  }
				  }
			  }
		  }
		  if( empty ){
			  append('/');
		  }
		  append('>');
	}	
	
	/** action called when element is closed.
	 * 
	 * @param tag
	 */
	protected void closeElement(String tag) {
		  append("</");
		  append(tag);
		  append('>');
	  }
	

	protected final void doClean(char c) {
		char prev=high_surrogate;
		high_surrogate=0;
		// We use number values for quotes as these are defined in all versions of
		// html. &apos; and &quot; are valid XML and HTML4
  		switch(c){
  		case '<': append("&lt;");
  		      break;
  		case '>': append("&gt;");
  		      break;
  		case '&': append("&amp;");
  		      break;
  		case '"': append("&#34;");
  			  break;
  		case '\'': append("&#39;");
		  break;
  		default: 
  			if( c > 127 && escape_unicode){
  				if( Character.isHighSurrogate(c)) {
  					high_surrogate=c;
  				}else if( prev != 0 && Character.isLowSurrogate(c)) {
  					// we have a pair
  					// always escape chars outside 
  					append("&#");
  	  				append(Integer.toString(Character.toCodePoint(prev, c)));
  	  				append(";");
  				}else {
  					char data[] = { c};
  					append("&#");
  					append(Integer.toString(Character.codePointAt(data, 0)));
  					append(";");
  				}
  			}else{
  				append(c);
  			}
  		}
}
	
	/** append un-cleaned content.
	 * 
	 * @param s
	 */
	protected abstract void append(CharSequence s);

	/** append un-cleaned content.
	 * 
	 * @param s
	 */
	protected abstract void append(char s);

	protected final void doClean(CharSequence s) {
		if (s == null) {
			return;
		}
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			doClean(c);
		}
	}

	@SuppressWarnings("unchecked")
	protected Stack<String> getTags(){
		return (Stack<String>) tags.clone();
	}
	/** Indicates that all attributes have been seen.
	 * 
	 */
	protected final void endOpen() {
		if (in_open) {
			openElement(tags.peek(), attributes, false);
			attributes.clear();
			in_open = false;
		}
	}

	protected final boolean isInOpen() {
		return in_open;
	}

	/** Are all tags matched in the current context.
	 * 
	 * @return are all tags matched
	 */
	public final boolean matched() {
		return tags.empty();
	}

	public final SimpleXMLBuilder open(String tag) {
		endOpen();
		tag = tag.trim();
		assert(! tag.contains(" ") );
		tags.push(tag);
		in_open = true;
		return this;
	}

	public final SimpleXMLBuilder open(String tag, String attr[][]) {
		open(tag);
		for (int i = 0; i < attr.length; i++) {
			String a[] = attr[i];
			if (a.length == 1) {
				if (a[0] != null && a[0].length() > 0) {
					attr(a[0], null);
				}
			}
			if (a.length == 2) {
				attr(a[0], a[1]);
			}
		}
		return this;
	}

	public final SimpleXMLBuilder attr(Map<String,String> attr){
		if( attr != null ){
			for(String key : attr.keySet()){
				attr(key,attr.get(key));
			}
		}
		return this;
	}

	protected void badAttribute(String name) {
		//throw new ConsistencyError("Attribute "+name+" added after element content");
	}
	public  SimpleXMLBuilder attr(String name, CharSequence s) {
		if( ! isInOpen()) {
			badAttribute(name);
		}
		attributes.put(name, s);
		return this;
	}
	protected CharSequence getAttribute(String name) {
		return attributes.get(name);
	}
	
	/** reset back to starting state
	 * 
	 */
	public void clear(){
		tags.clear();
		attributes.clear();
		in_open=false;
	}


	public boolean setEscapeUnicode(boolean escape_unicode) {
		boolean old = this.escape_unicode;
		this.escape_unicode = escape_unicode;
		return old;
	}
	public boolean getEscapeUnicode(){
		return escape_unicode;
	}

	public boolean setValidXML(boolean value){
		boolean old = this.valid_xml;
		this.valid_xml=value;
		return old;
	}
	public boolean getValidXML(){
		return valid_xml;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#addClass(java.lang.CharSequence)
	 */
	public SimpleXMLBuilder addClass(CharSequence s) {
		if( s != null && s.length() > 0 ) {
			CharSequence prev = getAttribute("class");
			if( prev == null) {
				prev=s;
			}else {
				StringBuilder sb = new StringBuilder(prev);
				sb.append(' ');
				sb.append(s);
				prev=sb.toString();
			}
			return attr("class", prev);
		}
		return this;
	}

	public final void nbs() {
		endOpen();
		if( getValidXML()){
			// &nbs; is an XML entity reference and only valid if defined.
			// we substitute the unicode equivalent for valid XML
			append("&#160;");
		}else{
			append("&nbsp;");
		}
	}

	/** Convert a string into a html fragment with matching linebreaks
	 * also comparing the line lengths with a maximum value
	 * @param max
	 * @param s
	 * @return true if lines are long
	 */
	public final boolean longLines(int max, String s) {
		boolean is_long=false;
		open("p");
		addClass("longlines");
		clean("\n");
		for(String line : s.split("\n")){
			String tmp = line.replace("\t", "        ");
			if( tmp.length() > max){
				is_long = true;
			}
			clean(line);
			br();
		}
		close();
		clean("\n");
		return is_long;
	}

	public final boolean cleanFormatted(int max, String s) {
		HtmlPrinter hb = new HtmlPrinter();
		if( hb.longLines(max,s)){
			append(hb);
			return true;
		}else{
			//open("p");
			//addClass("preformatted");
			open("pre");
			addClass("loose");
			clean(s);
			close();
			return false;
		}
	}

	public void br() {
		endOpen();
		append("<br/>\n");
		
	}
	public final void append(XMLPrinter hb) {
		if( hb.getParent() == this) {
			hb.appendParent();
			return;
		}
		if( ! hb.matched()){
			throw new ConsistencyError("unclosed tag "+hb.getTags().peek());
		}
		endOpen();
		append(hb.toString());
	}
}