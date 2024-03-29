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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;
import uk.ac.ed.epcc.webapp.model.data.FieldValuePatternArgument;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Class to perform an in database update based on a filter
 * 
 * @author spb
 *
 * @param <T>
 */


public class FilterUpdate<T> extends FilterSelect<T> {
	 private final Repository res;
	    public FilterUpdate(Repository r){
	    	res=r;
	    }
		/** Update a field to a constant value
		 * 
		 * This can update any {@link FieldValue} including references because
		 * {@link FieldValue} knows how to add its target type to a statement
		 * 
		 * @param target {@link FieldValue}
		 * @param value
		 * @param my_filter
		 * @return number of records updated.
		 * @throws DataFault
		 */
	    public <R> int update(FieldValue<R,T> target, R value,SQLFilter<T> my_filter) throws DataFault{
	    	return update(my_filter, new FieldValuePatternArgument<R,T>(target, value));
	    }
	    /** Update multiple fields to a constant values
		 * 
		 * This can update any {@link FieldValue} including references because
		 * {@link FieldValue} knows how to add its target type to a statement
		 * 
		 * @param my_filter
		 * @param args {@link FieldValuePatternArgument}s for each update
		 * @return number of records updated.
		 * @throws DataFault
		 */	
	    public  int update(SQLFilter<T> my_filter, FieldValuePatternArgument<?,T> ... args) throws DataFault {
	    	if( isEmpty(my_filter)){
	    		return 0;
	    	}
	    	StringBuilder sql = new StringBuilder();
	    	sql.append("UPDATE ");
	    	res.addSource(sql, true);
	    	sql.append(" SET ");
	    	boolean seen=false;
	    	for(FieldValuePatternArgument<?,T> a : args) {
	    		if( seen ) {
	    			sql.append(",");
	    		}
	    		a.getFieldValue().addField(sql, false);
	    		sql.append("=? ");
	    		seen=true;
	    	}
	
	    	
	    	
	    	if( my_filter != null ){
	    		HashSet<Repository> tables = new HashSet<>();
		    	tables.add(res);
	    		sql.append("WHERE ");
	    		makeWhere(tables,my_filter, sql, false);
	    	}
	    	SQLContext sqlContext = res.getSQLContext();
	    	
	    	try(PreparedStatement stmt=sqlContext.getConnection().prepareStatement(
    				sql.toString())){
	    		List<PatternArgument> list = new LinkedList<>();
	    		for(FieldValuePatternArgument<?,T> a : args) {
	    			list.add(a);
	    		}
	    		
				list = getFilterArguments(my_filter, list);
	    		setParams(1, sql, stmt, list);
	    		if( DatabaseService.LOG_UPDATE.isEnabled(res.getContext())){
	    			res.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Query is "+sql);
	    		}
	    		int count = stmt.executeUpdate();
	    		if( count > 0 ) {
	    			res.flushCache();
	    		}
				return count;
	    	}catch(SQLException e){
	    		sqlContext.getService().handleError("Error on update",e);
	    		return 0; // acutally unreachable
	    	}
	    }
	    /** Update using an {@link SQLExpression}.
	     * 
	     * This requires the target to be a {@link FieldSQLExpression}.
	     * 
	     * @param target
	     * @param value
	     * @param my_filter
	     * @return
	     * @throws DataFault
	     */
	    @SuppressWarnings("unchecked")
		public <R> int updateExpression(FieldSQLExpression<R,T> target, SQLExpression<R> value,SQLFilter<T> my_filter) throws DataFault{
	    	StringBuilder sql = new StringBuilder();
	    	sql.append("UPDATE ");
	    	res.addSource(sql, true);
	    	sql.append(" SET ");
	    	target.add(sql, false);
	    	sql.append("=");
	    	value.add(sql, false);
	    	
	    	if( my_filter != null ){
	    		HashSet<Repository> tables = new HashSet<>();
		    	tables.add(res);
	    		sql.append(" WHERE ");
	    		makeWhere(tables,my_filter, sql, false);
	    	}
	    	SQLContext sqlContext = res.getSQLContext();
	    	try(PreparedStatement stmt=sqlContext.getConnection().prepareStatement(
    				sql.toString())){
	    		List<PatternArgument> list = new LinkedList<>();
	    		list = value.getParameters(list);
	    		
	    		if (my_filter != null && my_filter instanceof PatternFilter) {
	    			list = ((PatternFilter)my_filter).getParameters(list);
	    		}
	    		setParams(1, sql, stmt, list);
	    		if( DatabaseService.LOG_UPDATE.isEnabled(res.getContext())){
	    			res.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Query is "+sql);
	    		}
	    		return stmt.executeUpdate();
	    	}catch(SQLException e){
	    		sqlContext.getService().handleError("Error on update",e);
	    		return 0; // actually unreachable
	    	}
	    }
}