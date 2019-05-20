//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.jdbc.wrap;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author Stephen Booth
 *
 */
public class PreparedStatementWrapper<C extends Connection,P extends PreparedStatement> extends StatementWrapper<C,P> implements PreparedStatement {

	/**
	 * @param nested
	 */
	public PreparedStatementWrapper(C conn,P nested) {
		super(conn,nested);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	@Override
	public void addBatch() throws SQLException {
		getNested().addBatch();

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	@Override
	public void clearParameters() throws SQLException {
		getNested().clearParameters();

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	@Override
	public boolean execute() throws SQLException {
		return getNested().execute();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		return getNested().executeQuery();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	@Override
	public int executeUpdate() throws SQLException {
		return getNested().executeUpdate();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return getNested().getMetaData();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return getNested().getParameterMetaData();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	@Override
	public void setArray(int arg0, Array arg1) throws SQLException {
		getNested().setArray(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		getNested().setAsciiStream(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		getNested().setAsciiStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		getNested().setAsciiStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		getNested().setBigDecimal(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		getNested().setBinaryStream(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		getNested().setBinaryStream(arg0, arg1,arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		getNested().setBinaryStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	@Override
	public void setBlob(int arg0, Blob arg1) throws SQLException {
		getNested().setBlob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
	 */
	@Override
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		getNested().setBlob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
	 */
	@Override
	public void setBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		getNested().setBlob(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	@Override
	public void setBoolean(int arg0, boolean arg1) throws SQLException {
		getNested().setBoolean(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	@Override
	public void setByte(int arg0, byte arg1) throws SQLException {
		getNested().setByte(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	@Override
	public void setBytes(int arg0, byte[] arg1) throws SQLException {
		getNested().setBytes(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		getNested().setCharacterStream(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
		getNested().setCharacterStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		getNested().setCharacterStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	@Override
	public void setClob(int arg0, Clob arg1) throws SQLException {
		getNested().setClob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	@Override
	public void setClob(int arg0, Reader arg1) throws SQLException {
		getNested().setClob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
	 */
	@Override
	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		getNested().setClob(arg0, arg1,arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	@Override
	public void setDate(int arg0, Date arg1) throws SQLException {
		getNested().setDate(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	@Override
	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
		getNested().setDate(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	@Override
	public void setDouble(int arg0, double arg1) throws SQLException {
		getNested().setDouble(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	@Override
	public void setFloat(int arg0, float arg1) throws SQLException {
		getNested().setFloat(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	@Override
	public void setInt(int arg0, int arg1) throws SQLException {
		getNested().setInt(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	@Override
	public void setLong(int arg0, long arg1) throws SQLException {
		getNested().setLong(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		getNested().setNCharacterStream(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		getNested().setNCharacterStream(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
	 */
	@Override
	public void setNClob(int arg0, NClob arg1) throws SQLException {
		getNested().setNClob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
	 */
	@Override
	public void setNClob(int arg0, Reader arg1) throws SQLException {
		getNested().setNClob(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	@Override
	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		getNested().setNClob(arg0, arg1,arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	@Override
	public void setNString(int arg0, String arg1) throws SQLException {
		getNested().setNString(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	@Override
	public void setNull(int arg0, int arg1) throws SQLException {
		getNested().setNull(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	@Override
	public void setNull(int arg0, int arg1, String arg2) throws SQLException {
		getNested().setNull(arg0, arg1,arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	@Override
	public void setObject(int arg0, Object arg1) throws SQLException {
		getNested().setObject(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	@Override
	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
		getNested().setObject(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	@Override
	public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
		getNested().setObject(arg0, arg1, arg2,arg3);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	@Override
	public void setRef(int arg0, Ref arg1) throws SQLException {
		getNested().setRef(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
	 */
	@Override
	public void setRowId(int arg0, RowId arg1) throws SQLException {
		getNested().setRowId(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		getNested().setSQLXML(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	@Override
	public void setShort(int arg0, short arg1) throws SQLException {
		getNested().setShort(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	@Override
	public void setString(int arg0, String arg1) throws SQLException {
		getNested().setString(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	@Override
	public void setTime(int arg0, Time arg1) throws SQLException {
		getNested().setTime(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	@Override
	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
		getNested().setTime(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
		getNested().setTimestamp(arg0, arg1);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
		getNested().setTimestamp(arg0, arg1, arg2);

	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	@Override
	public void setURL(int arg0, URL arg1) throws SQLException {
		getNested().setURL(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		getNested().setUnicodeStream(arg0, arg1, arg2);

	}

}
