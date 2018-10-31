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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

/** Input to select integers from a small  rage using  pull-down.
 * 
 * @author spb
 *
 */


public class IntegerRangeInput extends IntegerSetInput {
  public IntegerRangeInput(int min, int count){
	  super(makeSet(min, count));
  }
  private static Set<Integer> makeSet(int min, int count){
	  LinkedHashSet<Integer> result=new LinkedHashSet<>();
	  for(int i=0;i<count;i++){
		  result.add(min+i);
	  }
	  return result;
  }
}