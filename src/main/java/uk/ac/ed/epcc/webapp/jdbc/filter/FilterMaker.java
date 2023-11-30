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

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
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
  
	public FilterMaker(AppContext c,String tag) {
		super(c,tag);
	}
	private StringBuilder query=null;
	
	protected String getLastQuery() {
		if( query == null) {
			return "";
		}
		return query.toString();
	}
	
	
   protected O make() throws DataException{
	    query = new StringBuilder();
		BaseFilter<? super T> f = getFilter();
		if( isEmpty(f)){
			return makeDefault();
		}
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
		String lock_clause = getLockClause();
		if( lock_clause != null ) {
			query.append(lock_clause);
		}
		AppContext conn = getContext();
		String q = query.toString();
		TimerService timer = conn.getService(TimerService.class);
		if( timer != null ){
			timer.startTimer("FilterMaker: "+q);
		}
		
		
		DatabaseService db_service = conn.getService(DatabaseService.class);
		try {
			SQLContext sqlContext = db_service.getSQLContext(getDBTag());
			Connection connection = sqlContext.getConnection();
			try(PreparedStatement stmt= connection.prepareStatement(
					q, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY)) {

				// We are only using the REsultSet to initialise the Record and
				// are not concurrent anyway so give
				// the DB the best chance of optimising the query


				List<PatternArgument> list = new LinkedList<>();
				list=getTargetParameters(list);
				list=getFilterArguments(f, list);
				list=getModifyParameters(list);
				setParams(1,query, stmt, list);
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(conn)){
					getLogger().debug("Query is "+query);
				}
				try( ResultSet rs = stmt.executeQuery()){
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
								Logger.getLogger(conn,getClass()).error("Multiple result expecting 1"+query.toString(),new Exception());
							}
						}
					}else{
						result = makeDefault();
					}
					return result;
				}
			} 
		} catch (SQLException e) {
			db_service.handleError("DataFault in FilterFinder " + query, e);
			return null;
		}finally{
			if( timer != null ){
				timer.stopTimer("FilterMaker: "+q);
			}
		}
   }

}