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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/** Interface for classes that map ResultSets to Objects
 * 
 * Where Filters are used to define which database records are to be selected
 * a ResultMapper defines what data is to retrieved from these records and
 * how it is to be converted into a domain object. 
 * 
 * @author spb
 *
 * @param <O>
 */
public interface ResultMapper<O> {
	/** Request that field names should be qualified with the table
	 * name where appropriate. This is for use with joined tables.
	 * For some result mappers this does not make sense so this method always returns the same value.
	 * @param qualify
	 * @return previous value of qualify flag
	 */
  public boolean setQualify(boolean qualify);
	/** Convert a ResultSet into a domain object.
	 * The ResultSet should already be positioned at the correct starting Row to start creating the object 
	 * 
	 * It is legal for this method to return null to indicate the ResultSet represents an
	 * invalid record that should be ignored by the higher levels of the code. 
	 * 
	 * @param rs ResultSet or null
	 * @return domain object
	 * @throws DataException 
	 * @throws SQLException 
	 */
  public O makeObject(ResultSet rs) throws DataException, SQLException;
 /** Create a default Object to be returned if no SQL results are generated. 
  * If there is no obvious default result then return null. Where there is an obvious result
  * (like a sum=0 or an empty Map) this is less error prone than trying to trap null at the 
  * calling level. Note that FilterFinder can optionally check for null results and generate exceptions 
  * this still fails but makes it easier to find the problem.
  * 
  * @return domain object
  */
  public O makeDefault();
  /** get the target clause of the SQL query.
   * 
   * it makes sense to set it here as it has to match what the ResultMapper is expecting
   * @return String
   */
  public String getTarget();
  
  /** Add parameters for the target clause to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getTargetParameters(List<PatternArgument> list);

  
  /** provide an override GROUP/ORDER by clause
   *  null to use default order from filters
   * @return String SQL clause
   */
  public default String getModify() {
	  return null;
  }
  
  /** Add parameters for the GROUP/ORDER  clause to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getModifyParameters(List<PatternArgument> list);

  /** Get an additional Filter required to make the target string
   * work properly.
   * 
   * This is usually used to add an additional join.
   * 
   * @return null or SQLFilter
   */
  public default SQLFilter getRequiredFilter() {
	  return null;
  }
}
  