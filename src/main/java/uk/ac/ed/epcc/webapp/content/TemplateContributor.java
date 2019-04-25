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
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Interface for {@link Composite}s that set parameters on a {@link TemplateFile}
 * @author spb
 *
 */
public interface TemplateContributor<BDO extends DataObject> {

	/** Set parameters in a template.
	 * 
	 * The prefix is to allow multiple objects of the same type to be added to
	 * a template under differnet names.
	 * 
	 * @param template {@link TemplateFile} to add content to.
	 * @param prefix String prefix to be pre-pended to property/region names
	 * @param target Object to take data from
	 */
	public void setTemplateContent(TemplateFile template, String prefix, BDO target);
}
