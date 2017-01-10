//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.radius;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.codec.digest.Crypt;


import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordChangeListener;
import uk.ac.ed.epcc.webapp.session.RandomService;
import uk.ac.ed.epcc.webapp.session.WebNameFinder;

/**
 * @author spb
 *
 */
public class RadiusPasswordChangeListener implements PasswordChangeListener, Contexed {

	private static final char[][] salt_chars = { { 'a', 'z' }, { 'A', 'Z' }, { '0', '9' }, {'.'},{'/'}};
	private final AppContext conn;
	private final String realm; // realm to use as username
	private final String name_suffix;
	/**
	 * 
	 */
	public RadiusPasswordChangeListener(AppContext conn) {
		this.conn=conn;
		realm=conn.getInitParameter("radius.realmname",WebNameFinder.WEB_NAME);
		name_suffix=conn.getInitParameter("radius.name_suffix");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordChangeListener#passwordInvalid(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void passwordInvalid(AppUser u) {
		update(getName(u),"!");
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.PasswordChangeListener#setPassword(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@Override
	public void setPassword(AppUser u, String password) {
		RandomService serv = conn.getService(RandomService.class);
		String salt = serv.randomString(salt_chars, 16);
		String hash = Crypt.crypt(password.getBytes(), "$5$"+salt+"$");
		update(getName(u), hash);
	}
	
	private void update(String name, String entry){
		
		try {
			DatabaseService serv = conn.getService(DatabaseService.class);
			SQLContext sql = serv.getSQLContext("radius");
			Connection c = sql.getConnection();
			PreparedStatement set = c.prepareStatement("UPDATE radcheck SET value=? WHERE username=? and attribute=?");
			set.setString(1, entry);
			set.setString(2, name);
			set.setString(3, "Crypt-Password");
			int rows = set.executeUpdate();
			if( rows == 0){
				set = c.prepareStatement("INSERT INTO radcheck (username,attribute,op,value) VALUES (?,?,?,?)");
				set.setString(1, name);
				set.setString(2, "Crypt-Password");
				set.setString(3, ":=");
				set.setString(4, entry);
				rows = set.executeUpdate();
			}
			
		} catch (Throwable  e) {
			conn.getService(LoggerService.class).getLogger(getClass()).error("Error in password update", e);
		}
	}

	private String getName(AppUser u){
		String realmName = u.getRealmName(realm);
		if( name_suffix != null ){
			return realmName+name_suffix;
		}
		return realmName;
	}
}
