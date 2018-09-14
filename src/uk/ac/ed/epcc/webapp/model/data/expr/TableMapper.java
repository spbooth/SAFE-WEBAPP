//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.expr;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLGroupMapper;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLValue;
/** Creates a Table from a SQL GROUP BY query with a single key.
 * The key expression is used to GROUP the SQL results and a Mapper is used to convert the
 * expression into the table key. 
 * @author spb
 *
 */


public class TableMapper extends SQLGroupMapper<Table> {
 
    Mapper m=null;
    String key_name=null;
    /** Make a TableMapper
     * 
     * @param m   Mapper to convert key expression into table key
     * @param key field/expression  to use for key
     * @param key_name  name of key expression (can be null if key is a field name)
     */
    public TableMapper(AppContext conn,Mapper m,SQLValue key, String key_name){
        super(conn);
        setUseAlias(true);
    	this.m = m;
    	this.key_name=key_name;
    	addKey(key, key_name);
    }
  
	public Table makeObject(ResultSet rs) throws DataException, SQLException {
		Table t = new Table();
		// is we have a name for the key set it for the table
		if( key_name != null ){
			t.setKeyName(key_name);
		}
		
			ResultSetMetaData meta_data = rs.getMetaData();
			int md_columns = meta_data.getColumnCount();
			if( rs.isAfterLast()){
				return t;
			}
			if( rs.isBeforeFirst()){
				if( ! rs.next()){
					return t;
				}
			}
			do{
				
				Object row_key = getTargetObject(0,rs);
				if( m != null ){
					row_key = m.map(row_key);
				}
				for (int i = 2; i <= md_columns; i++) {
					// this assumes that C is a String !!!!
					String col_name = meta_data.getColumnLabel(i);
					Object value = getTargetObject(i-1,rs);
					if (value instanceof Number) {
						t.addNumber(col_name, row_key, (Number) value);
					} else {
						t.put(col_name, row_key, value);
					}

				}

			}while( rs.next());
		
		
		return t;
	}

	public Table makeDefault() {
		return new Table();
	}


}