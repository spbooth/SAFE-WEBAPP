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
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
/**
 * @author Stephen Booth
 *
 */
public class CheckCloseConnectionWrapper extends ConnectionWrapper {

	Set<PreparedStatement> prepared_statements = new HashSet<>();
	int statements_open=0;
	int p_statements_open=0;
	int statements_close=0;
	int p_statements_close=0;
	/**
	 * @param nested
	 */
	public CheckCloseConnectionWrapper(Connection nested) {
		super(nested);
	}
	@Override
	public void close() throws SQLException {
		checkClosed();
		super.close();
	}
	/**
	 * 
	 */
	public void checkClosed() {
		assertEquals("statements still open",statements_open,statements_close);
		int still_open = p_statements_open-p_statements_close;
		if( still_open > 0 && still_open < 10) {
			// show statments if less than 10 of them
			assertEquals("prepared-statements still open "+prepared_statements,p_statements_open,p_statements_close);
		}
		assertEquals("prepared-statements still open",p_statements_open,p_statements_close);
	}
	@Override
	public Statement createStatement() throws SQLException {
		
		return new CheckClosedStatement(this, super.createStatement());
	}
	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return new CheckClosedStatement(this, super.createStatement(arg0, arg1));
	}
	@Override
	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		return new CheckClosedStatement(this, super.createStatement(arg0, arg1, arg2));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0, arg1));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0, arg1, arg2));
	}
	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		
		return new CheckClosedPreparedStatement(this, super.prepareStatement(arg0, arg1, arg2, arg3));
	}
	@Override
	public String toString() {
		return "CheckCloseConnectionWrapper [statements_open=" + statements_open + ", p_statements_open="
				+ p_statements_open + ", statements_close=" + statements_close + ", p_statements_close="
				+ p_statements_close + "]";
	}

	
}
