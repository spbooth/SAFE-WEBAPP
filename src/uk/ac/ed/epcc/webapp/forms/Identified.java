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
package uk.ac.ed.epcc.webapp.forms;

public interface Identified {

	/**
	 * Advisory maximum length for the Identifier string.
	 * 
	 */
	public static final int MAX_IDENTIFIER = 64;

	/**
	 * produce a unique identifying string for this object for use in forms and
	 * pull-down menus and charts. The string should be kept shorter than
	 * MAX_IDENTIFIER if at all possible. This defaults to the ID number for the
	 * object but classes should override this with something more sensible.
	 * 
	 * @return String
	 */
	public abstract String getIdentifier();
	
	/** Generate Identifier with advisory max length.
	 * 
	 * @param max_length advisory max length
	 * @return String identifier
	 */
	public abstract String getIdentifier(int max_length);

}