// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.sql.PreparedStatement;
import java.util.List;



/** Type of filter that specifies the SQL using  parameterised query
 * 
 * @author spb
 * @param <T> type of object selected
 * 
 */
public interface PatternFilter<T> extends BaseSQLFilter<T> {
	

	/** Add parameters for this filter to a list.
	 * @param list to modify
	 * 
	 * @return modified list of parameter objects
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list);

	/**
	 * get a Parameterised selection SQL clause
	 * @param sb StringBuilder to modify
	 * 
	 * @see PreparedStatement
	 * @param qualify request field names to be qualified with table name.
	 * @return modified StringBuilder
	 */
	public StringBuilder addPattern(StringBuilder sb,boolean qualify);
}