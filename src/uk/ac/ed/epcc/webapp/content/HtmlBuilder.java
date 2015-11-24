// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
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
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.PrefixInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TagInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;


/** A {@link HtmlPrinter} that also implements {@link ContentBuilder} 
 * in terms of web based interfaces.
 * 
 * @author spb
 *
 */

@uk.ac.ed.epcc.webapp.Version("$Id: HtmlBuilder.java,v 1.30 2015/11/09 16:32:09 spb Exp $")

public class HtmlBuilder extends HtmlPrinter implements ContentBuilder  {
  
public static final Feature HTML_USE_LABEL_FEATURE = new Feature("html.use_label",true,"generate html labels in automatic forms");
public static final Feature HTML_TABLE_SECTIONS_FEATURE = new Feature("html.table_sections",false,"generate thead/tbody in tables");
Boolean use_table_section=null;

protected static final class Text extends HtmlPrinter {
	  Text(HtmlBuilder parent){
		  super(parent);
		  open("div");
		  attr("class","para");
	  }
		@Override
		public HtmlBuilder appendParent() throws UnsupportedOperationException {
			if( isInOpen() && sb.length()==0){
				// this is an empty div which confuses some browsers
				// supress box entirely
				return (HtmlBuilder) getParent();
			}
			close();
			clean("\n");
			return (HtmlBuilder) super.appendParent();
		}
		public void addText(String text) {
			clean(text);
		}
		public ExtendedXMLBuilder getText() {
			return new HtmlPrinter(this);
		}
	}
  protected static final class Heading extends HtmlBuilder {
	  int level;
	  Heading(HtmlBuilder parent,int level){
		  super(parent);
		  open("h"+level);
		  this.level=level;
	  }
		@Override
		public HtmlBuilder appendParent() throws UnsupportedOperationException {
			if( isInOpen() && sb.length()==0){
				// this is an empty heading which mightconfuses some browsers
				// supress box entirely
				return (HtmlBuilder) getParent();
			}
			close();
			clean("\n");
			return (HtmlBuilder) super.appendParent();
		}
		public void addText(String text) {
			clean(text);
		}
		public ExtendedXMLBuilder getText() {
			return new HtmlPrinter(this);
		}
	}
  protected static final class Panel extends HtmlBuilder {
	  String type;
	  Panel(HtmlBuilder parent, String type){
		  super(parent);
		  this.type=type;
	  }
		@Override
		public HtmlBuilder appendParent() throws UnsupportedOperationException {
			HtmlBuilder parent = (HtmlBuilder) getParent();
			parent.open("div");
			parent.attr("class", type);
			parent.clean("\n");
			super.appendParent();
			parent.clean("\n");
			parent.close();
			parent.clean("\n");
			return parent;
		}
	}
public HtmlBuilder(){
	super();
	setEscapeUnicode(false);
}
  
  
  public HtmlBuilder(HtmlBuilder htmlBuilder) {
	super(htmlBuilder);
}


public void addButton(AppContext conn,String text, FormResult action) {
	AddButtonVisitor vis = new AddButtonVisitor(conn, this, text);
	try {
		action.accept(vis);
	} catch (Exception e) {
		conn.error(e,"Error adding Button");
	}
}
public void addButton(AppContext conn,String text, String hover,FormResult action) {
	AddButtonVisitor vis = new AddButtonVisitor(conn, this, text,hover);
	try {
		action.accept(vis);
	} catch (Exception e) {
		conn.error(e,"Error adding Button");
	}
}
public void addLink(AppContext conn,String text, FormResult action) {
	if( action == null){
		clean(text);
		return;
	}
	AddLinkVisitor vis = new AddLinkVisitor(conn, this, text);
	try {
		action.accept(vis);
	} catch (Exception e) {
		conn.error(e,"Error adding Link");
	}
}
public <C,R> void addTable(AppContext conn,Table<C,R> t,String style) {
	TableXMLFormatter<C,R> fmt = new TableXMLFormatter<C,R>(this, null,style);
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


public ExtendedXMLBuilder getText() {
	return new Text(this);
}
public ExtendedXMLBuilder getSpan() {
	return new HtmlPrinter(this);
}

public ContentBuilder getHeading(int level) {
	return new Heading(this,level);
}


public ContentBuilder getPanel(String type)
		throws UnsupportedOperationException {
	return new Panel(this,type);
}


public ContentBuilder addParent() throws UnsupportedOperationException {
	return (ContentBuilder) appendParent();
}


public <C, R> void addColumn(AppContext conn, Table<C, R> t, C col) {
	TableXMLFormatter<C,R> fmt = new TableXMLFormatter<C,R>(this, null,"auto");
	fmt.addColumn(t,col);
}


public void addText(String text) {
	open("div");
	attr("class","para");
	clean(text);
	close();
}


public void addHeading(int level, String text) {
	open("h"+level);
	clean(text);
	close();
	
}



public <C, R> void addTable(AppContext conn, Table<C, R> t) {
	addTable(conn, t, "auto");
	
}

private Collection<String> missing_fields=null;
private Map<String,String> errors=null;
private Map<String,Object> post_params=null;


/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(java.lang.Iterable)
 */
public void addFormTable(AppContext conn,Iterable<Field> form) {
	Iterator<Field> it = form.iterator();
	boolean even=true;
	if( it.hasNext() ){
		open("table");
		attr("class", "form");
		while (it.hasNext()) {
			Field<?> f = it.next();
		    try {
		    	open("tr");
		    	if( even ){
		    		attr("class", "even");
		    	}else{
		    		attr("class","odd");
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
				conn.error(e,"Error making html from form");
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
		HtmlBuilder parent = (HtmlBuilder) getParent();
		if( parent != null){
			return parent.getError(field);
		}else{
			return null;
		}
	}
	return errors.get(field);
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.forms.Field)
 */
public <I> void addFormLabel(AppContext conn,Field<I> f) {
	String key = f.getKey();
	boolean missing = isMissing(key);
	String error = getError(key);
	Input<I> i = f.getInput();
	boolean optional = i instanceof OptionalInput
	&& ((OptionalInput) i).isOptional();
	if( HTML_USE_LABEL_FEATURE.isEnabled(conn)){
		open("label");
		InputIdVisitor vis = new InputIdVisitor(form_id);
		try {
			String id =  (String) i.accept(vis);
			if( id != null){
				attr("for",id);
			}
		} catch (Exception e) {
			conn.error(e,"Error getting id");
		}
	}else{
		open("span");
	}
	if (optional) {
		attr("class","optional");
	}else{
		attr("class","required");
	}
	clean(f.getLabel());
	close(); // span or label

	if (missing) {
		open("b");
		open("span");
		attr("class", "warn");
		clean("*");
		close(); //span
		close(); //b
	}
	if (error != null) {
		open("b");
		open("span");
		attr("class", "warn");
		clean(error);
		close(); //span
		close(); //b
	}
	
}


/** should we do browser required field validation
 * 
 */
private boolean use_required=true;
public void setUseRequired(boolean use_required){
	this.use_required=use_required;
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.forms.Field)
 */
public <I,T> void addFormInput(AppContext conn,Field<I> f,T item) {
	String key =  f.getKey();
	String error = null;
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
		EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(conn,this, use_post, post_params,form_id);
		vis.setRadioTarget(item);
		vis.setUseRequired(use_required);
		i.accept(vis);
	}catch(Exception e){
		conn.error(e,"Error generating input");
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
private String form_id=null;
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
 */
public void addActionButtons(Form f) {
	Iterator<String> it = f.getActionNames();
	if( it.hasNext()){
		open("fieldset");
		attr("class", "action_buttons");
		while ( it.hasNext()) {
			String name =  it.next();
			FormAction action = f.getAction(name);
			String text = action.getText();
			if( text != null ){
				open("button");
			}else{
				open("input");
			}
			attr("class","input_button");
			attr("type","submit");
			if( ! action.getMustValidate()){
				attr("formnovalidate",null);
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
			if( action instanceof DisabledAction){
				attr("disabled",null);
			}
			if( text != null ){
				clean(text);
			}
			close();
		}
		close();
	}
	
}


/** Set an ID prefix so to make {@link Input}s from
 * different forms unique. Set a different id for each form.
 * 
 * Don't use period or colon as this makes the id's difficult to select
 * in javascript
 * 
 * @param id
 */
public void setFormID(String id){
	this.form_id=id;
}

public void setActionName(String action_name) {
	this.action_name = action_name;
}
public void setTableSections(boolean value){
	use_table_section=Boolean.valueOf(value);
}


/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.util.Collection)
 */
public <X> void addList(Iterable<X> list) {
	open("ul");
	for(X target : list){
		open("li");
		if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if( target instanceof Identified){
			clean(((Identified)target).getIdentifier());
		}else{
			clean(target.toString());
		}
		close();
	}
	close();
	
}
public <X> void addList(X[] list) {
	open("ul");
	for(X target : list){
		open("li");
		if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if( target instanceof Identified){
			clean(((Identified)target).getIdentifier());
		}else{
			clean(target.toString());
		}
		close();
	}
	close();
	
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
}