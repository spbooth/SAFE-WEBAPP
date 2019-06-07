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

import uk.ac.ed.epcc.webapp.content.Operator;

/** Reduction operators.
 * <p>
 * The INDEX reduction is equivalent to the SQL GROUP BY CLAUSE, when combined with other
 * Reductions it requests that multiple results should be returned, with reductions only happending
 * across sets of records where the INDEX property is the same.
 * </p>
 * <p>
 * AVG is a mean value except when doing overlap mapping when it becomes a time average
 * </p>
 * <p>
 * 
 * </p>
 * @author spb
 *
 */
public enum Reduction {
  /** values are summed
   * 
   */
  SUM(Operator.ADD),
  /** values are averaged
   * 
   */
  AVG(Operator.AVG),
  /** median value of values is calcualted
   * 
   */
  MEDIAN(Operator.MEDIAN),
  /** minimum value taken
   * 
   */
  MIN(Operator.MIN),
  /** maximum value taken.
   * 
   */
  MAX(Operator.MAX),
  /** The INDEX reduction is equivalent to the SQL GROUP BY CLAUSE, when combined with other
 * Reductions it requests that multiple results should be returned, with reductions only happening
 * across sets of records where the INDEX property is the same.
   * 
   */
  INDEX(Operator.MERGE),
  /** SELECT just selects a value without adding to the SQL GROUP BY. The expression is assumed to be
 * derivable from the INDEXs or the same for all records. 
   * 
   */
  SELECT(Operator.MERGE),
  /** Count distinct values. This always generates a number whatever is being reduced
   * 
   */
  DISTINCT(Operator.ADD);
  
  private final Operator op;
  private Reduction(Operator o){
	  this.op=o;
  }
  /** get a {@link Operator} suitable for combing partial results.
   * 
   * @return {@link Operator}
   */
  public Operator operator(){
	  return op;
  }
}