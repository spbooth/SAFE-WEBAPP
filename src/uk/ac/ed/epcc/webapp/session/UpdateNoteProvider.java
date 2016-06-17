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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Interface to allow {@link Composite}s to add notes to the update form.
 * @author spb
 *
 */

public interface UpdateNoteProvider {
	/** add Notes to be included in a signup/update form.
	 * This is included within the block element above the
	 * form.
	 * 
	 * @param cb
	 * @return
	 */
	public <CB extends ContentBuilder> CB addUpdateNotes(CB cb);
}