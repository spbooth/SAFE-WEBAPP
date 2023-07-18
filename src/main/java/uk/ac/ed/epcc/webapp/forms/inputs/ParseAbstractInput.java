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
 * Superclass for inputs that might be implemented as a text box (ie ones that
 * implement ParseInput and LengthInput)
 * 
 * @author spb
 * @param <V> type of result object
 * 
 */
public abstract class ParseAbstractInput<V> extends AbstractInput<V> implements
		 LengthInput<V> {

	// input width
	int maxwid = 64;

	boolean force_single = false;

	

	
	@Override
	public int getBoxWidth() {
		return maxwid;
	}

	/**
	 * Get if the input should be forced to be a single line
	 * 
	 * @return boolean
	 */
	@Override
	public boolean getSingle() {
		return force_single;
	}

	

	/**
	 * set the input width for this parameter as a text box.
	 * 
	 * @param l
	 *            int maximum input width
	 */
	@Override
	public void setBoxWidth(int l) {
		maxwid = l;
	}

	/**
	 * Set if the input should be forced to be a single line
	 * 
	 * @param b
	 *            boolean
	 */
	public void setSingle(boolean b) {
		force_single = b;
	}


	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitLengthInput(this);
	}
}