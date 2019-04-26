//| Copyright - The University of Edinburgh 2018                            |
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/**
 * @author Stephen Booth
 *
 */
public class BoundedInputInterfaceTestImpl<T,I extends BoundedInput<T>,X extends BoundedInputDataProvider<T, I>> implements BoundedInputInterfaceTest<T,I,X>{

	/**
	 * @param target
	 */
	
	public BoundedInputInterfaceTestImpl(X target) {
		super();
		this.target = target;
	}

	private final X target;
	
	
    public void testMin() throws Exception {
		I input = target.getInput();
		T old = input.getMin();
		T prev = input.setMin(target.getLowBound());
		if( old == null ) {
			assertNull(prev);
		}else {
			assertEquals(old, prev);
		}
		assertEquals(target.getLowBound(),input.getMin());
		Set<T> good = new HashSet<>();
		good.addAll(target.getGoodData());
		good.addAll(target.getHighData());
		
		
		Set<T> bad = new HashSet<>();
		bad.addAll(target.getBadData());
		bad.addAll(target.getLowData());
		for(T dat : good){
    		input.setValue(dat);
    		checkValid(dat,true, input);
    	}
		for(T dat : bad){
    		input.setValue(dat);
    		checkValid(dat,false, input);
    	}
	}
	
	
    public void testMax() throws Exception {
		I input = target.getInput();
		T old = input.getMax();
		T prev = input.setMax(target.getHighBound());
		if( old == null ) {
			assertNull(prev);
		}else {
			assertEquals(old, prev);
		}
		assertEquals(target.getHighBound(),input.getMax());
		Set<T> good = new HashSet<>();
		good.addAll(target.getGoodData());
		good.addAll(target.getLowData());
		
		
		Set<T> bad = new HashSet<>();
		bad.addAll(target.getBadData());
		bad.addAll(target.getHighData());
		for(T dat : good){
    		input.setValue(dat);
    		checkValid(dat,true, input);
    	}
		for(T dat : bad){
    		input.setValue(dat);
    		checkValid(dat,false, input);
    	}
	}
	
    public void testBoth() throws Exception {
		I input = target.getInput();
		input.setMin(target.getLowBound());
		input.setMax(target.getHighBound());
		Set<T> good = new HashSet<>();
		good.addAll(target.getGoodData());
		
		
		
		Set<T> bad = new HashSet<>();
		bad.addAll(target.getBadData());
		bad.addAll(target.getLowData());
		bad.addAll(target.getHighData());
		for(T dat : good){
    		input.setValue(dat);
    		checkValid(dat,true, input);
    	}
		for(T dat : bad){
    		input.setValue(dat);
    		checkValid(dat,false, input);
    	}
	}
	
    public void testAll() throws Exception {
		I input = target.getInput();
		
		Set<T> good = new HashSet<>();
		good.addAll(target.getGoodData());
		good.addAll(target.getHighData());
		good.addAll(target.getLowData());
		
		Set<T> bad = new HashSet<>();
		bad.addAll(target.getBadData());
		
		for(T dat : good){
    		input.setValue(dat);
    		checkValid(dat,true, input);
    	}
		for(T dat : bad){
    		input.setValue(dat);
    		checkValid(dat,false, input);
    	}
	}
	public  void checkValid(T value,boolean expect, I i) throws FieldException  {
		
		
		try{
			i.validate();
			assertTrue("Exception validate passed for ["+value+"]",expect);
		}catch(FieldException e){
			assertFalse("Exception thrown for value "+value+" "+e.getMessage(), expect);
		}
	
   }
}