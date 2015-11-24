// Copyright - The University of Edinburgh 2011
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
}
