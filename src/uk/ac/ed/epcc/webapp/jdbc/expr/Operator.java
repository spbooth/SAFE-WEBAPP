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

import uk.ac.ed.epcc.webapp.NumberOp;

/** A supported binary operators on Numbers in an expression
 * 
 * @author spb
 *
 */
public enum Operator {

	
  ADD{
	  @Override
	public Number operate(Number a,Number b){
		  return NumberOp.add(a,b);
	  }
	  @Override
	  public String text(){ return "+"; }
	  @Override
	  public boolean commutes(){ return true;}
	@Override
	public boolean associates(Operator op) {
		return op == ADD || op == SUB;
	}
  },
  SUB{
	  @Override
	public Number operate(Number a,Number b){
		  return NumberOp.sub(a, b);
	  }
	  @Override
	  public String text(){ return "-"; }
  },
  MUL{
	
	@Override
	public Number operate(Number a,Number b){
		  return NumberOp.mult(a, b);
	  }
	  @Override
	  public String text() { return "*"; }
	  @Override
	  public boolean commutes(){ return true;}
	  @Override
		public boolean associates(Operator op) {
			return op == MUL || op == DIV;
		}
	  @Override
		public boolean leftDistributive(Operator op) {
			return op == ADD || op == SUB;
		}
	  @Override
		public boolean rightDistributive(Operator op) {
			return op == ADD || op == SUB;
		}
  },
  DIV{  
	  @Override
	public Number operate(Number a,Number b){
		  if( a == null || b == null ){
			  return null;
		  }
		  // In general division gives a fraction not programmer rounding.
		  // but don't promote to double unless we have too.
		  double divide = a.doubleValue()/b.doubleValue();
		  Number n = NumberOp.div(a, b);
		  if( n.doubleValue() == divide){
			  // no loss
			  return n;
		  }
		  return Double.valueOf(divide);
	  }
	  @Override
	  public String text() { return "/"; }
	  @Override
		public boolean rightDistributive(Operator op) {
			return op == ADD || op == SUB;
		}
  };
  abstract public Number operate(Number a,Number b);
  abstract public String text();
  /** returns true iff:
   * 
   * A this ( B op C) 
   * is the same as
   *  (A this B) op C
   * 
   * @param op
   * @return
   */
  public boolean associates(Operator op){
	  return false;
  }
  
  /** returns true iff:
   * 
   *  A this ( B op C )
   *  is the same as
   *  (A this B) op ( A this C)
   *  
   *   
   * 
   * @param op
   * @return
   */
  public boolean leftDistributive( Operator op){
	  return false;
  }
  /** returns true iff:
   * 
   *  ( A op B ) this C
   *  is the same as
   *  (A this C) op ( B this C)
   *  
   * @param op
   * @return
   */
  public boolean rightDistributive( Operator op){
	  return false;
  }
  /** do you get the same result if the order of the arguments is swapped.
 * @return boolean true if operator commutes.
   * 
   */
  public boolean commutes(){
		return false;
  }
}