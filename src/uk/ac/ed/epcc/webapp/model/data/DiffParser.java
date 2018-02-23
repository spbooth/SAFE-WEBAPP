//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.io.IOException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.IdMode;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;

/** A {@link DumpParser} parses a baseline dump of a database and compares it with
 * the current database state via calls to {@link Dumper#dumpDiff(Record, Record)} on an enclosed {@link Dumper}.
 * Records from the baseline will either be added to the dump as changes or marked as seen. The
 * same {@link Dumper} can then be used to generate a full database dump adding and records added since
 * the baseline.
 * <p>
 * The dump being compared against must be a newly generated dump from an earlier state of the
 * <em>same</em> database as this code cannot work out the correct id to use if the
 * record has changed and has to use the id from the parse. 
 * 
 
 * @author spb
 *
 */

public class DiffParser extends DumpParser {

	
	private final Dumper dumper;
	public Dumper getDumper() {
		return dumper;
	}


	/**
	 * @param conn
	 */
	public DiffParser(AppContext conn,Dumper dumper) {
		super(conn,false);
		this.dumper=dumper;
		
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#processRecord(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	public int processRecord(int parse_id,Record rec) throws ConsistencyError, DataException, IOException {
		
		// would this even work without assuming parsed-ids are correct.
		// if the id-s are remapped 
		Repository rep = rec.getRepository();
		
		// Get the record as it currently is in the database
		Record updated = rep.new Record();
		try {
			updated.setID(parse_id);
		}catch(DataNotFoundException e) {
			updated=null;
		}
		
		// compare with the value from the baseline parse
		dumper.dumpDiff(updated, rec);
		// This is a known record that has changed so return its real id
		// Here we assume the dump is from the SAME database we are currently looking at.
		return parse_id;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#processSpecification(java.lang.String, uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification)
	 */
	@Override
	public void processSpecification(String table, TableSpecification spec)
			throws DataFault {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#skipSpecification(java.lang.String)
	 */
	@Override
	public boolean skipSpecification(String table) {
		return true;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#getIdMode()
	 */
	@Override
	protected IdMode getIdMode() {
		// Need to ignore the DB state because we are
		// parsing a previous dump and merge will
		// fail to detect null -> value change
		return IdMode.IgnoreExisting;
	}

}