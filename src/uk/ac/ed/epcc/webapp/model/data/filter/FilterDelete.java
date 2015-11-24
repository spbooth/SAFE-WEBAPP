// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.filter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** Class to implement filter driven deletion. 
 * 
 * @author spb
 *
 * @param <T>
 */
public class FilterDelete<T extends DataObject> extends FilterSelect<T>{
    private final Repository res;
    public FilterDelete(Repository r){
    	res=r;
    }
	
    @SuppressWarnings("unchecked")
	public int delete(SQLFilter<T> my_filter) throws DataFault{
    	StringBuilder sql = new StringBuilder();
    	sql.append("DELETE from ");
    	res.addTable(sql, true);
    	
    	if( my_filter != null ){
    		sql.append(" WHERE ");
    		makeWhere(my_filter, sql, false);
    	}
    	try{
    		PreparedStatement stmt = res.getSQLContext().getConnection().prepareStatement(
    				sql.toString());
    		List<PatternArgument> list = new LinkedList<PatternArgument>();
			
    		if (my_filter != null && my_filter instanceof PatternFilter) {
    			list = ((PatternFilter<T>) my_filter).getParameters(list);
    		}
    		setParams(1, sql, stmt, list);
    		if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(res.getContext())){
    			res.getContext().getService(LoggerService.class).getLogger(getClass()).debug("Query is "+sql);
    		}
    		return stmt.executeUpdate();
    	}catch(SQLException e){
    		throw new DataFault("Error on delete",e);
    	}
    }

}
