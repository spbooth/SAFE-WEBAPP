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
package uk.ac.ed.epcc.webapp.jdbc.exception;

/** Force a transaction retry/rollback 
 * 
 * Though this obviously is a recoverable exception we sub-class {@link Error}
 * to bypass normal error recovery and reach the level that created the original transaction
 * @author Stephen Booth
 *
 */
public class ForceRollBack extends Error {

	/**
	 * 
	 */
	public ForceRollBack() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ForceRollBack(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public ForceRollBack(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ForceRollBack(Throwable arg0) {
		super(arg0);
	}

}
