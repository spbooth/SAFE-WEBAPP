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
package uk.ac.ed.epcc.webapp.forms.result;


/** All FormActions return a FormResult
 * FormResults are lightweight command classes that encode the response to the form
 * These need interpreted by the surrounding framework for example a text message is displayed 
 * differently in a web, gui or command line context.
 * Some form results only make sense in a restricted set of contexts. For example the web navigation results.
 * The {@link FormResultVisitor} can be extended to handle these additional result types.
 * Functionality can be promoted to additional contexts by replacing these with explicit FormResult sub-classes 
 * handled by the {@link FormResultVisitor}.
 * 
 * 
 * By using the visitor pattern we make it easier to identify what {@link FormResult} types need to be implemented in each context. 
 * 
 * 
 * @author spb
 *
 */
public interface FormResult {
	public void accept(FormResultVisitor vis) throws Exception;

}