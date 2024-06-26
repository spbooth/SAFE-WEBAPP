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

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.preferences.Preference;


/** A {@link HtmlPrinter} that also implements {@link ContentBuilder} 
 * in terms of web based interfaces.
 * 
 * @see HtmlWriter
 * @author spb
 *
 */



public class HtmlBuilder extends HtmlPrinter implements XMLContentBuilder {
  
public static final Feature HTML_TABLE_SECTIONS_FEATURE = new Preference("html.table_sections",false,"generate thead/tbody in html tables");
public static final Feature HTML_TABLE_HEADER_SCOPE_FEATURE = new Preference("html.table_header_scope",false,"generate scope attributes for html table headers");
Boolean use_table_section=null;
Boolean use_table_scope=null;
private boolean new_tab=false; // Do  we want to open links/buttons in new tab/window
private HtmlFormPolicy policy = new HtmlFormPolicy();
protected static final class Text extends Panel {
	  Text(AbstractXMLBuilder parent){
		  super("div",parent,true,"para");
	  }
	}
   /** A {@link Panel} for in-line text.
    * This should only introduce an additional span-element if you set attributes
    * 
    * @author Stephen Booth
    *
    */
   protected static final class SpanText extends Panel {
	  SpanText(AbstractXMLBuilder parent){
		  super(null,parent,true,null);
	  }
	}
  protected static final class Heading extends Panel {
	  
	  Heading(AbstractXMLBuilder parent,int level){
		  super("h"+level,parent,true,null);
	  }
	}
  /** A nested {@link HtmlBuilder} representing an element.
   * 
   * @author Stephen Booth
   *
   */
  public static class Panel extends HtmlBuilder {
	  Map<String,String> attr = new LinkedHashMap<>();
	  String element;
	  String type;
	  boolean inline;
	  /**
	   * 
	   * @param element Name of element (an be null) 
	   * @param parent   
	   * @param inline   Should nested text be inline or block
	   * @param type    class of element
	   */
	  Panel(String element,AbstractXMLBuilder parent, boolean inline,String type){
		  super(parent);
		  this.element=element;
		  this.inline=inline;
		  if(type != null) {
			  addClass(type);
		  }
		  this.type=type;
	  }
	  @Override
	public final void addText(String text) {
		  if( inline) {
			  // Text 
			  clean(text);
		  }else {
			  super.addText(text);
		  }
		}
		@Override
		public final ExtendedXMLBuilder getText() {
			if( inline) {
				return new SpanText(this);
			}else {
				return super.getText();
			}
		}
		@Override
		protected void appendTo(AbstractXMLBuilder builder) throws UnsupportedOperationException {
			boolean add_newline=true;
			if( (element == null || element.isEmpty()) && ! attr.isEmpty()) {
				element="span";
				add_newline=false;
			}
			boolean wrap = (element != null);
			if( wrap ) {
				builder.open(element.trim());
				for(String key : attr.keySet()){
					builder.attr(key,attr.get(key));
				}
				if( ! inline) {
					builder.clean("\n");
				}
			}
			super.appendTo(builder);
			if( wrap) {
				if( ! inline) {
					builder.clean("\n");
				}
				builder.close();
				if( add_newline) {
					builder.clean("\n");
				}
			}
		}
		/** Add an attribute to the container itself.
		 * 
		 * @param key
		 * @param value
		 */
		public void addAttr(String key, String value){
			attr.put(key.trim(), value.trim());
		}
		@Override
		public String toString() {
			XMLPrinter tmp = new XMLPrinter();
			appendTo(tmp);
			return tmp.toString().trim();
		}
		@Override
		public SimpleXMLBuilder attr(String name, CharSequence s) {
			if( ! isInOpen() && ! hasContent()) {
				// this is a container attribute
				addAttr(name, s.toString());
				return this;
			}
			return super.attr(name, s);
		}
		@Override
		protected CharSequence getAttribute(String name) {
			if( ! isInOpen() && ! hasContent()) {
				// this is a container attribute
				return attr.get(name);
			}
			return super.getAttribute(name);
		}
		@Override
		public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
			return new SpanText(this);
		}
	}
public HtmlBuilder(){
	super();
	setEscapeUnicode(false);
}
  
  
  public HtmlBuilder(AbstractXMLBuilder parent) {
	super(parent);
	if( parent != null && parent instanceof XMLContentBuilder){
		XMLContentBuilder xcb=(XMLContentBuilder) parent;
		this.policy=xcb.getFormPolicy().getChild();
		if( parent instanceof HtmlBuilder) {
			HtmlBuilder htmlBuilder = (HtmlBuilder) parent;
			if( htmlBuilder.use_table_section != null ){
				setTableSections(htmlBuilder.use_table_section);
			}
		}
	}
}

  @Override
	public <C,R> void addTable(AppContext conn,Table<C,R> t,NumberFormat nf,String style) {
		TableXMLFormatter<C,R> fmt = new TableXMLFormatter<>(this, nf,style);
		if( use_table_section != null){
			// If set explicitly this takes preference.
			fmt.setTableSections(use_table_section);
		}else{
			fmt.setTableSections(HTML_TABLE_SECTIONS_FEATURE.isEnabled(conn));
		}
		if( use_table_scope != null){
			// If set explicitly this takes preference.
			fmt.setUseScope(use_table_scope);
		}else{
			fmt.setUseScope(HTML_TABLE_HEADER_SCOPE_FEATURE.isEnabled(conn));
		}
		fmt.add(t);
	}


public void paragraph(String text) {
	open("p");
	clean(text);
	close();
	
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









public void setTableSections(boolean value){
	use_table_section=Boolean.valueOf(value);
}

@Override
public final ContentBuilder getDetails(Object summary_text) {
	Panel details = new Panel("details",this,false,"details");
	if( summary_text != null ){
		details.open("summary");
		details.addObject(summary_text);
		details.close();
	}
	return details;
}
@Override
public final void closeDetails() {
	addParent();
}

/** Add a script element to HTML. 
 * This only works for html so classes that want to add script elements
 * will need to check the class of the content builder.
 * 
 * @param script
 */
public void addScript(String script){
	open("script");
	endOpen();
	append(script);
	close();
}
public void addScriptFile(String path){
	open("script");
	attr("src", path);
	endOpen();
	close();
}
public final Logger getLogger(AppContext conn){
	return Logger.getLogger(conn,getClass());
}


@Override
public boolean useNewTab() {
	return new_tab;
}
@Override
public HtmlFormPolicy getFormPolicy() {
	return policy;
}
@Override
public boolean setNewTab(boolean new_tab) {
	boolean old = this.new_tab;
	this.new_tab = new_tab;
	return old;
}

public static String strip(String input) {
	if( input == null) {
		return null;
	}
	HtmlBuilder hb=new HtmlBuilder();
	hb.clean(input);
	return hb.toString();
}

}