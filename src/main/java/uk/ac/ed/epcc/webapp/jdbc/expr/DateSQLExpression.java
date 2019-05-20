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


/** A Date valued {@link SQLExpression} where the underlying representation is really a numeric value.
 * 
 * This provides additional methods to access that numeric value. This should not be used for
 * expressions that are natively implemented as a Date.
 * 
 * @author spb
 *
 */
public interface DateSQLExpression extends SQLExpression<Date>{
	/** get an {@link SQLExpression} for the millisecond value of the  date
	 * 
	 * @return {@link SQLExpression} giving milliseconds since epoch
	 */
      public SQLExpression<? extends Number> getMillis();
      
    /** get an {@link SQLExpression} for the second value of the  date
  	 * 
  	 * @return {@link SQLExpression} giving seconds since epoch
  	 */
      public SQLExpression<? extends Number> getSeconds();
      
      /** Would this expression preferentially use seconds
       * 
       * @return boolean
       */
      public boolean preferSeconds();
}