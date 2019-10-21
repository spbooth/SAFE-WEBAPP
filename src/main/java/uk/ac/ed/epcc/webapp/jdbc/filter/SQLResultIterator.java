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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.CloseableIterator;
import uk.ac.ed.epcc.webapp.model.data.Removable;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.TimerService;
/** Iterator over filter results based on the SQL parts of a filter
 * 
 *  * <p>
	 * For large queries the results are generated in batches to reduce memory
	 * use. It is up to the calling method not to retain too many references to
	 * the generated objects.
	 * <p>
	 * This is an {@link CloseableIterator} as it wraps jdbc {@link AutoCloseable}s. It should close automatically
	 * when the iterator is exhausted. It also registers itself with the {@link DatabaseService} to be closed 
	 * on {@link AppContext} cleanup if not explicitly closed before then.
	 * <p>
	 * Note that where the filter implements more than one of the supported
	 * filter interfaces AcceptFilter, ConditionFilter, PatternFilter The
	 * iterator will return the intersection of the sets selected by each
	 * interface.
	 * <p>
	 * The ResultMapper, target, source and order clauses are set as methods
	 *
	 * @param <T> type of filter
 * @param <O> Type of object produced.
 * 
 */
public abstract class SQLResultIterator<T,O> extends FilterReader<T,O> implements CloseableIterator<O> {
	static final int DEFAULT_CHUNKSIZE = 1024;
		
	// Would like this to be a preference but this class is needed to evaluate preferences
	// Nasty interaction with basic-auth and password-fail count
	// 
    //static final Feature CHUNKING_FEATURE= new Preference("chunking",true,"retrieve SQL data in chunks using limit clause",SessionService.ADMIN_ROLE,"Tester");
	static final Feature CHUNKING_FEATURE= new Feature("chunking",false,"retrieve SQL data in chunks using limit clause");
		private PreparedStatement stmt;
		private ResultSet rs;
		
		private int chunkstart;

		private int maxreturn;

		private int chunksize;

		private int nchunck=0;
		private int pos = 0;

		private int parm_pos = 1;

		private O next;
		private O prev = null;
		
		private int chunk = DEFAULT_CHUNKSIZE;
        private String tag;
		private boolean use_chunking = false;
		/**
		 * Set the chunking this factory is to use
		 * 
		 * reset to default if value less than 1
		 * 
		 * @param i
		 *            new value oc chunking
		 */
		public final void setChunkSize(int i) {
			if (i > 1) {
				chunk = i;
			} else {
				chunk = DEFAULT_CHUNKSIZE;
			}
		}
		public final boolean useChunking() {
			return use_chunking;
		}
		/**
		 * Get the default chunk size for large database queries.
		 * 
		 * @return size of chunk
		 */
		protected final int getChunkSize() {
			return chunk;
		}
		
		
        protected SQLResultIterator(AppContext c, Class<T> target){
            super(c,target);
            use_chunking = CHUNKING_FEATURE.isEnabled(getContext());
			chunk = getContext().getIntegerParameter("chunksize", DEFAULT_CHUNKSIZE);
        }
		
		
	 
