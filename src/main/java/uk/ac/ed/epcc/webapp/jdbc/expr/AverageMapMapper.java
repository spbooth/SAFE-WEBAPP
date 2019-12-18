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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AverageValue;
import uk.ac.ed.epcc.webapp.NumberOp;
/** A MapMapper where the data field is Numerical data averaged out.
 * 
 * This relies on the {@link AverageValue} type to allow partial averages to be combined.
 * 
 * @author adrianj
 *
 * @param <K> key type
 */


public class AverageMapMapper<K> extends MapMapper<K, Number> {

	public AverageMapMapper(AppContext c, GroupingSQLValue<K> key, String key_name,SQLExpression<? extends Number> val, String value_name) throws InvalidKeyException {
		super(c, key, key_name);
		addAverage(val, value_name);
	}

	@Override
	protected Number combine(Number a, Number b) {
		return NumberOp.average(a,b);
		
	}

}