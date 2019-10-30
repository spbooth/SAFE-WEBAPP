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
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.html.AddButtonVisitor;
import uk.ac.ed.epcc.webapp.forms.html.AddLinkVisitor;
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
}
