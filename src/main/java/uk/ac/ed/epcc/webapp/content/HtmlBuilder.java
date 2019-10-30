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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.action.DisabledAction;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.html.AddButtonVisitor;
import uk.ac.ed.epcc.webapp.forms.html.AddLinkVisitor;
import uk.ac.ed.epcc.webapp.forms.html.EmitHtmlInputVisitor;
import uk.ac.ed.epcc.webapp.forms.html.InputIdVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.CanSubmitVisistor;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.PrefixInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TagInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.log.Viewable;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.servlet.ServeDataServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;


/** A {@link HtmlPrinter} that also implements {@link ContentBuilder} 
 * in terms of web based interfaces.
 * 
 * @author spb
 *
 */



public class HtmlBuilder extends HtmlPrinter implements XMLContentBuilder {
  
public static final Feature HTML_USE_LABEL_FEATURE = new Preference("html.use_label",true,"generate html labels in automatic forms");
public static final Feature HTML_TABLE_SECTIONS_FEATURE = new Preference("html.table_sections",false,"generate thead/tbody in tables");
Boolean use_table_section=null;

private boolean new_tab=false; // Do  we want to open links/buttons in new tab/window
protected static final class Text extends Panel {
	  Text(HtmlBuilder parent){
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
	  SpanText(HtmlBuilder parent){
		  super(null,parent,true,null);
	  }
	}
  protected static final class Heading extends Panel {
	  
