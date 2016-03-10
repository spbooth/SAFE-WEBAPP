//| Copyright - The University of Edinburgh 2015                            |
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

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.RadioButtonInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Interface used to add content to a user interface.
 * These operations are intended to be sufficiently generic 
 * to be implemented by both HTML and GUI interfaces
 * 
 * @author spb
 *
 */

public interface ContentBuilder {
	
	/** create a {@link ExtendedXMLBuilder} that appends its contents as XML
	 * text when the {@link ExtendedXMLBuilder#appendParent()} method is called.
	 * 
	 * Normally this represents a separate paragraph of text but this can be mapped 
	 * onto {@link #getSpan()} where this does not make sense for example where the 
	 * {@link ContentBuilder} represents a heading or table cell.
	 * @return ExtendedXMLBuilder
	 */
	public ExtendedXMLBuilder getText();
	
	/** create a {@link ExtendedXMLBuilder} that appends its contents as XML
	 * text when the {@link ExtendedXMLBuilder#appendParent()} method is called.
	 * 
	 * This is intended for in-line text rather than creating a paragraph.
	 * 
	 * @return {@link ExtendedXMLBuilder}
	 */
	public ExtendedXMLBuilder getSpan();
	/** Add unformatted text. Equivalent to
	 * {@link #getText()}.clean(text).appendParent().
	 * @param text string to add to content
	 * 
	 */
	public void addText(String text);
	/** add a pre-formatted string as a text section.
	 * In HTML this is 
	 * either as a pre-formated section or a paragraph containing a series of explicitly broken lines 
	 * (if any of the lines are longer than the specified threshold.
	 * @param max max line length
	 * @param s
	 * @return true if lines are long
	 */
	public abstract boolean cleanFormatted(int max, String s);
	/** create a {@link ContentBuilder} that appends its contents as a
	 * heading when the {@link ContentBuilder#addParent()} method is called.
	 * 
	 * If the heading is to only contain text use the {@link #addHeading(int, String)} method.
	 * @param level level of heading
	 * @return ContentBuilder for header
	 */
	public ContentBuilder getHeading(int level);
	
	/** Convenience routine to add a heading containing unformatted text.
	 * This is equivalent to {@link #getHeading(int)}.addText(text).
	 * @param level
	 * @param text
	 */
	public void addHeading(int level , String text);
	
	/** create a nested content panel
	 * 
	 * @param type
	 * @return ContentBuilder for panel
	 * @throws UnsupportedOperationException 
	 */
	public ContentBuilder getPanel(String type) throws UnsupportedOperationException;
	
	/** append a nested panel to its parent content.
	 * @return parent ContentBuilder
	 * 
	 * @throws UnsupportedOperationException
	 */
	public ContentBuilder addParent() throws UnsupportedOperationException;
	
	/** add a bullet list of objects to the content.
	 * Any {@link UIGenerator}s will generate their content otherwise 
	 * a suitable text representation will be used.
	 * 
	 * @param list
	 */
	public abstract <X> void addList(Iterable<X> list);
	
	/** add a bullet list of objects to the content.
	 * Any {@link UIGenerator}s will generate their content otherwise 
	 * a suitable text representation will be used.
	 * @param attr 
	 * @param list
	 */
	public abstract <X> void addList(Map<String,String> attr,Iterable<X> list);
	
	/** add a bullet list of objects to the content.
	 * Any {@link UIGenerator}s will generate their content otherwise 
	 * a suitable text representation will be used.
	 * 
	 * @param list
	 */
	public abstract <X> void addList(X[] list);
	
	/** Add an action button to the Content
	 * @param conn AppContext
	 * @param text
	 * @param action
	 */
	public abstract void addButton(AppContext conn,String text, FormResult action);
	
	/** Add an action button to the Content
	 * @param conn AppContext
	 * @param text
	 * @param hover tooltip text
	 * @param action
	 */
	public abstract void addButton(AppContext conn,String text, String hover, FormResult action);
	/** Add an action link to the Content
	 * @param conn AppContext
	 * 
	 * @param text  link text
	 * @param action FormResult to navigate to.
	 */
	public abstract void addLink(AppContext conn,String text, FormResult action);
	
	/** Add a table to the content.
	 * 
	 * Table content that implements {@link UIGenerator} should be added via {@link UIGenerator#addContent(ContentBuilder)}
	 * @param conn
	 * @param t
	 */
	public <C,R> void addTable(AppContext conn,Table<C,R> t);
	
	/** Add a table to the content.
	 * 
	 * Table content that implements {@link UIGenerator} should be added via {@link UIGenerator#addContent(ContentBuilder)}
	 * @param conn
	 * @param t
	 * @param style
	 */
	public <C,R> void addTable(AppContext conn,Table<C,R> t,String style);
	/** Add the data from a single column of a {@link Table} formatted in
	 * 2 columns (key and data).
	 * 
	 * 
	 * @param conn
	 * @param t
	 * @param col
	 */
	public  <C,R> void addColumn(AppContext conn,Table<C,R> t, C col);
	/** Add a set of {@link Field}s as a table of labels and inputs.
	 * @param conn 
	 * @param f
	 */
	public void addFormTable(AppContext conn,Iterable<Field> f);
	/** Add the label for a form {@link Field}
	 * 
	 * @param conn
	 * @param f 
	 * 
	 */
	public <I> void addFormLabel(AppContext conn,Field<I> f);
	/** Add the input for a form {@link Field}
	 * If the input is a {@link RadioButtonInput} then the item parameter
	 * selects which item the input is for. If item is null then
	 * a {@link RadioButtonInput} will output all inputs in a block.
	 * @param conn AppContext
	 * @param f  Field
	 * @param item item (only used by RadioInputs)
	 */
	public <I,T> void addFormInput(AppContext conn,Field<I> f,T item);
	;
	/** Add the action buttons for a form.
	 * 
	 * @param f
	 */
	public void addActionButtons(Form f);
}