// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/** Encodes the arguments to a PreparedStatement for a select clause.
 * 
 * @author spb
 *
 */
public interface PatternArgument {
	/** Add a argument to a PreparedStatment
	 * 
	 * @param stmt PreparedStatement to modify
	 * @param pos  int position to add parameter
	 * @throws SQLException
	 */
	public void addArg(PreparedStatement stmt,int pos) throws SQLException;
	/** Get field name.
	 * 
	 * For debugging messages.
	 * 
	 * @return Field name
	 */
	public String getField();
	/** Get raw object
	 * 
	 * @return Raw object.
	 */
	public Object getArg();
	/** Is this argument allowed to be logged.
	 * This is for marking info like passwords that should never be logged.
	 * @return boolean
	 */
	public boolean canLog();
}