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
package uk.ac.ed.epcc.webapp.editors.mail;

import uk.ac.ed.epcc.webapp.AppContext;

/** Class to convert a Message into a quoted text string 
 * Nested messaged are quoted to the appropriate message depth.
 * non text parts are ommitted.
 * 
 * If the top level part passed to the visit class is not a message the top level will
 * be unquoted.
 * 
 * @author spb
 *
 */


public class QuoteVisitor extends PrefixVisitor<TextMailBuilder> {
	private TextMailBuilder sb;
	public QuoteVisitor(AppContext conn) {
		super(conn);
		sb = new TextMailBuilder();
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	@Override
	protected TextMailBuilder getMailBuilder() {
		return sb;
	}




}