	  Heading(HtmlBuilder parent,int level){
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
	  Panel(String element,HtmlBuilder parent, boolean inline,String type){
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
	}
public HtmlBuilder(){
	super();
	setEscapeUnicode(false);
}
  
  
  public HtmlBuilder(HtmlBuilder htmlBuilder) {
	super(htmlBuilder);
	if( htmlBuilder != null ){
		setUseRequired(htmlBuilder.use_required);
		setActionName(htmlBuilder.action_name);
		if( htmlBuilder.use_table_section != null ){
			setTableSections(htmlBuilder.use_table_section);
		}
		setMissingFields(htmlBuilder.missing_fields);
		setErrors(htmlBuilder.errors);
		setPostParams(htmlBuilder.post_params);
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



private Collection<String> missing_fields=null;
private Map<String,String> errors=null;
private Map<String,Object> post_params=null;


/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(java.lang.Iterable)
 */
@Override
public void addFormTable(AppContext conn,Iterable<Field> form) {
	Iterator<Field> it = form.iterator();
	boolean even=true;
	if( it.hasNext() ){
		open("table");
		addClass( "form");
		while (it.hasNext()) {
			Field<?> f = it.next();
		    try {
		    	open("tr");
		    	if( even ){
		    		addClass( "even");
		    	}else{
		    		addClass("odd");
		    	}
		    	open("th");
				addFormLabel(conn, f);
				close();
				open("td");
				addFormInput(conn, f,null);
				close();
				close();
				clean('\n');
				even = ! even;
			} catch (Exception e) {
				getLogger(conn).error("Error making html from form",e);
			}
		}
		close();
		}
}

private boolean isMissing(String field){
	if( missing_fields == null ){
		HtmlBuilder parent = (HtmlBuilder) getParent();
		if( parent != null){
			return parent.isMissing(field);
		}else{
			return false;
		}
	}
	return missing_fields.contains(field);
}
private String getError(String field){
	if( errors == null ){
		return null;
	}
	return errors.get(field);
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.forms.Field)
 */
@Override
public <I> void addFormLabel(AppContext conn,Field<I> f) {
	String key = f.getKey();
	boolean missing = isMissing(key);
	String error = getError(key);
	Input<I> i = f.getInput();
	boolean optional = f.isOptional();
	if( HTML_USE_LABEL_FEATURE.isEnabled(conn)){
		open("label");
		InputIdVisitor vis = new InputIdVisitor(f.getForm().getFormID());
		try {
			String id =  (String) i.accept(vis);
			if( id != null){
				attr("for",id);
			}
		} catch (Exception e) {
			getLogger(conn).error("Error getting id",e);
		}
	}else{
		open("span");
	}
	if (optional) {
		addClass("optional");
	}else{
		addClass("required");
	}
	if( missing) {
		addClass("missing");
	}
	String tooltip = f.getTooltip();
	if( tooltip != null && ! tooltip.isEmpty()) {
		attr("title",tooltip);
	}
	
	clean(f.getLabel());
	
	close(); // span or label

//	if (missing) {
//		open("b");
//		open("span");
//		addClass( "warn");
//		clean("*");
//		close(); //span
//		close(); //b
//	}
	if (error != null) {
		nbs();
		open("span");
		addClass( "field_error");
		clean(error);
		close(); //span
	}
	
}


/** should we do browser required field validation
 * 
 */
private boolean use_required=true;
public boolean setUseRequired(boolean use_required){
	boolean old_value = use_required;
	this.use_required=use_required;
	return old_value;
}

/** Should locked inputs have their vvalues posed as hidden parameters
 * 
 */
private boolean locked_as_hidden=false;
public boolean setLockedAsHidden(boolean value) {
	boolean old = locked_as_hidden;
	this.locked_as_hidden=value;
	return old;
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.forms.Field)
 */
@Override
public <I,T> void addFormInput(AppContext conn,Field<I> f,T item) {
	String key =  f.getKey();
	String error = null;
	boolean optional = f.isOptional();
	// If we have errors to report then we are showing the post_params.
	// if we want to force errors to be shown from the Form state (say
	// updating an old object with
	// invalid state then pass null post_params or set validate
	boolean use_post = (post_params != null)
			&& ((errors != null && errors.size() > 0) || (missing_fields != null && missing_fields
					.size() > 0));
	if (errors != null) {
		error = errors.get(key);
	}
	Input<I> i = f.getInput();
	if( i instanceof PrefixInput){
		clean(((PrefixInput)i).getPrefix());
	}
	try{
		EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(conn,optional,this, use_post, post_params,f.getForm().getFormID(),f.getAttributes());
		vis.setRadioTarget(item);
		vis.setUseRequired(use_required);
		vis.setAutoFocus(f.getKey().equals(f.getForm().getAutoFocus()));
		vis.setLockedAsHidden(locked_as_hidden);
		i.accept(vis);
	}catch(Exception e){
		getLogger(conn).error("Error generating input",e);
	}
	if( i instanceof TagInput){
		clean(((TagInput)i).getTag());
	}
	
}



public Collection<String> getMissingFields() {
	return missing_fields;
}


public void setMissingFields(Collection<String> missing_fields) {
	this.missing_fields = missing_fields;
}


public Map<String,String> getErrors() {
	return errors;
}


public void setErrors(Map<String,String> errors) {
	this.errors = errors;
}


public Map<String,Object> getPostParams() {
	return post_params;
}


public void setPostParams(Map<String,Object> post_params) {
	this.post_params = post_params;
}

private String action_name=null;

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
 */
@Override
public void addActionButtons(Form f,String legend,Set<String> actions) {
	boolean can_submit=CanSubmitVisistor.canSubmit(f);

	if( ! actions.isEmpty()){
		open("fieldset");
		addClass( "action_buttons");
		if( legend != null && ! legend.isEmpty()) {
			open("legend");
			clean(legend);
			close();
		}
		for( String name : actions) {
			FormAction action = f.getAction(name);
			Object content = action.getText();
			
			if( content != null ){
				open("button");
			}else{
				open("input");
			}
			addClass("input_button");
			attr("type","submit");
			boolean must_validate = action.getMustValidate();
			if( ! must_validate){
				attr("formnovalidate",null);
			}
			if( action.wantNewWindow()) {
				attr("formtarget","_blank");
			}
			
			String help = action.getHelp();
			if( help != null){
				attr("title", help);
			}
			String shortcut = action.getShortcut();
			if( shortcut != null){
				attr("accesskey", shortcut);
			}
			if( action_name != null ){
				attr("name", action_name);
			}else{
				attr("name",name);
			}
			attr("value",name);
			if( must_validate) {
				// non validsating actions always enabled
				if( action instanceof DisabledAction || ! can_submit){
					attr("disabled",null);
				}
			}
			
			if( content != null ){
				addObject(content);
			}
			close();
		}
		close();
	}
	
}



public void setActionName(String action_name) {
	this.action_name = action_name;
}
public void setTableSections(boolean value){
	use_table_section=Boolean.valueOf(value);
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
	return conn.getService(LoggerService.class).getLogger(getClass());
}


/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getDetails(java.lang.String)
 */
@Override
public ContentBuilder getDetails(Object summary_text) {
	Panel details = new Panel("details",this,false,"details");
	if( summary_text != null ){
		details.open("summary");
		details.addObject(summary_text);
		details.close();
	}
	return details;
}


public boolean useNewTab() {
	return new_tab;
}


public boolean setNewTab(boolean new_tab) {
	boolean old = this.new_tab;
	this.new_tab = new_tab;
	return old;
}
}