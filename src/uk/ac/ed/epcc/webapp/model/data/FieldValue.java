// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;

/** SQLAccessor that represents a field in the DataBase
 * 
 * @author spb
 *
 * @param <T> type of field data
 * @param <X> type of owning DataObject
 */
public interface FieldValue<T,X> extends  SQLAccessor<T,X>{
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