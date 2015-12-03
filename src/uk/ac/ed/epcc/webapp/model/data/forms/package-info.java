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
/** classes to support the default create/update transitions.
 * <p>
The <code> FormUpdate</code> interface supports selecting and editing existing objects.
<code>FormCreator</code> supports creating objects from a form. This functionality is now mapped onto
the transition mechanism via a standard provider. The same operations can be performed through custom providers using sub-classes of {@link uk.ac.ed.epcc.webapp.model.data.forms.CreateTransition} or
{@link uk.ac.ed.epcc.webapp.model.data.forms.UpdateTransition}
</p>
 * 
 */
package uk.ac.ed.epcc.webapp.model.data.forms;