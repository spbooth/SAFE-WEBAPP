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
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** Exception that indicates a filter cannot be converted to an {@link AcceptFilter}
 * 
 * @author spb
 *
 */


public class NoAcceptFilterException extends Exception {

	public NoAcceptFilterException() {
		super();
	}

	public NoAcceptFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoAcceptFilterException(String message) {
		super(message);
	}

	public NoAcceptFilterException(Throwable cause) {
		super(cause);
	}

}