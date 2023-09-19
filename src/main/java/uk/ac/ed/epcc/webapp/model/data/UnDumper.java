//| Copyright - The University of Edinburgh 2015                            |
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


import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;


/** A {@link DumpParser} that parses an XML data specification created by the {@link Dumper} class
 * and creates the corresponding database state.
 * 
 * 
 * @see Dumper
 * @author spb
 *
 */
public class UnDumper extends DumpParser{
	
	
	DataBaseHandlerService serv;
	public UnDumper(AppContext conn){
		this(conn,false);
	}
	public UnDumper(AppContext conn,boolean map_ids){
		super(conn,map_ids);
		serv=conn.getService(DataBaseHandlerService.class);
	}
	
	/**
	 * @param rec
	 * @return int id to record in map.
	 * @throws ConsistencyError
	 * @throws DataFault
	 */
	public int processRecord(int parse_id,Record rec,boolean deleted) throws ConsistencyError, DataFault {
		if( deleted ) {
			rec.delete();
			return 0;
		}else {
			int new_id=0;
			if( new_id == 0 || rec.hasID()){
				rec.commit();
				new_id = rec.getID();
			}
			return new_id;
		}
	}
	public void processSpecification(String table_name,TableSpecification spec) throws DataFault{
		if( serv != null){
			table_name = getContext().getInitParameter("table."+table_name, table_name);
			serv.createTable(table_name, spec);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DumpParser#skipSpecification(java.lang.String)
	 */
	@Override
	public boolean skipSpecification(String table_name) {
		table_name = getContext().getInitParameter("table."+table_name, table_name);
		if( serv == null || serv.tableExists(table_name)){
			return true;
		}
		return false;
	}

	
	
}