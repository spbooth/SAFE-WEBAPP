//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.exceptions;

/** a {@link RuntimeException} thrown for an un-supported operation.
 * @author Stephen Booth
 *
 */
public class UnsupportedOperation extends RuntimeException {

	/**
	 * 
	 */
	public UnsupportedOperation() {
		
	}

	/**
	 * @param arg0
	 */
	public UnsupportedOperation(String arg0) {
		super(arg0);
		
	}

	/**
	 * @param arg0
	 */
	public UnsupportedOperation(Throwable arg0) {
		super(arg0);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public UnsupportedOperation(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public UnsupportedOperation(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		
	}

}
