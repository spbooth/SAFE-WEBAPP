// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

/** Input to select integers from a small  rage using  pull-down.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IntegerRangeInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")

public class IntegerRangeInput extends IntegerSetInput {
  public IntegerRangeInput(int min, int count){
	  super(makeSet(min, count));
  }
  private static Set<Integer> makeSet(int min, int count){
	  LinkedHashSet<Integer> result=new LinkedHashSet<Integer>();
	  for(int i=0;i<count;i++){
		  result.add(min+i);
	  }
	  return result;
  }
}