// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;

/** An implementation of {@link SimpleXMLBuilder} that builds an
 * equivalent JSON document.
 * This version has no external dependencies so has to do its own
 * escaping etc. A reconfigurable factory method is provided 
 * so that alternate implementations can be included in projects
 * that use a 3rd party JSON library.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: JsonBuilder.java,v 1.9 2015/02/26 10:37:04 spb Exp $")
public class JsonBuilder   implements SimpleXMLBuilder{
	
	/** static factory method to allow implementations to be replaced.
	 * 
	 * @param conn
	 * @return
	 */
	public static SimpleXMLBuilder getJsonBuilder(AppContext conn){
		return conn.makeObjectWithDefault(SimpleXMLBuilder.class, JsonBuilder.class, "JsonBuilder");
	}
	/**
	 * 
	 */
	private static final String CLOSE_OBJECT = "}";
	/**
	 * 
	 */
	private static final String SEPERATOR = ",\n";
	/**
	 * 
	 */
	private static final String OPEN_OBJECT = "{\n";
	private StringBuilder content=new StringBuilder();
	private StringBuilder str = new StringBuilder();
	Number n;
	int depth=0; // depth levels
	List<Integer> count = new LinkedList<Integer>();
	
	
	public JsonBuilder(){
		count.add(0);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getNested()
	 */
	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#appendParent()
	 */
	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getParent()
	 */
	public SimpleXMLBuilder getParent() {
		return null;
	}

	private void addString(String s){
		content.append('"');
		
		s=s.replace("\\", "\\\\");
		s=s.replace("\"", "\\\"");
		s=s.replace("\n", "\\n");
		content.append(s);
		content.append('"');
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.CharSequence)
	 */
	public SimpleXMLBuilder clean(CharSequence s) {
		str.append(s);
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(char)
	 */
	public SimpleXMLBuilder clean(char c) {
		str.append(c);
		return this;
	}

	private boolean hasContent(){
		return str.length() > 0 || n != null;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.Number)
	 */
	public SimpleXMLBuilder clean(Number i) {
		if( hasContent() ){
			// append text
			if( n != null ){
				clean(n.toString());
				n=null;
			}
			clean(i.toString());
		}else{
			// cache number in it is full element content
			n=i;
		}
		return this;
	}
/** Add any pending content
 * 
 */
	private void doContent(){
		if( str.length() > 0 ){
			addString(str.toString());
		}else if( n != null){
			content.append(n.toString());
		}else{
			content.append("null");
		}
		str.setLength(0);
		n=null;
		assert(! hasContent());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String)
	 */
	public SimpleXMLBuilder open(String tag) {
		int pos = count.get(depth);
		if( pos == 0 ){
			content.append(OPEN_OBJECT);
		}else{
			content.append(SEPERATOR);
		}
		// If we are first element  open object first
		// otherwise add seperator first
		for(int i=0;i<depth;i++){
			content.append(' ');
		}
		addString(tag);
		content.append(": ");
		depth++;
		count.add(0);
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String, java.lang.String[][])
	 */
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
	public SimpleXMLBuilder attr(String name, CharSequence s) {
		open("@"+name);
		clean(s);
		close();
		return this;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#close()
	 */
	public SimpleXMLBuilder close() {
		//TODO complete
		// if we have children
		//          if content add special this object
		//          close object
		// otherwise add content.
		int num = count.get(depth);
		if( num > 0){
			if( hasContent() ){
				content.append(SEPERATOR);
				for(int i=0;i<depth;i++){
					content.append(' ');
				}
				addString("#content");
				content.append(": ");
				doContent();
			}
			content.append('\n');
			for(int i= 0; i< (depth-1);i++){
				content.append(' ');
			}
			content.append(CLOSE_OBJECT);
		}else{
			doContent();
		}
		count.remove(depth);
		depth--;
		if( depth >= 0 ){ 
			count.set(depth, 1+count.get(depth));
		}
		return this;
	}

	public String toString(){
		return content.toString()+"\n"+CLOSE_OBJECT;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#setEscapeUnicode(boolean)
	 */
	public boolean setEscapeUnicode(boolean escape_unicode) {
		// Not an option in JSON
		return false;
	}

	

}
