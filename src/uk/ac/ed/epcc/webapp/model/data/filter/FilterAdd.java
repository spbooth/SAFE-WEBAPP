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
import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterSelect;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.FieldValue;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** Class to perform an in database increment based on a filter
 * 
 * @author spb
 *
 * @param <T>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FilterAdd.java,v 1.4 2014/09/15 14:30:30 spb Exp $")

public class FilterAdd<T> extends FilterSelect<T> {
	 private final Repository res;
	    public FilterAdd(Repository r){
	    	res=r;
	    }
		
	    @SuppressWarnings("unchecked")
		public <R extends Number> int update(FieldValue<R,T> target, R value,SQLFilter<T> my_filter) throws DataFault{
	    	StringBuilder sql = new StringBuilder();
	    	sql.append("UPDATE ");
	    	res.addTable(sql, true);
	    	sql.append(" SET ");
	    	target.add(sql, false);
	    	sql.append("=");
	    	target.add(sql, false);
	    	sql.append(" + ? ");
	    	
	    	if( my_filter != null ){
	    		sql.append(" WHERE ");
	    		makeWhere(my_filter, sql, false);
	    	}
	    	try{
	    		PreparedStatement stmt = res.getSQLContext().getConnection().prepareStatement(
	    				sql.toString());
	    		List<PatternArgument> list = new LinkedList<PatternArgument>();
				list.add(new ConstPatternArgument<R>(target.getTarget(), value));
	    		
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