		/**
		 * Get the next Chunk from the stream
		 * 
		 * @throws SQLException
		 * 
		 */
		private final void fetchChunk() throws SQLException {
			assert(stmt != null); // known empty query won't make stmt but this should not be called
			
			int chunk = chunksize;
			AppContext conn = getContext();
			TimerService timer = conn.getService(TimerService.class);
			
			if (maxreturn > 0 && (!useChunking() || maxreturn < chunk)) {
				chunk = maxreturn;
			}

			if (useChunking() || maxreturn > 0) {
				stmt.setInt(parm_pos, chunkstart);
				stmt.setInt(parm_pos + 1, chunk);
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(conn)){
				  getLogger().debug("fetchChunk "+chunkstart+","+chunk+" maxreturn "+maxreturn);
				}
			}
			try{
				if( timer != null ){
					timer.startTimer(tag);
					if( nchunck > 0) {
						// explicit timer for impact of chunked 
						// if this shows up consider disabling or increasing chunksize
						timer.startTimer("chunk-"+tag);
					}
				}
			    rs = stmt.executeQuery();
			}finally{
				if( timer != null ){ 
					if( nchunck > 0) {
						timer.stopTimer("chunk-"+tag);
					}
					timer.stopTimer(tag);
				}
			}
			pos = 0;
			chunkstart += chunk;
			nchunck++;
			if (maxreturn > 0) {
				maxreturn -= chunk;
			}
		}

		

		

		@Override
		public final boolean hasNext() {
			if( next == null ){
				close();
				return false;
			}
			return true;
		}

		/**
		 * return the next object from the stream. Once the
		 * end of the stream is reached return null
		 * 
		 * @throws SQLException
		 * @throws DataException 
		 */
		protected  O iterate() throws SQLException, DataException{
            //Logger log = getLogger();
            //
			
            assert( rs != null ); // known empty query would not make result set but this should not be called
            
            //log.debug("In iterate");
			O my_next=null;
			do {

				if (rs.next()) {
					// Note that if makeEntry returns null for a result
					// then that value is just skipped from the sequence.
					// and the loop will continue.
					my_next = makeEntry(rs);
					pos++;
					//log.debug("pos="+pos);
				} else {
					//log.debug("No more");
					rs.close();
					// may need next row
					if (useChunking()) {
						if (pos < chunksize - 1) {
							// short result must be at end
							return null;
						} else {
							fetchChunk();
							my_next = null;
						}
					} else {
						return null;
					}
				}
			} while (my_next == null );
			return my_next;
		}

		
		
	

		

	
		@Override
		public final O next() {
			O result = next;

			if (result != null) {
				try {
					next=iterate();
				} catch (DataException e) {
					getContext().error(e, "DataFault in ResultIterator");
					next=null;
				} catch (SQLException e) {
					getContext().getService(DatabaseService.class).logError("SQLException in ResultIterator",e);
					next=null;
				}
				if( next == null ){
					close();
				}
			}else{
				throw new NoSuchElementException();
			}
			prev=result;
			return result;
		}

		/** close the underlying statement etc. to free resources
		 * @throws SQLException 
		 * 
		 */
		@Override
		public void close()  {
			if( stmt == null){
				return; // already closed
			}
			// Note this can be called when close called from DatabaseService cleanup itself
			// after service is de-registered
			DatabaseService db_serv = getContext().getService(DatabaseService.class);
			if( db_serv != null) {
				db_serv.removeClosable(this);
			}
			try {
				if( rs != null && ! rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				if( db_serv != null) {
					db_serv.logError("Error closing ResultSet",e);
				}
			}
			rs=null;
			
			try {
				if( ! stmt.isClosed()) {
					stmt.close();
				}
			} catch (SQLException e) {
				if( db_serv != null) {
					db_serv.logError("Error closing statement",e);
				}
			}
			stmt = null;
			
		}

		@Override
		public final void remove() {
			if( prev != null && prev instanceof Removable){
				try {
					((Removable) prev).remove();
					chunkstart--;
					return;
				} catch (Exception e) {
					throw new UnsupportedOperationException(e);
				}
				
			}
			throw new UnsupportedOperationException(
					"Cannot remove in BasicDataObject.Iterator");
		}

		/** last ditch order clause to use if no other modify string
		 * 
		 * inlcude "ORDER BY" if set.
		 * 
		 * @return
		 */
		protected String fallbackOrder(){
			return null;
		}
		
		
		/** method to do the initialisation
		 * 
		 * @param f
		 * @param start
		 * @param max
		 * @throws DataException 
		 */
		protected void setup(BaseFilter<T> f, int start, int max) throws DataException {
			
			if( isEmpty(f)){
				return;
			}
			
			String modify=null;
			//getLogger().debug("args "+start+","+max);
			chunkstart = start;
			maxreturn = max;
			prev=null;
			setFilter(f);
			
			StringBuilder query = new StringBuilder();
			
			
			makeSelect(query);
			// Use mapper/sub-class modify by preference
			modify=getModify();
			if( modify == null || modify.isEmpty()){
				// Nor take order filter
			if ( f != null && f instanceof OrderFilter) {
				OrderFilter<?> o = (OrderFilter) f;
				List<OrderClause> orderBy = o.OrderBy();
				if( orderBy != null ){
					

					boolean seen=false;

					if( orderBy.size() > 0){
						StringBuilder builder = new StringBuilder();
						builder.append(" ORDER BY ");
						for(OrderClause c : orderBy){
							if( seen){
								builder.append(", ");
							}
							seen=true;
							c.addClause(builder, getQualify());
						}
						modify = builder.toString();
					}
					
				}
			}
			
			}
			if( modify == null ){
				modify = fallbackOrder();
			}
			if (modify != null) {
				query.append(" ").append(modify);
			}else{
				getLogger().warn("No order clasue");
			}
			if (useChunking() || maxreturn > 0) {
				query.append(" LIMIT ?,?");
			}
			DatabaseService db_serv = getContext().getService(DatabaseService.class);
			db_serv.addClosable(this);
			
			try {
				tag=query.toString();
				//System.out.println("Query is "+query);
				// We are only using the REsultSet to initialise the Record and
				// are not concurrent anyway so give
				// the DB the best chance of optimising the query
				
				stmt = db_serv.getSQLContext(getDBTag()).getConnection().prepareStatement(
						query.toString(), ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				List<PatternArgument> list = new LinkedList<>();
				list=getTargetParameters(list);
				list=getFilterArguments(f, list);
				list=getModifyParameters(list);
				parm_pos = setParams(1,query, stmt, list);
				if( DatabaseService.LOG_QUERY_FEATURE.isEnabled(getContext())){
				  getLogger().debug("FilterIterator - Query : " + query);
				}
				chunksize = getChunkSize();
				if (!useChunking() && chunksize > 0) {
					// see if we can get the SQL to chunk for us
					stmt.setFetchSize(chunksize);
				}
				rs = null;
				// initialise the next pointer.

				fetchChunk();

				next=iterate();

			} catch (SQLException e) {
				db_serv.handleError("DataFault in FilterIterator " + query, e);
			}

		}
	}