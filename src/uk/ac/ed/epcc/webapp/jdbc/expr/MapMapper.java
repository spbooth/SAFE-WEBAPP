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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/** {@link SQLGroupMapper} to populate a Map from a SQL query where the key and data are single
 * target values.
 * 
 * @author spb
 * @param <K> type of key
 * @param <R> type of result
 *
 */


public class MapMapper<K,R> extends GeneralMapMapper<K,R>{
	private Class<? super K> key_type;
	public MapMapper(AppContext c,GroupingSQLValue<K> key,
			String key_name) throws InvalidKeyException {
		super(c);
		addKey(key,key_name);
		key_type = key.getTarget();
	}

	/** Simple constructor for a basic map between two values with no combine operation. 
	 * 
	 * @param c
	 * @param key
	 * @param key_name
	 * @param dat
	 * @param dat_name
	 * @throws InvalidKeyException 
	 */
	public MapMapper(AppContext c, GroupingSQLValue<K> key, String key_name, SQLValue<R> dat, String dat_name) throws InvalidKeyException{
		this(c,key,key_name);
		addClause(dat, dat_name);
	}
	@Override
	@SuppressWarnings("unchecked")
	protected K makeKey(ResultSet rs) throws DataException, SQLException{
	
		K key = (K) getTargetObject(0, rs);
		if( key == null ){
			// this can happen if the SQLValue can't map the DB value to the target space
		    // for strings we can define a default
			key=getNullKey();
		}
		return key;
	}
	@Override
	@SuppressWarnings("unchecked")
	protected R makeResult(ResultSet rs) throws DataException, SQLException{
		R val  = (R) getTargetObject(1, rs);
		return val;
	}


	@SuppressWarnings("unchecked")
	protected K getNullKey(){
		if( key_type==String.class){
			return (K) "Unknown";
		}
		return null;
	}
	

}