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
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;

/** SQLAccessor that represents a field in the DataBase
 * 
 * @author spb
 *
 * @param <T> type of field data
 * @param <X> type of owning DataObject
 */
public interface FieldValue<T,X> extends  SQLAccessor<T,X>, GroupingSQLValue<T>{
	/** Get the corresponding field name. This should not be used to create SQL fragments but
	 * can be used to identify Repository field names
	 * 
	 * @return database field name
	 */
	public abstract String getFieldName();
	
	/** Add an object of the target type to a prepared statement doing any necessary type conversion
	 * to map it to the SQL representation of the object.
	 * Note this is only valid if canSet from {@link SQLAccessor} returns true.
	 * 
	 * @param stmt
	 * @param pos
	 * @param value
	 * @throws SQLException 
	 */
	public void setObject(PreparedStatement stmt, int pos, T value) throws SQLException;

}