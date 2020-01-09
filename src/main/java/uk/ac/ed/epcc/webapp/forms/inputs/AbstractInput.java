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

/**
 * Superclass for general non-composite Inputs
 * 
 * 
 * 
 * @author spb
 * @param <V> Param of object we generate
 * 
 */
public abstract class AbstractInput<V> extends BaseInput<V> {
	V value;

	public AbstractInput() {
		super();
		value = null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getValue()
	 */
	@Override
	public final V getValue() {
		return value;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#setValue(java.lang.Object)
	 */
	@Override
	public final V setValue(V v) throws TypeError{
		V old = value;
		// in case we are called without generic checking but have a convert method.
		value = convert(v);
		return old;
	}
	
	@Override
	public final void setKey(String key) {
		super.setKey(key);
	}
}