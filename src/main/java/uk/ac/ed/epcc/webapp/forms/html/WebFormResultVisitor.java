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
package uk.ac.ed.epcc.webapp.forms.html;

import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;
/** Additional FormResults that only make sense in a web deployment.
 * 
 * @author spb
 *
 */
public interface WebFormResultVisitor extends FormResultVisitor {
	public void visitForwardResult(ForwardResult res) throws Exception;
	public void visitRedirectResult(RedirectResult res) throws Exception;
	public void visitErrorFormResult(ErrorFormResult res)throws Exception;
	public void visitExternalRedirectResult(ExternalRedirectResult res) throws Exception;
}