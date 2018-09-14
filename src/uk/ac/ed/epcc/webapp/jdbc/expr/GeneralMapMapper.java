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
import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
/** SQLGroupMapper to populate a Map from a SQL query.
 * 
 * @author spb
 *
 * @param <K>
 * @param <R>
 */
public abstract class GeneralMapMapper<K,R> extends SQLGroupMapper<Map<K,R>>  implements Contexed {
	
	public GeneralMapMapper(AppContext c){
		super(c);
	}
	public Map<K,R> makeDefault() {
		return new HashMap<K,R>();
	}

	
	public Map<K,R> makeObject(ResultSet rs) throws DataException {


		Map<K,R> m = new HashMap<K,R>();
		//Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		try{
			do{
				K key = makeKey(rs);
				R val = makeResult(rs);
				//log.debug("MapMapper key["+key_expr+"]="+key+" value["+target_expr+"]="+val);


				if( key != null && val != null ){
					m.put(key, combine(m.get(key),val));
				}

			}while(rs.next());
		}catch(SQLException e){
			throw new DataException("Error populating map",e);
		} catch (CombineException e) {
			throw new DataException("Illegal combine operation for "+getClass().getName(),e);
		}
		return m;
	}
	/** If more than one database value maps to the same key we may have to
	 * combine multiple results. This method defines how multiple results are combined. 
	 * 
	 * If combination is not possible this method needs to throw {@link CombineException}
	 * if the first parameter is non-null.
	 * 
	 * @param a previous value
	 * @param b new value
	 * @return combined value
	 */
	protected R combine(R a, R b) throws CombineException{
		if( a != null){
			throw new CombineException("No combine operation defined");
		}
		return b;
	}
	/** Make the key object from the ResultSet
	 * 
	 * @param rs
	 * @return
	 * @throws DataFault
	 */
	protected abstract K makeKey(ResultSet rs) throws DataException, SQLException;
	/** make the result object from the ResultSet
	 * 
	 * @param rs
	 * @return
	 * @throws DataFault
	 */
	protected abstract R makeResult(ResultSet rs) throws DataException, SQLException;
}