//| Copyright - The University of Edinburgh 2015                            |
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
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

import static org.junit.Assert.assertFalse;

import org.junit.Ignore;

@Ignore
public class ExampleInterfaceTestImpl<X extends TargetProvider<ExampleInterface>> implements ExampleInterfaceTest<ExampleInterface> {

	private final X target_provider;
	public ExampleInterfaceTestImpl(X target ) {
		this.target_provider=target;
	}
	
	

	public void testDoThrow() throws Exception{
		ExampleInterface i = getTarget();
		i.doThrow(false);
		try{
			i.doThrow(true);
			assertFalse("should never get here",true);
		}catch(Exception e){
			//  ok expect this
		}
		
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.junit.TargetProvider#getTarget()
	 */
	@Override
	public ExampleInterface getTarget() {
		return target_provider.getTarget();
	}
}