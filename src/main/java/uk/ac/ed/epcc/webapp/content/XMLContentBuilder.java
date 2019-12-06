//| Copyright - The University of Edinburgh 2019                            |
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

import java.text.NumberFormat;
import java.util.Iterator;
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
import uk.ac.ed.epcc.webapp.forms.inputs.CanSubmitVisistor;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.log.Viewable;
import uk.ac.ed.epcc.webapp.servlet.ServeDataServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/**Interface for combined {@link ContentBuilder} and {@link ExtendedXMLBuilder}
 * for a web context where {@link ContentBuilder} is implemented as html.
 * 
 * most of the {@link ContentBuilder} methods (those that only rely on the {@link ExtendedXMLBuilder} interface)
 *  are implemented as default methods
 * so different {@link ExtendedXMLBuilder} sub-classes can 
 * share a single implementation.
 * 
 * 
 * @author Stephen Booth
 *
 */
public interface XMLContentBuilder extends ContentBuilder,ExtendedXMLBuilder{
	
	public static final Feature STREAM_BUILDER_FEATURE = new Feature("html.stream_builder",true,"use HtmlWriter where possible");
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.forms.Field)
	 */
	@Override
	public default <I,T> void addFormLabel(AppContext conn,Field<I> f, T item) {
		try {
			HtmlFormPolicy p = getFormPolicy();
			p.addFormLabel(this, conn, f, item);
		}catch(Exception e) {
			getLogger(conn).error("Error making form label", e);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.forms.Field)
	 */
	@Override
	public default <I,T> void addFormInput(AppContext conn,Field<I> f,T item) {
		try {
			HtmlFormPolicy p = getFormPolicy();
			p.addFormInput(this, conn, f, item);
		}catch(Exception e) {
			getLogger(conn).error("Error making form label", e);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.util.Collection)
	 */
	@Override
	public default <X> void addList(Iterable<X> list) {
		addList(null,list);
	}
	@Override
	public default <X> void addList(Map<String,String> attr,Iterable<X> list) {
		open("ul");
		attr(attr);
		for(X target : list){
			open("li");
			addObject(target);
			close();
		}
		close();
		clean("\n");
	}
	@Override
	public default <X> void addNumberedList(int start,Iterable<X> list) {
		open("ol");
		attr("start",Integer.toString(start));
		for(X target : list){
			open("li");
			addObject(target);
			close();
		}
		close();
		clean("\n");
	}


	/**
	 * @param target
	 */
	@Override
	public default <X> void addObject(X target) {
		if( target == null ) {
			return;
		}
		if( target instanceof UIProvider){
			((UIProvider)target).getUIGenerator().addContent(this);
		}else if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if(target instanceof XMLPrinter) {
			append((XMLPrinter)target);
		}else if( target instanceof Identified){
			if( target instanceof Viewable && target instanceof Contexed) {
				addLink(((Contexed)target).getContext(), ((Identified)target).getIdentifier(), ((Viewable)target).getViewTransition());
			}else {
				clean(((Identified)target).getIdentifier());
			}
		}else if( target  instanceof Iterable){
			addList((Iterable)target);
		}else if( target instanceof Object[]) {
			addList((Object []) target);
		}else{
			clean(target.toString());
		}
	}
	public void append(XMLPrinter target);
	public Logger getLogger(AppContext conn);
	public boolean useNewTab();
	public boolean setNewTab(boolean new_tab);
	public HtmlFormPolicy getFormPolicy();
	@Override
	public default <X> void addList(X[] list) {
		open("ul");
		for(X target : list){
			open("li");
			addObject(target);
			close();
		}
		close();
		clean("\n");
	}
	@Override
	public default void addButton(AppContext conn,String text, FormResult action) {
		AddButtonVisitor vis = new AddButtonVisitor(conn, this, text);
		vis.new_tab=useNewTab();
		try {
			action.accept(vis);
		} catch (Exception e) {
			getLogger(conn).error("Error adding Button",e);
		}
	}
	@Override
	public default void addButton(AppContext conn,String text, String hover,FormResult action) {
		AddButtonVisitor vis = new AddButtonVisitor(conn, this, text,hover);
		vis.new_tab=useNewTab();
		try {
			action.accept(vis);
		} catch (Exception e) {
			getLogger(conn).error("Error adding Button",e);
		}
	}
	@Override
	public default void addLink(AppContext conn,String text, FormResult action) {
		addLink(conn,text,null,action);
	}
	@Override
	public default void addLink(AppContext conn,String text, String hover,FormResult action) {
		if( action == null){
			clean(text);
			return;
		}
		AddLinkVisitor vis = new AddLinkVisitor(conn, this, text,hover);
		vis.new_tab=useNewTab();
		try {
			action.accept(vis);
		} catch (Exception e) {
			getLogger(conn).error("Error adding Link",e);
		}
	}
	@Override
	public default void addImage(AppContext conn, String alt, String hover,Integer width, Integer height, ServeDataResult image) {
		if( image == null) {
			return;
		}
		try {
			String url = conn.getService(ServletService.class).encodeURL(ServeDataServlet.getURL(conn, image.getProducer(), image.getArgs()));
			open("img");
			if( alt != null) {
				attr("alt", alt);
			}
			if( hover != null ) {
				attr("title",hover);
			}
			if( width != null && width.intValue() > 0) {
				attr("width",width.toString());
			}
			if( height != null && height.intValue() > 0) {
				attr("height",height.toString());
			}

			attr("src",url);
			close();
		}catch(Exception t) {
			getLogger(conn).error("Error adding image",t);
		}
	}


	@Override
	public default <C,R> void addTable(AppContext conn,Table<C,R> t,String style) {
		addTable(conn,t,null,style);
	}
	
	@Override
	public default <C, R> void addColumn(AppContext conn, Table<C, R> t, C col) {
		TableXMLFormatter<C,R> fmt = new TableXMLFormatter<>(this, null,"auto");
		fmt.addColumn(t,col);
	}


	@Override
	public default void addText(String text) {
		open("div");
		addClass("para");
		clean(text);
		close();
	}


	@Override
	public default void addHeading(int level, String text) {
		open("h"+level);
		clean(text);
		close();
		
	}


	@Override
	public default <C, R> void addTable(AppContext conn, Table<C, R> t) {
		addTable(conn,null,t);
	}
	@Override
	public default <C, R> void addTable(AppContext conn, NumberFormat nf,Table<C, R> t) {
		addTable(conn, t, nf, "auto");
		
	}
	@Override
	public default <C,R> void addTable(AppContext conn,Table<C,R> t,NumberFormat nf,String style) {
		TableXMLFormatter<C,R> fmt = new TableXMLFormatter<>(this, nf,style);
		fmt.setTableSections(true);
		fmt.add(t);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(java.lang.Iterable)
	 */
	@Override
	public default void addFormTable(AppContext conn,Iterable<Field> form) {
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public default void addActionButtons(Form f,String legend,Set<String> actions) {
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
				
				attr("name",name);
				
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
	@Override
	default ContentBuilder getDetails(Object summary_text) {
		open("details");
		addClass("details");
		if( summary_text != null ){
			open("summary");
			addObject(summary_text);
			close();
		}
		return this;
	}
	@Override
	default void closeDetails() {
		close();	
	}

}
