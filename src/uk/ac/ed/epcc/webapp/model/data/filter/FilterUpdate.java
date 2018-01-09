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
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLExpression;
import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.FieldSQLExpression;
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
		
	    @SuppressWarnings("unchecked")
		public <R> int update(FieldSQLExpression<R,T> target, R value,SQLFilter<T> my_filter) throws DataFault{
	    	if( isEmpty(my_filter)){
	    		return 0;
	    	}
	    	StringBuilder sql = new StringBuilder();
	    	sql.append("UPDATE ");
	    	res.addSource(sql, true);
	    	sql.append(" SET ");
	    	target.add(sql, false);
	    	sql.append("=?");
	    	
	    	if( my_filter != null ){
	    		sql.append(" WHERE ");
	    		makeWhere(my_filter, sql, false);
	    	}
	    	try{
	    		PreparedStatement stmt = res.getContext().getService(DatabaseService.class).getSQLContext(res.getDBTag()).getConnection().prepareStatement(
	    				sql.toString());
	    		List<PatternArgument> list = new LinkedList<PatternArgument>();
				list.add(new ConstPatternArgument<R>(target.getTarget(), value));
	    		
				list = getFilterArguments(my_filter, list);
	    		setParams(1, sql, stmt, list);
	    		if( DatabaseService.LOG_UPDATE.isEnabled(res.getContext())){
	    			res.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Query is "+sql);
	    		}
	    		return stmt.executeUpdate();
	    	}catch(SQLException e){
	    		throw new DataFault("Error on update",e);
	    	}
	    }
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
	    		sql.append(" WHERE ");
	    		makeWhere(my_filter, sql, false);
	    	}
	    	try{
	    		PreparedStatement stmt = res.getContext().getService(DatabaseService.class).getSQLContext(res.getDBTag()).getConnection().prepareStatement(
	    				sql.toString());
	    		List<PatternArgument> list = new LinkedList<PatternArgument>();
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
	    		throw new DataFault("Error on update",e);
	    	}
	    }
}