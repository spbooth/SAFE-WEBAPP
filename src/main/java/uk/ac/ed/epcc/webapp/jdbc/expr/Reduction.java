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
 
 * @author spb
 *
 */
public enum Reduction {
  /** values are summed
   * 
   */
  SUM(Operator.ADD,false),
  /** values are averaged
   * 
   */
  AVG(Operator.AVG,true),
  /** median value of values is calcualted
   * 
   */
  MEDIAN(Operator.MEDIAN,true),
  /** minimum value taken
   * 
   */
  MIN(Operator.MIN,false),
  /** maximum value taken.
   * 
   */
  MAX(Operator.MAX,false),
  /** The INDEX reduction is equivalent to the SQL GROUP BY CLAUSE, when combined with other
 * Reductions it requests that multiple results should be returned, with reductions only happening
 * across sets of records where the INDEX property is the same.
   * 
   */
  INDEX(Operator.MERGE,false),
  /** SELECT just selects a value without adding to the SQL GROUP BY. The expression is assumed to be
 * derivable from the INDEXs or the same for all records. 
   * 
   */
  SELECT(Operator.MERGE,false),
  /** Count distinct values. This always generates a number whatever is being reduced
   * 
   */
  DISTINCT(Operator.ADD,true);
  
  private final Operator op;
  private final boolean custom_number;
  private Reduction(Operator o, boolean custom){
	  this.op=o;
	  this.custom_number=custom;
  }
  /** get a {@link Operator} suitable for combing partial results.
   * 
   * @return {@link Operator}
   */
  public Operator operator(){
	  return op;
  }

  /** Can these reductions reductions be implemented by a custom number type
   * 
   * @return
   */
  public boolean customNumber() {
	  return custom_number;
  }
}