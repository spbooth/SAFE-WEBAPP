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
/** functions that can be applied to arrays of {@link SQLExpression}s.
 * 
 * @author spb
 *
 */
public enum ArrayFunc {
	/** select the greatest value from the array
	 * 
	 */
    GREATEST{

		@Override
		public Object combine(Comparable a, Comparable b) {
			if( a == null ) return b;
			if( b == null ) return a;
			if( a.compareTo(b) < 0){
				return b;
			}else{
				return a;
			}
		}
	  
  },
    /** select the least value from the array.
     * 
     */
  LEAST{

	@Override
	public Object combine(Comparable a, Comparable b) {
		if( a == null ) return b;
		if( b == null ) return a;
		if( a.compareTo(b) < 0){
			return a;
		}else{
			return b;
		}
	}
	  
  };
  public abstract Object combine(Comparable a, Comparable b);

}