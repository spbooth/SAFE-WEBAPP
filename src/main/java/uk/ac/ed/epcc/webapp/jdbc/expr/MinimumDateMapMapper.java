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

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.NumberOp;
/** A {@link MapMapper} where the data field is Numerical that the minimum is selected from.
 * As the key is a {@link SQLValue} rather than a {@link SQLExpression} we may have multiple SQL rows mapping to the
 * same key so numerical results may have to be combined.
 * 
 * @author adrianj
 *
 * @param <K> key type
 */


public class MinimumDateMapMapper<K> extends MapMapper<K, Date> {

	public MinimumDateMapMapper(AppContext c, GroupingSQLValue<K> key, String key_name,SQLExpression<? extends Date> val, String value_name) throws InvalidKeyException {
		super(c, key, key_name);
		addMinDate(val, value_name);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.MapMapper#combine(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Date combine(Date a, Date b) {
		if( a == null ) {
			return b;
		}
		if( b == null ) {
			return a;
		}
		return a.before(b) ? a : b;		
	}
	

}