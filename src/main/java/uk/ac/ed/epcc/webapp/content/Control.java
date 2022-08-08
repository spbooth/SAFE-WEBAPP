//| Copyright - The University of Edinburgh 2013                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;

/** A {@link UIGenerator} used to add a {@link Form} control/input
 * to a table.
 * 
 * @author Stephen Booth
 *
 * @param <I> type of {@link Field}
 * @see Label
 */
public class Control<I> implements UIGenerator{
	public Control(AppContext conn,Field<I> field) {
		this(conn,field,null);
	}
	public Control(AppContext conn,Field<I> field,Object item) {
		this(conn,field,item,false);
	}
	/** A control together with the form error
	 * (This is for content where the normal form label is not shown)
	 * 
	 * @param conn
	 * @param field
	 * @param item
	 * @param add_error
	 */
	public Control(AppContext conn,Field<I> field,Object item,boolean add_error) {
		this.conn=conn;
		this.field = field;
		this.radio_selector=item;
		this.add_error=add_error;
	}
	public final Field<I> field;
	public final AppContext conn;
	public final Object radio_selector;
	public final boolean add_error;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.UIGenerator#addContent(uk.ac.ed.epcc.webapp.content.ContentBuilder)
	 */
	public ContentBuilder addContent(ContentBuilder builder) {
		if( add_error) {
			builder.addFormError(conn, field, radio_selector);
		}
		builder.addFormInput(conn, field,radio_selector);
		return builder;
	}
	
}