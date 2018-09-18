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
import java.util.Map;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;

/**
 * @author Stephen Booth
 *
 */
public class RollbackInsertionDatabaseService extends DatabaseServiceWrapper {

	/**
	 * @author Stephen Booth
	 *
	 */
	public final class RollbackSQLContext extends SQLContextWrapper {
		private Connection c=null;

		/**
		 * @param db
		 * @param nested
		 */
		public RollbackSQLContext(DatabaseService db, SQLContext nested) {
			super(db, nested);
		}

		@Override
		public Connection getConnection() {
			if( c == null) {
				c = new RollbackInsertionWrapper(getNested().getConnection(),getService());
			}
			return c;
		}
	}

	/**
	 * @param nested
	 */
	public RollbackInsertionDatabaseService(DatabaseService nested) {
		super(nested);
	}
	
	private Map<SQLContext,SQLContextWrapper> map = new HashMap<>();

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.wrap.DatabaseServiceWrapper#wrap(uk.ac.ed.epcc.webapp.jdbc.SQLContext)
	 */
	@Override
	public SQLContext wrap(SQLContext ctx) {
		SQLContextWrapper c = map.get(ctx);
		if( c == null) {
			c = new RollbackSQLContext(this, ctx);
			map.put(ctx, c);
		}
		return c;
	}

}
