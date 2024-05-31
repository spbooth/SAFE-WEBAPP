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
import java.util.*;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Class to null record fields based on a filter
 * 
 * @author spb
 *
 * @param <T>
 */


public class FilterWipe<T> extends FilterSelect<T> {
	 private final Repository res;
	    public FilterWipe(Repository r){
	    	res=r;
	    }
		
	    public <R extends Number> int update(Set<String> fields,SQLFilter<T> my_filter) throws DataFault{
	    	if( isEmpty(my_filter) || fields.isEmpty()){
	    		return 0;
	    	}
	    	int count=0;
	    	StringBuilder sql = new StringBuilder();
	    	// assume no joins in filter
	    	sql.append("UPDATE ");
	    	res.addSource(sql, true);
	    	sql.append(" SET ");
	    	for(String field : fields) {
	    		FieldInfo info = res.getInfo(field);
	    		if( info != null && info.getNullable() ) {
	    			if( count > 0) {
	    				sql.append(",");
	    			}
	    			count++;
	    			info.addName(sql, true, true);
	    			sql.append("=null ");
	    		}
	    	}
	    	if( count==0) {
	    		return 0;
	    	}
	    	HashSet<Repository> tables = new HashSet<>();
	    	tables.add(res);
	    	
	    	if( my_filter != null ){
	    		sql.append(" WHERE ");
	    		makeWhere(tables,my_filter, sql, false);
	    	}
	    	SQLContext sqlContext = res.getSQLContext();
	    	try(PreparedStatement stmt=sqlContext.getConnection().prepareStatement(
    				sql.toString())){
	    		List<PatternArgument> list = new LinkedList<>();
				
	    		list=getFilterArguments(my_filter, list);
	    		setParams(1, sql, stmt, list);
	    		if( DatabaseService.LOG_UPDATE.isEnabled(res.getContext())){
	    			Logger.getLogger(res.getContext(),getClass()).debug("Query is "+sql);
	    		}
	    		int updates = stmt.executeUpdate();
	    		if( updates > 0) {
	    			res.flushCache();
	    		}
				return updates;
	    	}catch(SQLException e){
	    		sqlContext.getService().handleError("Error on update",e);
	    		return 0; // actually unreachable
	    	}
	    }

}