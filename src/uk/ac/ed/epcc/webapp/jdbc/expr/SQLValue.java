// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** An object that can create a value from a SQL fragment.
 * We encode both the SQL
 *  fragment used in the select and the java code needed to convert this to and from a java type in
 *  the same class as there is an implicit dependency between the two.
 * Note that though there must be a one-to-one relation between the result of the select and the returned
 * object the makeObject method may apply an arbitrary mapping.
 * 
 * 
 * 
 * @author spb
 *
 * @param <T> type produced
 * @see SQLExpression 
 * 
 */
public interface SQLValue<T> extends Targetted<T>{
	
	/** Add the expression to a StringBuilder
	 * 
	 * @param sb StringBuilder to modify
	 * @param qualify boolean should fields be qualified with the table name
	 * @return number of fields added
	 */
	public int add(StringBuilder sb, boolean qualify);
	/** Add parameters for this value to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list);

	/** Extract a result of the expression from a ResultSet into an object of the specified type.
	 * 
	 * Note that this method is also used to extract the result of functions over the result type.
	 * @param rs  ResultSet
	 * @param pos
	 * @return produced object
	 * @throws DataException 
	 */
	public T makeObject(ResultSet rs, int pos) throws DataException;
	
	/** Get an SQLFilter required to be added to the filter set.
	 * This is usually to implement a join.
	 * 
	 * @return null of SQLFilter
	 */
	public SQLFilter getRequiredFilter();
}