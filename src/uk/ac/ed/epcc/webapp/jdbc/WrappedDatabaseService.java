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
package uk.ac.ed.epcc.webapp.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

/** A {@link DatabaseService} wrapper that can beused to generate a tabe
 * showing the current active connections
 * @author Stephen Booth
 *
 */
public class WrappedDatabaseService implements DatabaseService {
	private final DatabaseService inner;
	private final String id;
	private static class Status{
		public final String id;
		public final int session;
		public final Date start;
		public Status(String id, int session) {
			this.id=id;
			this.session=session;
			start=new Date();
		}
	}
	
	private static final Map<WrappedDatabaseService,Status> sessions = new HashMap<>();
	
	public static synchronized void add(WrappedDatabaseService serv) {
		if( sessions.containsKey(serv)) {
			return;
		}
		try {
			SQLContext s = serv.inner.getSQLContext();
			Statement st = s.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select @@SESSION.pseudo_thread_id");
			int session=0;
			if( rs.next()) {
				session = rs.getInt(1);
			}
			rs.close();
			st.close();
			String id = serv.id;
			ServletService ss = serv.getContext().getService(ServletService.class);
			if( ss != null ) {
				id = ss.encodePage();
			}
			sessions.put(serv, new Status(id,session));
		} catch (SQLException e) {
			serv.inner.logError("Error creating session log", e);
		}
		
	}
	public static synchronized void remove(WrappedDatabaseService serv) {
		sessions.remove(serv);
	}
	
	public static synchronized Table getStatusTable() {
		Table t = new Table();
		for(Status s : sessions.values()) {
			t.put("ID", s.session, s.id);
			t.put("Start", s.session, s.start);
		}
		t.setKeyName("Session");
		return t;
	}
	/**
	 * @param conn 
	 * 
	 */
	public WrappedDatabaseService(AppContext conn) {
		this(conn.getService(DatabaseService.class),"spontaneous");
	}
	public WrappedDatabaseService(DatabaseService inner,String id) {
		this.inner=inner;
		this.id=id;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return inner.getContext();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super DatabaseService> getType() {
		return DatabaseService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextCleanup#cleanup()
	 */
	@Override
	public void cleanup() {
		inner.cleanup();
		remove(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#addClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void addClosable(AutoCloseable c) {
		inner.addClosable(c);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.CloseRegistry#removeClosable(java.lang.AutoCloseable)
	 */
	@Override
	public void removeClosable(AutoCloseable c) {
		inner.removeClosable(c);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext(java.lang.String, java.util.Properties)
	 */
	@Override
	public SQLContext getSQLContext(String tag, Properties config_props) throws SQLException {
		return inner.getSQLContext(tag, config_props);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext()
	 */
	@Override
	public SQLContext getSQLContext() throws SQLException {
		add(this);
		return inner.getSQLContext();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#getSQLContext(java.lang.String)
	 */
	@Override
	public SQLContext getSQLContext(String tag) throws SQLException {
		return inner.getSQLContext(tag);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#inTransaction()
	 */
	@Override
	public boolean inTransaction() {
		return inner.inTransaction();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#transactionStage()
	 */
	@Override
	public int transactionStage() {
		return inner.transactionStage();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#startTransaction()
	 */
	@Override
	public void startTransaction() {
		inner.startTransaction();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() {
		inner.rollbackTransaction();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		inner.commitTransaction();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#stopTransaction()
	 */
	@Override
	public void stopTransaction() {
		inner.stopTransaction();

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#handleError(java.lang.String, java.sql.SQLException)
	 */
	@Override
	public void handleError(String message, SQLException e) throws DataFault {
		inner.handleError(message, e);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.DatabaseService#logError(java.lang.String, java.sql.SQLException)
	 */
	@Override
	public void logError(String message, SQLException e) {
		inner.logError(message, e);
	}

}
