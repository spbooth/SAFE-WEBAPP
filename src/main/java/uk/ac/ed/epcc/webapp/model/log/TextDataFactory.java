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
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterUpdate;

/** A {@link DataObjectFactory} for {@link TextData}
 * @author Stephen Booth
 *
 */
public class TextDataFactory<T extends TextData> extends DataObjectFactory<T> implements AnonymisingFactory{

	public static final String DEFAULT_TABLE = "TextData";

	public TextDataFactory(AppContext conn, String table) {
		super();
		setContext(conn, table);
	}
	public TextDataFactory(AppContext conn) {
		this(conn,DEFAULT_TABLE);
	}
	public static TextDataFactory getFactory(AppContext conn,String tag) {
		return conn.makeObject(TextDataFactory.class, tag);
	}
	public static TextDataFactory getFactory(AppContext conn) {
		return getFactory(conn,DEFAULT_TABLE);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected T makeBDO(Record res) throws DataFault {
	
		return (T) new TextData(res);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		return TextData.getTableSpecification();
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingFactory#anonymise()
	 */
	@Override
	public void anonymise() throws DataFault {
		FilterUpdate<T> update = new FilterUpdate<>(res);
		// wipe all text data
		update.update(res.getStringExpression(TextData.TEXT), "redacted-text", null);
		
	}

}
