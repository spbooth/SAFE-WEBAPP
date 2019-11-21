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
package uk.ac.ed.epcc.webapp.forms.action;

/** Information for a confirm message
 * @author Stephen Booth
 *
 */
public class ConfirmMessage {
	/**
	 * @param message
	 * @param args
	 */
	public ConfirmMessage(String message, String[] args) {
		super();
		this.message = message;
		this.args = args;
	}
	final String message;
	final String args[];
	
	public String getMessage() {
		return message;
	}
	
	public String[] getArgs() {
		return args;
	}
}
