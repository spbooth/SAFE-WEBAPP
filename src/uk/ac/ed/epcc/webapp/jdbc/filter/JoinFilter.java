// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** Interface for filters that add an explicit join clause. 
 * The intention here is to filter entries from the primary tables based on the data it
 * points to
 * 
 * The filter clauses on the remote object have to be done entirely in SQL because 
 * we are only returning objects from the 
 * primary table. Any condition clause will need to qualify the field names 
 * 
 * This is added in to the source clause by the {@link FilterReader}.
 * @author spb
 * @param <T> type of object selected
 *
 */
public interface JoinFilter<T> extends PatternFilter<T> {
	
   /** Join clause to add to query
    * 
    * @return String
    */
   public String getJoin();
}