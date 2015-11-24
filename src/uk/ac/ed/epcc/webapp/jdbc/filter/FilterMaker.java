// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.timer.TimerService;
/** Class to create single items specified by a filter and {@link ResultMapper}.
 * 
 * @author spb
 * @param  <T> type of filter
 * @param <O> Type of object being produced
 *
 */
public abstract class FilterMaker<T,O> extends FilterReader<T,O> {
  
	public FilterMaker(AppContext c,Class<? super T> target) {
		super(c,target);
	}
	
   protected O make() throws DataException{
	   StringBuilder query = new StringBuilder();
		BaseFilter<T> f = getFilter();
		if( f != null && ! (f instanceof SQLFilter)){
			// null filter is ok
			throw new ConsistencyError("Illegal filter in FilterMaker");
		}
		
		makeSelect(query);
		String modify = getModify();
		if( modify != null ){
			query.append(" ");
			query.append(modify);
		}
		AppContext conn = getContext();
		String q = query.toString();
		try {
			TimerService timer = conn.getService(TimerService.class);
			if( timer != null ){
				timer.startTimer("FilterMaker: "+q);
			}
			// We are only using the REsultSet to initialise the Record and
			// are not concurrent anyway so give
			// the DB the best chance of optimising the query
			PreparedStatement stmt = conn.getService(DatabaseService.class).getSQLContext(getDBTag()).getConnection().prepareStatement(
					q, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			List<PatternArgument> list = new LinkedList<PatternArgument>();
			list=getTargetParameters(list);
			if (f instanceof PatternFilter) {
				list = ((PatternFilter<T>)f).getParameters(list);
			}
			list=getModifyParameters(list);
			setParams(1,query, stmt, list);
			if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(conn)){
				getLogger().debug("Query is "+query);
			}
			ResultSet rs = stmt.executeQuery();
			O result = null;
			if( rs.next()){ // need to position the REsultSet before making object
				result = makeEntry(rs);
				if( result ==null ){
					result = makeDefault();
				}
				if( rs.next() ){
					if( DataObjectFactory.REJECT_MULTIPLE_RESULT_FEATURE.isEnabled(conn)){
				
						throw new MultipleResultException("Found multiple results expecting 1 :"+query.toString());
					}else{
						// just log
						conn.getService(LoggerService.class).getLogger(getClass()).error("Multiple result expecting 1"+query.toString(),new Exception());
					}
				}
			}else{
				result = makeDefault();
			}
			stmt.close();
			
			if( timer != null ){
				timer.stopTimer("FilterMaker: "+q);
			}
			
			return result;
		} catch (SQLException e) {
			throw new DataException("DataFault in FilterFinder " + query, e);
		}
   }

}