// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;

/** A {@link ContentBuilder} that generates plain text.
 * @author spb
 *
 */
public class TextContentBuilder implements ContentBuilder, ExtendedXMLBuilder {
	private final StringBuilder sb = new StringBuilder();
	private final TextContentBuilder parent;
	
	public TextContentBuilder(){
		this.parent=null;
	}
	private TextContentBuilder(TextContentBuilder parent){
		this.parent=parent;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getText()
	 */
	@Override
	public ExtendedXMLBuilder getText() {
		return new TextContentBuilder(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getSpan()
	 */
	@Override
	public ExtendedXMLBuilder getSpan() {
		return new TextContentBuilder(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addText(java.lang.String)
	 */
	@Override
	public void addText(String text) {
		sb.append(text);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#cleanFormatted(int, java.lang.String)
	 */
	@Override
	public boolean cleanFormatted(int max, String s) {
		sb.append(s);
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getHeading(int)
	 */
	@Override
	public ContentBuilder getHeading(int level) {
		return new TextContentBuilder(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addHeading(int, java.lang.String)
	 */
	@Override
	public void addHeading(int level, String text) {
		sb.append(text);
		sb.append("\n");
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getPanel(java.lang.String)
	 */
	@Override
	public ContentBuilder getPanel(String ... type) throws UnsupportedOperationException {
		return new TextContentBuilder(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addParent()
	 */
	@Override
	public ContentBuilder addParent() throws UnsupportedOperationException {
		if( parent != null ){
			parent.sb.append(sb.toString());
		}
		return parent;
	}
	public <X> void addObject(X target) {
		if( target instanceof UIProvider){
			((UIProvider)target).getUIGenerator().addContent(this);
		}else if( target instanceof UIGenerator){
			((UIGenerator)target).addContent(this);
		}else if( target instanceof Identified){
			clean(((Identified)target).getIdentifier());
		}else if( target  instanceof Iterable){
			addList((Iterable)target);
		}else{
			clean(target.toString());
		}
	}
	
	public <X> boolean canAdd(X target) {
		if( target instanceof UIProvider){
			return true;
		}else if( target instanceof UIGenerator){
			return true;
		}else if( target instanceof Identified){
			return true;
		}else if( target  instanceof Iterable){
			return true;
		}else{
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.lang.Iterable)
	 */
	@Override
	public <X> void addList(Iterable<X> list) {
		sb.append("\n");
		for(X item : list){
			addObject(item);
			sb.append("\n");
		}
		
	}
	@Override
	public <X> void addNumberedList(int start,Iterable<X> list) {
		sb.append("\n");
		int i=start;
		for(X item : list){
			sb.append(Integer.toString(i++));
			sb.append(": ");
			addObject(item);
			sb.append("\n");
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.lang.Object[])
	 */
	@Override
	public <X> void addList(X[] list) {
		sb.append("\n");
		for(X item : list){
			addObject(item);
			sb.append("\n");
		}
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addButton(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, uk.ac.ed.epcc.webapp.forms.result.FormResult)
	 */
	@Override
	public void addButton(AppContext conn, String text, FormResult action) {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addButton(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, java.lang.String, uk.ac.ed.epcc.webapp.forms.result.FormResult)
	 */
	@Override
	public void addButton(AppContext conn, String text, String hover, FormResult action) {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addLink(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, uk.ac.ed.epcc.webapp.forms.result.FormResult)
	 */
	@Override
	public void addLink(AppContext conn, String text,String hover, FormResult action) {
		sb.append(text);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addLink(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, uk.ac.ed.epcc.webapp.forms.result.FormResult)
	 */
	@Override
	public void addLink(AppContext conn, String text, FormResult action) {
		sb.append(text);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addTable(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.Table)
	 */
	@Override
	public <C, R> void addTable(AppContext conn, NumberFormat nf,Table<C, R> t) {
		TextTableFormatter< C, R> fmt = new TextTableFormatter<>(nf,t);
		fmt.add(sb);
	}
	@Override
	public <C, R> void addTable(AppContext conn, Table<C, R> t) {
		addTable(conn,null,t);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addTable(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.Table, java.lang.String)
	 */
	@Override
	public <C, R> void addTable(AppContext conn, Table<C, R> t, NumberFormat nf,String style) {
		addTable(conn, nf, t);
		
	}
	@Override
	public <C, R> void addTable(AppContext conn, Table<C, R> t, String style) {
		addTable(conn,t,null,style);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addColumn(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.content.Table, java.lang.Object)
	 */
	@Override
	public <C, R> void addColumn(AppContext conn, Table<C, R> t, C col) {
		Table.Col c = t.getCol(col);
		sb.append("\n");
		for( R row : t.getRows()){
			Object val = c.get(row);
			if( val instanceof UIGenerator){
				((UIGenerator)val).addContent(this);
			}else{
				sb.append(val);
			}
			sb.append("\n");
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormTable(uk.ac.ed.epcc.webapp.AppContext, java.lang.Iterable)
	 */
	@Override
	public void addFormTable(AppContext conn, Iterable<Field> f) {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormLabel(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field)
	 */
	@Override
	public <I,T> void addFormLabel(AppContext conn, Field<I> f, T item) {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addFormInput(uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.forms.Field, java.lang.Object)
	 */
	@Override
	public <I, T> void addFormInput(AppContext conn, Field<I> f, T item) {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addActionButtons(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void addActionButtons(Form f,String legend,Set<String> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addActionButton(Form f, String name) {
		// TODO Auto-generated method stub
		
	}
	public String toString(){
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.CharSequence)
	 */
	@Override
	public SimpleXMLBuilder clean(CharSequence s) {
		sb.append(s);
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(char)
	 */
	@Override
	public SimpleXMLBuilder clean(char c) {
		sb.append(c);
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#clean(java.lang.Number)
	 */
	@Override
	public SimpleXMLBuilder clean(Number i) {
		sb.append(i.toString());
		return null;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String)
	 */
	@Override
	public SimpleXMLBuilder open(String tag) {
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#open(java.lang.String, java.lang.String[][])
	 */
	@Override
	public SimpleXMLBuilder open(String tag, String[][] attr) {
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#attr(java.lang.String, java.lang.CharSequence)
	 */
	@Override
	public SimpleXMLBuilder attr(String name, CharSequence s) {
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#close()
	 */
	@Override
	public SimpleXMLBuilder close() {
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#getNested()
	 */
	@Override
	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		
		return new TextContentBuilder(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#appendParent()
	 */
	@Override
	public SimpleXMLBuilder appendParent() throws UnsupportedOperationException {
		
		return (SimpleXMLBuilder) addParent();
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#nbs()
	 */
	@Override
	public void nbs() {
		sb.append(" ");
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#br()
	 */
	@Override
	public void br() {
		sb.append("\n");
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addList(java.util.Map, java.lang.Iterable)
	 */
	@Override
	public <X> void addList(Map<String, String> attr, Iterable<X> list) {
		addList(list);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#getDetails(java.lang.String)
	 */
	@Override
	public ContentBuilder getDetails(Object summary_text) {
		addObject(summary_text);
		return this;
	}
	@Override
	public void closeDetails() {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder#addClass(java.lang.CharSequence)
	 */
	@Override
	public SimpleXMLBuilder addClass(CharSequence s) {
		return this;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.ContentBuilder#addImage(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, uk.ac.ed.epcc.webapp.forms.result.ServeDataResult)
	 */
	@Override
	public void addImage(AppContext conn, String alt, String hover, Integer width, Integer height,
			ServeDataResult image) {
		clean(alt);
		
	}
}
