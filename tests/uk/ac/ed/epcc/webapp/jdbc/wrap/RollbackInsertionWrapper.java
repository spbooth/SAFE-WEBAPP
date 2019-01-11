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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.sql.Statement;

import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;

/** A {@link ConnectionWrapper} that throws an {@link SQLException} to simulate a transient fault.
 * 
 * Ideally we should also wrap the {@link PreparedStatement} but to start with we perform the check
 * when making the {@link PreparedStatement}
 * @author Stephen Booth
 *
 */
public class RollbackInsertionWrapper extends ConnectionWrapper {

	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0, arg1, arg2));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		runcheck();
		return new RollbackInsertionPreparedStatement(this, super.prepareStatement(arg0, arg1, arg2, arg3));
	}
	@Override
	public Statement createStatement() throws SQLException {
		runcheck();
		return new RollbackInsertionStatement(this, super.createStatement());
	}
	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		runcheck();
		return new RollbackInsertionStatement(this, super.createStatement(arg0, arg1));
	}
	@Override
	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		runcheck();
		return new RollbackInsertionStatement(this, super.createStatement(arg0, arg1, arg2));
	}
	private int count=0;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	private int target=-1;
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
	/**
	 * @param nested
	 */
	public RollbackInsertionWrapper(Connection nested,DatabaseService serv) {
		super(nested);
		this.serv=serv;
	}

	private final DatabaseService serv;
	public void runcheck() throws SQLException{
		if( serv.inTransaction()) {
			count++;
			if(count == target) {
				
				SQLTransactionRollbackException e = new SQLTransactionRollbackException("Inserted fault");
				e.printStackTrace(); // make sure we can find where we inserted
				throw e;
			}
		}
	}
}
