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

import java.sql.SQLException;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public abstract class DatabaseServiceWrapper implements DatabaseService {
	
	/**
	 * @param nested
	 */
	public DatabaseServiceWrapper(DatabaseService nested) {
		super();
		this.nested = nested;
	}

	protected final DatabaseService nested;

	public DatabaseService getNested() {
		return nested;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return nested.getContext();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super DatabaseService> getType() {
		return nested.getType();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		nested.cleanup();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext(java.lang.String, java.util.Properties)
	 */
	@Override
	public SQLContext getSQLContext(String tag, Properties config_props) throws SQLException {
		return wrap(nested.getSQLContext(tag, config_props));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext()
	 */
	@Override
	public SQLContext getSQLContext() throws SQLException {
		return wrap(nested.getSQLContext());
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext(java.lang.String)
	 */
	@Override
	public SQLContext getSQLContext(String tag) throws SQLException {
		return wrap(nested.getSQLContext(tag));
	}
	public abstract SQLContext wrap(SQLContext ctx);

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#inTransaction()
	 */
	@Override
	public boolean inTransaction() {
		return nested.inTransaction();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#transactionStage()
	 */
	@Override
	public int transactionStage() {
		return nested.transactionStage();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#startTransaction()
	 */
	@Override
	public void startTransaction() {
		nested.startTransaction();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() {
		nested.rollbackTransaction();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		nested.commitTransaction();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#stopTransaction()
	 */
	@Override
	public void stopTransaction() {
		nested.stopTransaction();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#handleError(java.lang.String, java.sql.SQLException)
	 */
	@Override
	public void handleError(String message, SQLException e) throws DataFault {
		nested.handleError(message, e);

	}
	@Override
	public void logError(String message, SQLException e) {
		nested.logError(message, e);
	}

}
