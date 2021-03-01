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
package uk.ac.ed.epcc.webapp.forms.inputs;

/** An UnmodifiableInput that wraps an existing input showing the value of the wrapped input 
 * as Unmodifiable text in the form. 
 * 
 * @author spb
 *
 * @param <V>
 */


public class LockedInput<V> extends WrappingInput<V> implements UnmodifiableInput ,  Input<V>{
  
    public LockedInput(Input<V> wrapped_input){
    	super(wrapped_input);
    }
   
	@Override
	public String getLabel() {
		return getNested().getPrettyString(getNested().getValue());
	}


	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitLockedInput(this);
	}

	@Override
	public void setKey(String key) {
		// non modifyable
	}

	@Override
	public V setValue(V v) {
		// non modifyable
		// for multi stage update forms we need to be able to call form setContents
		// without the value being updated.
		return getNested().getValue();
	}

}