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