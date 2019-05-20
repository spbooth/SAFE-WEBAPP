//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.jdbc.wrap;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.wrap.RollbackInsertionDatabaseService.RollbackSQLContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * @author Stephen Booth
 *
 */
public class CheckCloseDatabaseService extends DatabaseServiceWrapper {

	/**
	 * @param nested
	 */
	public CheckCloseDatabaseService(DatabaseService nested) {
		super(nested);
		// TODO Auto-generated constructor stub
	}
	public final class CheckClosedSQLContext extends SQLContextWrapper {
		private CheckCloseConnectionWrapper c=null;

		/**
		 * @param db
		 * @param nested
		 */
		public CheckClosedSQLContext(DatabaseService db, SQLContext nested) {
			super(db, nested);
		}

		@Override
		public Connection getConnection() {
			if( c == null) {
				c = new CheckCloseConnectionWrapper(getNested().getConnection());
			}
			return c;
		}

		@Override
		public void close() throws Exception {
			checkClosed();
			super.close();
		}

		/**
		 * 
		 */
		public void checkClosed() {
			if( c != null ) {
				c.checkClosed();
			}
		}

		@Override
		public String toString() {
			return "CheckClosedSQLContext [c=" + c + "]";
		}
	}

	private Map<SQLContext,CheckClosedSQLContext> map = new HashMap<>();

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.wrap.DatabaseServiceWrapper#wrap(uk.ac.ed.epcc.webapp.jdbc.SQLContext)
	 */
	@Override
	public SQLContext wrap(SQLContext ctx) {
		CheckClosedSQLContext c = map.get(ctx);
		if( c == null) {
			c = new CheckClosedSQLContext(this, ctx);
			map.put(ctx, c);
		}
		return c;
	}

	@Override
	public void cleanup() {
		// comment out to check there are no deferred closes
		// uncomment to log which statements still open
		//closeRetainedClosables(); // These would be closed ok in practice
		for(CheckClosedSQLContext x : map.values()) {
			x.checkClosed();
		}
		super.cleanup();
	}

	@Override
	public String toString() {
		return "CheckCloseDatabaseService [map=" + map + "]";
	}
	private Set<AutoCloseable> closes=null;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#addClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void addClosable(AutoCloseable c) {
		if( closes == null) {
			closes = new HashSet<>();
		}
		closes.add(c);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#removeClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void removeClosable(AutoCloseable c) {
		if( closes != null ) {
			closes.remove(c);
		}
		
	}

	public void closeRetainedClosables() {
		if( closes != null) {
			if( ! closes.isEmpty()) {
				LoggerService serv = getContext().getService(LoggerService.class);
				if( serv != null) {
					Logger log = serv.getLogger(getClass());
					if( log != null ) {
						log.warn("Unclosed retained closables "+closes);
					}
				}
			}
			// set will be modified during loop so copy
			for(AutoCloseable c : closes.toArray(new AutoCloseable[closes.size()])) {
				try {
					c.close();
				} catch (Exception e) {
					LoggerService serv = getContext().getService(LoggerService.class);
					if( serv != null) {
						Logger log = serv.getLogger(getClass());
						if( log != null ) {
							log.error("Error in close",e);
						}
					}else {
						throw new ConsistencyError("Unreportable error",e);
					}
				}
			}
			closes.clear();
			closes=null;
		}
	}
}
